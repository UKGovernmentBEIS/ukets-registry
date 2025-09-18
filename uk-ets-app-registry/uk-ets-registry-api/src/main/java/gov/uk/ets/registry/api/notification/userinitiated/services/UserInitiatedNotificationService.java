package gov.uk.ets.registry.api.notification.userinitiated.services;

import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.notification.userinitiated.NotificationDefinitionDTO;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.SelectionCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.DashboardNotificationDTO;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationDTO;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationMapper;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchResult;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchCriteria;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Sheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static gov.uk.ets.registry.api.file.upload.services.FileUploadService.ERROR_WHILE_PROCESSING_THE_FILE;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserProjection;
import static java.util.Optional.ofNullable;


@Service
@RequiredArgsConstructor
public class UserInitiatedNotificationService {    
    private static final List<UserStatus> excludedUserStatuses =
        List.of(UserStatus.SUSPENDED, UserStatus.DEACTIVATED, UserStatus.DEACTIVATION_PENDING);
    /**
     * The class attribute is needed because of the way that quill-js handles nested lists
     */
    private static final Safelist CONTENT_SAFE_LIST = Safelist.basic()
        .addAttributes(":all", "class")
        .addTags("div");
    private static final Safelist SUBJECT_SAFE_LIST = Safelist.none();
    private static final Document.OutputSettings settings = new Document.OutputSettings();

    static {
        // This is needed to avoid the new line characters added by JSoup when pretty-printing.
        settings.prettyPrint(false);
    }

    private final NotificationRepository notificationRepository;
    private final NotificationDefinitionRepository notificationDefinitionRepository;
    private final NotificationSchedulingRepository schedulingRepository;
    private final UserService userService;
    private final NotificationMapper mapper;
    private final UploadedFilesRepository uploadedFilesRepository;
    private final UserAdministrationService userAdministrationService;

    @Value("${notification.inactive-users.days}")
    private Long INACTIVE_USER_DAYS;
    
    /**
     * Creates a new notification.c
     */
    @Transactional
    public Notification createNotification(NotificationDTO request) {

        UploadedFile uploadedFile = null;
        if (request.getUploadedFileId() != null) {
            uploadedFile = uploadedFilesRepository.findById(request.getUploadedFileId()).orElseThrow(
                () -> new FileUploadException(ERROR_WHILE_PROCESSING_THE_FILE));
            uploadedFile.setFileStatus(FileStatus.SUBMITTED);
            uploadedFilesRepository.save(uploadedFile);
        }

        LocalDateTime now = LocalDateTime.now().minusSeconds(5);
        if(request.getActivationDetails() != null && request.getActivationDetails().getIsScheduledTimeNow()){
            request.getActivationDetails().setScheduledTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        }

        final var sanitizedRequest = sanitizeContent(request);

        NotificationDefinition definition = notificationDefinitionRepository.findByType(sanitizedRequest.getType())
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Notification definition not found for type: %s", sanitizedRequest.getType()))
            );
        // for non-adhoc notifications only one should be active at any time:
        if (sanitizedRequest.getType() != NotificationType.AD_HOC &&
            sanitizedRequest.getType() != NotificationType.AD_HOC_EMAIL &&
            notificationRepository.findActiveNotificationByType(sanitizedRequest.getType()).isPresent()) {
            throw new BusinessRuleErrorException(
                ErrorBody
                    .from(String.format("Only one active notification for type %s might exist at the same time.", sanitizedRequest.getType())));
        }
        // scheduled date-time cannot be in the past:
        if (sanitizedRequest.getActivationDetails().getScheduledDateTime()
            .isBefore(now)) {
            throw new BusinessRuleErrorException(
                ErrorBody
                    .from("Scheduled date/time cannot be earlier than now.")
            );
        }

        User currentUser = userService.getCurrentUser();

        Notification notification = mapper.toEntity(sanitizedRequest, definition, currentUser.getUrid(), uploadedFile);

        Notification savedNotification = notificationRepository.save(notification);

        updateNotificationDefinition(savedNotification, definition);

        return savedNotification;
    }

    @Transactional
    public Notification updateNotification(Long id, NotificationDTO request) {
        LocalDateTime now = LocalDateTime.now();
        if(request.getActivationDetails().getIsScheduledTimeNow()){
            request.getActivationDetails().setScheduledTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        }
        final var sanitizedRequest = sanitizeContent(request);

        Notification notification = notificationRepository.getByIdWithDefinition(id).orElseThrow(
            () -> new IllegalArgumentException(
                String.format("Notification  not found for id: %s", id))
        );

        // Pre-requisite: Expired notifications cannot be updated.
        if (notification.getStatus().equals(NotificationStatus.EXPIRED)) {
            throw new BusinessRuleErrorException(
                ErrorBody
                    .from("You cannot update an expired notification.")
            );
        }

        // Pre-requisite: Cancelled notifications cannot be updated.
        if (notification.getStatus().equals(NotificationStatus.CANCELLED)) {
            throw new BusinessRuleErrorException(
                    ErrorBody
                            .from("You cannot update a cancelled notification.")
            );
        }

        LocalDateTime requestedScheduledDateTime = sanitizedRequest.getActivationDetails().getScheduledDateTime();
        if (notification.getStatus().equals(NotificationStatus.ACTIVE)) {
            // BR: After becoming active, the scheduled date/time cannot change.
            if (!notification.getSchedule().getStartDateTime().equals(requestedScheduledDateTime)) {
                throw new BusinessRuleErrorException(
                    ErrorBody
                        .from("After becoming active, the scheduled date/time cannot change.")
                );
            }
        } else {
            // BR: Scheduled date/time cannot be earlier than now (for non active notifications!).
            if (requestedScheduledDateTime.isBefore(now)) {
                throw new BusinessRuleErrorException(
                    ErrorBody
                        .from("Scheduled date/time cannot be earlier than now.")
                );
            }
        }

        User currentUser = userService.getCurrentUser();

        notification = mapper.toUpdatedEntity(notification, sanitizedRequest, currentUser.getUrid());

        updateNotificationDefinition(notification, notification.getDefinition());

        return notification;
    }

    @Transactional
    public void cancelNotification(Long id) {
        Notification notification = notificationRepository.getByIdWithDefinition(id).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("Notification  not found for id: %s", id))
        );

        // Pre-requisite: Expired notifications cannot be cancelled
        if (notification.getStatus().equals(NotificationStatus.EXPIRED)) {
            throw new BusinessRuleErrorException(
                    ErrorBody
                            .from("You cannot cancel an expired notification.")
            );
        }

        // Pre-requisite: Cancelled notifications cannot be re-canclled.
        if (notification.getStatus().equals(NotificationStatus.CANCELLED)) {
            throw new BusinessRuleErrorException(
                    ErrorBody
                            .from("You cannot cancel a cancelled notification.")
            );
        }
        notification.setStatus(NotificationStatus.CANCELLED);
        notificationRepository.save(notification);
    }

    public NotificationDTO retrieveNotificationById(Long id) {
        Notification notification =
            notificationRepository.getByIdWithDefinition(id).orElseThrow(() -> new IllegalArgumentException(
                String.format("Notification not found for id: %s", id))
            );
        // when updating an existing notification the notification definition is not retrieved (like when creating
        // a new one) so we need to fetch the tentative recipients here too.
        Integer recipients = Optional.ofNullable(notification.getUploadedFile())
            .map(this::calculateNumberOfRecipients)
            .orElseGet(() -> calculateNumberOfRecipients(notification.getDefinition().getType()));
        return sanitizeContent(mapper.toDto(notification, recipients));
    }

    public NotificationDefinitionDTO retrieveDefinitionByType(NotificationType type) {
        NotificationDefinitionDTO definition = notificationDefinitionRepository.findDtoByType(type)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Notification definition not found for type: %s", type)));
        definition.setTentativeRecipients(calculateNumberOfRecipients(type));
        return sanitizeContent(definition);
    }

    public Page<NotificationSearchResult> search(NotificationSearchCriteria criteria, Pageable pageable) {
        return notificationRepository.search(criteria, pageable);
    }

    public List<DashboardNotificationDTO> retrieveDashboardNotifications() {
        UserStatus userStatus = userService.getCurrentUser().getState();
        // TODO should we throw an error here instead of returning an empty list? should this check be a BR?
        if (excludedUserStatuses.contains(userStatus)) {
            return new ArrayList<>();
        }
        return notificationRepository.findDashBoardNotifications().stream()
            .peek(notification ->
                ofNullable(notification.getContent())
                    .ifPresent(
                        content -> notification.setContent(Jsoup.clean(content, "", CONTENT_SAFE_LIST, settings)))
            )
            .collect(Collectors.toList());
    }

    public Integer calculateNumberOfRecipients(NotificationType type) {
        if (type.equals(NotificationType.AD_HOC) || type.equals(NotificationType.AD_HOC_EMAIL)) {
            return 0;
        }
        NotificationDefinition definition = notificationDefinitionRepository.findByType(type)
            .orElseThrow(() -> new IllegalStateException(
                String.format("Definition with type %s not found", type)
            ));
        SelectionCriteria selectionCriteria = definition.getSelectionCriteria();

        if(type.equals(NotificationType.USER_INACTIVITY)){
            List<String> userIdList = this.getInactiveUsers();  
            List<BaseNotificationParameters> basicNotificationParameters =
                schedulingRepository.getUserInactivityNotificationParameters(
                        userIdList, 
                        selectionCriteria.getAccountStatuses(), 
                        selectionCriteria.getUserStatuses(), 
                        selectionCriteria.getAccountAccessStates());       
                      
            return basicNotificationParameters.size();            
        } else {
            List<BaseNotificationParameters> basicNotificationParameters =
                schedulingRepository.getBasicNotificationParameters(
                    selectionCriteria.getAccountTypes(),
                    selectionCriteria.getAccountStatuses(),
                    selectionCriteria.getUserStatuses(),
                    selectionCriteria.getAccountAccessStates(),
                    selectionCriteria.getComplianceStatuses()
                );
            return basicNotificationParameters.size();
        }
    }

    public Integer calculateNumberOfRecipients(UploadedFile uploadedFile) {

        try (InputStream multiPartInputStream = new ByteArrayInputStream(uploadedFile.getFileData())) {
            ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream);
            Sheet sheet = wb.getFirstSheet();
            return sheet.read().size() - 1; // Ignore headers
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to process file.");
        }
    }
	
    public List<String> getInactiveUsers(){
        KeycloakUserSearchCriteria criteria = new KeycloakUserSearchCriteria();
        criteria.setLastSignInTo(LocalDateTime.now().minusDays(INACTIVE_USER_DAYS).format(DateTimeFormatter.ISO_DATE));
        criteria.setPageSize(100L);
        criteria.setSortDirection("DESC");
        
        List<String> allUsers = new ArrayList<>();
        List<String> pageUsers;
        int page = 0;
        do {
            criteria.setPage(page);
            pageUsers = userAdministrationService.search(criteria, true)
                .getItems()
                .stream()
                .map(KeycloakUserProjection::getUserId)
                .toList();
            allUsers.addAll(pageUsers);
            page++;
        } while (!pageUsers.isEmpty());        
        
        return allUsers;
    }    
    
    /**
     * Updates notification definition, only for compliance notifications.
     */
    private void updateNotificationDefinition(Notification notification, NotificationDefinition definition) {
        if (definition.getType() != NotificationType.AD_HOC && definition.getType() != NotificationType.USER_INACTIVITY &&
            definition.getType() != NotificationType.AD_HOC_EMAIL) {
            definition.setShortText(notification.getShortText());
            definition.setLongText(notification.getLongText());
        }
    }

    /**
     * Sanitizes the content and subject fields of the Notification DTO
     */
    private NotificationDTO sanitizeContent(final NotificationDTO notification) {

        ofNullable(notification)
            .flatMap(n -> ofNullable(n.getContentDetails()))
            .ifPresent(details -> {
                ofNullable(details.getContent())
                    .ifPresent(c -> details.setContent(Jsoup.clean(c, "", CONTENT_SAFE_LIST, settings)));
                ofNullable(details.getSubject())
                    .ifPresent(s -> details.setSubject(Jsoup.clean(s, SUBJECT_SAFE_LIST)));
            });
        return notification;
    }

    /**
     * Sanitizes the content and subject fields of the NotificationDefinitionDTO
     */
    private NotificationDefinitionDTO sanitizeContent(NotificationDefinitionDTO definition) {
        if (definition != null) {
            ofNullable(definition.getLongText())
                .ifPresent(text -> definition.setLongText(Jsoup.clean(text, "", CONTENT_SAFE_LIST, settings)));
            ofNullable(definition.getShortText())
                .ifPresent(text -> definition.setShortText(Jsoup.clean(text, SUBJECT_SAFE_LIST)));
        }
        return definition;
    }
}
