package gov.uk.ets.registry.api.notification.userinitiated;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.SelectionCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.UserInitiatedNotificationService;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.AdHocActivationDetails;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.ComplianceActivationDetails;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.ContentDetails;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationDTO;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationMapper;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;
import org.apache.sis.internal.util.StandardDateFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserInitiatedNotificationServiceTest {

    private static final NotificationType TEST_COMPLIANCE_NOTIFICATION_TYPE =
        NotificationType.EMISSIONS_MISSING_FOR_AOHA;
    private static final String TEST_URID = "UK1212121212";
    public static final String TEST_SUBJECT = "test subject";
    public static final String TEST_CONTENT = "test content";
    private static final Long TEST_NOTIFICATION_ID = 1L;

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationDefinitionRepository notificationDefinitionRepository;
    @Mock
    private NotificationSchedulingRepository schedulingRepository;
    @Mock
    private UserService userService;

    NotificationMapper mapper;

    private UserInitiatedNotificationService cut;

    private NotificationDefinition complianceNotificationDefinition;
    private NotificationDTO complianceNotificationRequest;

    private NotificationDefinition adHocDefinition;
    private NotificationDTO adHocNotificationRequest;

    @BeforeEach
    public void setUp() {

        // this is a weird corner case where we need a mockito spy with mocked dependencies. Using @Spy and @InjectMocks
        // at the same time did not work so we need to revert ba k to manual instantiation.
        mapper = spy(new NotificationMapper(userService));

        cut = new UserInitiatedNotificationService(
            notificationRepository, notificationDefinitionRepository, schedulingRepository, userService, mapper
        );

        User user = new User();
        user.setUrid(TEST_URID);
        user.setDisclosedName("John Doe");
        user.setState(UserStatus.ENROLLED);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userService.getUserByUrid(TEST_URID)).thenReturn(user);

        complianceNotificationDefinition = NotificationDefinition.builder()
            .type(TEST_COMPLIANCE_NOTIFICATION_TYPE)
            .selectionCriteria(SelectionCriteria.builder().build())
            .build();

        ComplianceActivationDetails complianceNotificationActivationDetails = ComplianceActivationDetails.builder()
            .scheduledDate(LocalDate.now())
            .scheduledTime(LocalTime.now())
            .scheduledTimeNow(Boolean.FALSE)
            .build();
        ContentDetails contentDetails = ContentDetails.builder()
            .subject(TEST_SUBJECT)
            .content(TEST_CONTENT)
            .build();
        complianceNotificationRequest = NotificationDTO.builder()
            .type(TEST_COMPLIANCE_NOTIFICATION_TYPE)
            .activationDetails(complianceNotificationActivationDetails)
            .contentDetails(contentDetails)
            .build();

        adHocDefinition = NotificationDefinition.builder()
            .type(NotificationType.AD_HOC)
            .selectionCriteria(SelectionCriteria.builder().build())
            .build();

        AdHocActivationDetails adHocNotificationActivationDetails = AdHocActivationDetails.builder()
            .scheduledDate(LocalDate.now())
            .scheduledTime(LocalTime.now())
            .build();
        adHocNotificationRequest = NotificationDTO.builder()
            .type(NotificationType.AD_HOC)
            .activationDetails(adHocNotificationActivationDetails)
            .contentDetails(contentDetails)
            .build();

        when(notificationRepository.save(any())).then(returnsFirstArg());
    }

    @Test
    void shouldFailIfActiveNotificationOfSameTypeExists() {
        when(notificationDefinitionRepository.findByType(TEST_COMPLIANCE_NOTIFICATION_TYPE)).thenReturn(
            Optional.of(new NotificationDefinition()));
        when(notificationRepository.findActiveNotificationByType(TEST_COMPLIANCE_NOTIFICATION_TYPE)).thenReturn(
            Optional.of(new Notification()));

        NotificationDTO notificationRequest = NotificationDTO.builder()
            .type(TEST_COMPLIANCE_NOTIFICATION_TYPE)
            .build();

        assertThrows(BusinessRuleErrorException.class, () -> cut.createNotification(notificationRequest));
    }

    @Test
    void shouldFailIfNotificationDefinitionNotFound() {
        when(notificationDefinitionRepository.findByType(TEST_COMPLIANCE_NOTIFICATION_TYPE)).thenReturn(
            Optional.empty());

        NotificationDTO notificationRequest = NotificationDTO.builder()
            .type(TEST_COMPLIANCE_NOTIFICATION_TYPE)
            .build();

        assertThrows(IllegalArgumentException.class, () -> cut.createNotification(notificationRequest));
    }

    @Test
    void shouldCreateComplianceNotificationInStatusActive() {

        when(notificationDefinitionRepository.findByType(TEST_COMPLIANCE_NOTIFICATION_TYPE))
            .thenReturn(Optional.of(complianceNotificationDefinition));

        Notification notification = cut.createNotification(complianceNotificationRequest);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.ACTIVE);
        assertThat(notification.getDefinition()).isEqualTo(complianceNotificationDefinition);
        assertThat(notification.getShortText()).isEqualTo(TEST_SUBJECT);
        assertThat(notification.getLongText()).isEqualTo(TEST_CONTENT);
        assertThat(notification.getCreator()).isEqualTo(TEST_URID);
        assertThat(notification.getSchedule().getStartDateTime()).isEqualTo(
            complianceNotificationRequest.getActivationDetails().getScheduledDateTime());

        assertThat(notification.getLastUpdated()).isBeforeOrEqualTo(
            LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC)));
        assertThat(notification.getUpdatedBy()).isEqualTo(TEST_URID);
    }

    @Test
    void shouldCreateComplianceNotificationInStatusPending() {

        when(notificationDefinitionRepository.findByType(TEST_COMPLIANCE_NOTIFICATION_TYPE))
            .thenReturn(Optional.of(complianceNotificationDefinition));

        complianceNotificationRequest.getActivationDetails().setScheduledTime(LocalTime.now().plusMinutes(1L));
        Notification notification = cut.createNotification(complianceNotificationRequest);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
    }

    @Test
    void shouldFailCreateComplianceNotificationInThePast() {

        when(notificationDefinitionRepository.findByType(TEST_COMPLIANCE_NOTIFICATION_TYPE))
            .thenReturn(Optional.of(complianceNotificationDefinition));

        complianceNotificationRequest.getActivationDetails()
            .setScheduledDate(LocalDate.now(ZoneId.of(StandardDateFormat.UTC)).minusDays(1));
        ComplianceActivationDetails activationDetails =
            (ComplianceActivationDetails) complianceNotificationRequest.getActivationDetails();
        activationDetails.setHasRecurrence(true);
        activationDetails.setExpirationDate(LocalDate.now(ZoneId.of(StandardDateFormat.UTC)));
        activationDetails.setRecurrenceDays(2);

        assertThrows(BusinessRuleErrorException.class, () -> cut.createNotification(complianceNotificationRequest));
    }

    @Test
    void shouldCreateAdHocNotificationInStatusWithExpirationInStatusActive() {
        when(notificationDefinitionRepository.findByType(NotificationType.AD_HOC))
            .thenReturn(Optional.of(adHocDefinition));

        AdHocActivationDetails activationDetails =
            (AdHocActivationDetails) adHocNotificationRequest.getActivationDetails();
        activationDetails.setHasExpirationDateSection(true);
        activationDetails.setScheduledTimeNow(Boolean.FALSE);
        activationDetails.setExpirationDate(LocalDate.now());
        activationDetails.setExpirationTime(LocalTime.now().plusMinutes(1L));

        Notification notification = cut.createNotification(adHocNotificationRequest);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.ACTIVE);
    }

    @Test
    void shouldCreateAdHocNotificationInStatusWithExpirationInStatusPending() {
        when(notificationDefinitionRepository.findByType(NotificationType.AD_HOC))
            .thenReturn(Optional.of(adHocDefinition));

        AdHocActivationDetails activationDetails =
            (AdHocActivationDetails) adHocNotificationRequest.getActivationDetails();
        activationDetails.setHasExpirationDateSection(true);
        activationDetails.setExpirationDate(LocalDate.now());
        activationDetails.setScheduledTime(LocalTime.now().plusMinutes(1L));
        activationDetails.setExpirationTime(LocalTime.now().plusMinutes(2L));
        activationDetails.setScheduledTimeNow(Boolean.FALSE);

        Notification notification = cut.createNotification(adHocNotificationRequest);

        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
    }

    @Test
    public void shouldUpdateNotificationScheduleButNotContentForAdHoc() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
        LocalDateTime updatedStart = now.plusDays(1);
        LocalDateTime updatedEnd = now.plusDays(2);

        Notification notification = Notification.builder()
            .id(1L)
            .schedule(NotificationSchedule.builder()
                .startDateTime(now.plusMinutes(60))
                .endDateTime(now.plusDays(1))
                .build())
            .definition(adHocDefinition)
            .status(NotificationStatus.PENDING)
            .build();


        when(notificationRepository.getByIdWithDefinition(TEST_NOTIFICATION_ID)).thenReturn(Optional.of(notification));

        NotificationDTO request = NotificationDTO.builder()
            .type(NotificationType.AD_HOC)
            .activationDetails(AdHocActivationDetails.builder()
                .scheduledDate(updatedStart.toLocalDate())
                .scheduledTime(updatedStart.toLocalTime())
                .scheduledTimeNow(Boolean.FALSE)
                .expirationDate(updatedEnd.toLocalDate())
                .expirationTime(updatedEnd.toLocalTime())
                .hasExpirationDateSection(true)
                .build())
            .contentDetails(ContentDetails.builder()
                .subject("new subject")
                .content("new content")
                .build())
            .build();

        Notification updatedNotification = cut.updateNotification(TEST_NOTIFICATION_ID, request);

        NotificationSchedule schedule = updatedNotification.getSchedule();

        assertThat(schedule.getStartDateTime()).isEqualTo(updatedStart);
        assertThat(schedule.getEndDateTime()).isEqualTo(updatedEnd);

        assertThat(updatedNotification.getDefinition().getShortText()).isNotEqualTo("new subject");
        assertThat(updatedNotification.getDefinition().getLongText()).isNotEqualTo("new content");

        assertThat(updatedNotification.getLastUpdated()).isAfterOrEqualTo(now);
        assertThat(updatedNotification.getUpdatedBy()).isEqualTo(TEST_URID);
    }

    @Test
    public void shouldUpdateNotificationScheduleAndContentForCompliance() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
        LocalDateTime updatedStart = now.plusDays(1);
        LocalDateTime updatedEnd = now.plusDays(2);

        Notification notification = Notification.builder()
            .id(1L)
            .schedule(NotificationSchedule.builder()
                .startDateTime(now)
                .endDateTime(now.plusDays(3))
                .build())
            .definition(complianceNotificationDefinition)
            .status(NotificationStatus.ACTIVE)
            .build();


        when(notificationRepository.getByIdWithDefinition(TEST_NOTIFICATION_ID)).thenReturn(Optional.of(notification));

        NotificationDTO request = NotificationDTO.builder()
            .type(NotificationType.EMISSIONS_MISSING_FOR_AOHA)
            .activationDetails(ComplianceActivationDetails.builder()
                .scheduledDate(notification.getSchedule().getStartDateTime().toLocalDate())
                .scheduledTime(notification.getSchedule().getStartDateTime().toLocalTime())
                .scheduledTimeNow(Boolean.FALSE)
                .expirationDate(updatedEnd.toLocalDate())
                .hasRecurrence(true)
                .recurrenceDays(2)
                .build())
            .contentDetails(ContentDetails.builder()
                .subject("new subject")
                .content("new content")
                .build())
            .build();

        Notification updatedNotification = cut.updateNotification(TEST_NOTIFICATION_ID, request);

        NotificationSchedule schedule = updatedNotification.getSchedule();

//        assertThat(schedule.getStartDateTime()).isEqualTo(updatedStart);
        assertThat(schedule.getEndDateTime()).isEqualTo(updatedEnd.toLocalDate().atStartOfDay());
        assertThat(schedule.getRunEveryXDays()).isEqualTo(2);

        assertThat(updatedNotification.getDefinition().getShortText()).isEqualTo("new subject");
        assertThat(updatedNotification.getDefinition().getLongText()).isEqualTo("new content");
    }

    @Test
    public void shouldRetrieveAdHocNotificationById() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
        Notification notification = Notification.builder()
            .id(1L)
            .schedule(NotificationSchedule.builder()
                .startDateTime(now.plusMinutes(60))
                .endDateTime(now.plusDays(1))
                .build())
            .definition(adHocDefinition)
            .updatedBy(TEST_URID)
            .build();

        when(notificationRepository.getByIdWithDefinition(TEST_NOTIFICATION_ID)).thenReturn(Optional.of(notification));
        when(notificationDefinitionRepository.findByType(NotificationType.AD_HOC))
            .thenReturn(Optional.of(adHocDefinition));

        NotificationDTO notificationDTO = cut.retrieveNotificationById(TEST_NOTIFICATION_ID);

        AdHocActivationDetails activationDetails = (AdHocActivationDetails) notificationDTO.getActivationDetails();
        assertThat(activationDetails.getScheduledDateTime()).isEqualTo(now.plusMinutes(60));
        assertThat(activationDetails.getScheduledDateTime()).isEqualTo(now.plusMinutes(60));
        assertThat(activationDetails.isHasExpirationDateSection()).isEqualTo(true);
        assertThat(activationDetails.getExpirationDateTime()).isEqualTo(Optional.of(now.plusDays(1)));
        assertThat(notificationDTO.getUpdatedBy()).isEqualTo("John Doe");
    }

    @Test
    public void shouldRetrieveComplianceNotificationById() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
        Notification notification = Notification.builder()
            .id(1L)
            .schedule(NotificationSchedule.builder()
                .startDateTime(now.plusMinutes(60))
                .endDateTime(now.plusDays(1))
                .runEveryXDays(2)
                .build())
            .definition(complianceNotificationDefinition)
            .updatedBy(TEST_URID)
            .build();

        when(notificationRepository.getByIdWithDefinition(TEST_NOTIFICATION_ID)).thenReturn(Optional.of(notification));
        when(notificationDefinitionRepository.findByType(TEST_COMPLIANCE_NOTIFICATION_TYPE))
            .thenReturn(Optional.of(complianceNotificationDefinition));

        NotificationDTO notificationDTO = cut.retrieveNotificationById(TEST_NOTIFICATION_ID);

        ComplianceActivationDetails activationDetails =
            (ComplianceActivationDetails) notificationDTO.getActivationDetails();
        assertThat(activationDetails.getScheduledDateTime()).isEqualTo(now.plusMinutes(60));
        assertThat(activationDetails.getScheduledDateTime()).isEqualTo(now.plusMinutes(60));
        assertThat(activationDetails.getRecurrenceDays()).isEqualTo(2);
        assertThat(activationDetails.isHasRecurrence()).isEqualTo(true);
    }
}
