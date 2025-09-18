package gov.uk.ets.registry.api.user.profile.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import gov.uk.ets.registry.api.auditevent.DomainEvent;
import gov.uk.ets.registry.api.auditevent.DomainObject;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.security.GenerateTokenCommand;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.common.security.UsedTokenService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.domain.EmailChange;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Date;
import java.util.Objects;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that supports the user email change requests.
 */
@Service
public class EmailChangeService {

    private final UserService userService;
    private final TokenVerifier tokenVerifier;
    private final UserAdministrationService userAdministrationService;
    private final Long expiration;
    private final String applicationUrl;
    private final String verificationPath;
    private final TaskRepository taskRepository;
    private final EventService eventService;
    private final EmailChangeChecker emailChangeChecker;
    private final UsedTokenService usedTokenService;
    private final Mapper mapper;

    public EmailChangeService(UserService userService,
                              TokenVerifier tokenVerifier,
                              UserAdministrationService userAdministrationService,
                              TaskRepository taskRepository,
                              EventService eventService,
                              EmailChangeChecker emailChangeChecker,
                              UsedTokenService usedTokenService,
                              @Value("${application.url}") String applicationUrl,
                              @Value("${change.email.verification.path:/email-change/confirmation/}")
                                  String verificationPath,
                              @Value("${change.email.verification.url.expiration:60}") Long expiration,
                              Mapper mapper) {
        this.userService = userService;
        this.tokenVerifier = tokenVerifier;
        this.userAdministrationService = userAdministrationService;
        this.expiration = expiration;
        this.verificationPath = verificationPath;
        this.applicationUrl = applicationUrl;
        this.taskRepository = taskRepository;
        this.eventService = eventService;
        this.emailChangeChecker = emailChangeChecker;
        this.usedTokenService = usedTokenService;
        this.mapper = mapper;
    }


    /**
     * @param dto {@link EmailChangeDTO}
     * @return
     */
    @EmitsGroupNotifications(GroupNotificationType.EMAIL_CHANGE_REQUESTED)
    public EmailChange requestEmailChange(@RuleInput(RuleInputType.NEW_EMAIL) EmailChangeDTO dto) {
        User currentUser = userService.getCurrentUser();

        boolean requestedByAnotherUser = isRequestedByAnotherUser(currentUser.getUrid(), dto.getUrid());
        if (requestedByAnotherUser && !userService.isSeniorOrJuniorAdminUser(currentUser)) {
            throw new IllegalArgumentException("Only a Senior or Junior Admin can request email update for someone else.");
        }

        User userToBeUpdated = requestedByAnotherUser ? userService.getUserByUrid(dto.getUrid()) : currentUser;

        UserRepresentation userRepresentation = userAdministrationService
            .findByIamId(userToBeUpdated.getIamIdentifier());
        String oldEmail = userRepresentation.getEmail();

        EmailChangeDTO payload = EmailChangeDTO
            .builder()
            .oldEmail(oldEmail)
            .newEmail(dto.getNewEmail())
            .urid(userToBeUpdated.getUrid())
            .requesterUrid(currentUser.getUrid())
            .build();

        String token = tokenVerifier.generateToken(GenerateTokenCommand
            .builder()
            .payload(mapper.convertToJson(payload))
            .expiration(expiration)
            .build());

        String verificationUrl = applicationUrl + verificationPath + token;

        return EmailChange.builder()
            .confirmationUrl(verificationUrl)
            .newEmail(dto.getNewEmail())
            .oldEmail(oldEmail)
            .expiration(expiration)
            .build();
    }

    private boolean isRequestedByAnotherUser(String currentUserUrid, String suppliedUserUrid) {
        if (Objects.isNull(suppliedUserUrid)) {
            return false;
        }
        return !Objects.equals(currentUserUrid, suppliedUserUrid);
    }

    /**
     * Creates a new email change task with status SUBMITTED_NOT_YET_APPROVED.
     */
    @Transactional
    public Long openEmailChangeTask(String token) {

        if (usedTokenService.isTokenAlreadyUsed(token)) {
            throw new TokenExpiredException("Token has already been used.");
        }
        String payload = tokenVerifier.getPayload(token);
        EmailChangeDTO dto = mapper.convertToPojo(payload, EmailChangeDTO.class);

        User user = userService.getUserByUrid(dto.getUrid());
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (emailChangeChecker.sameEmailWithRecoveryEmail(dto.getNewEmail(), user.getIamIdentifier())) {
            throw new IllegalArgumentException("User email address cannot be the same as user recovery email address.");
        }
        if (emailChangeChecker.otherUserHasNewEmailAsWorkingEmail(dto.getNewEmail())) {
            throw new IllegalArgumentException("The new email belongs to other user");
        }
        if (emailChangeChecker.otherPendingRequestsOfUser(dto.getUrid())) {
            throw new IllegalArgumentException("Other email change request awaiting approval");
        }
        if (emailChangeChecker.pendingRequestsWithSameNewEmail(dto.getNewEmail())) {
            throw new IllegalArgumentException("Other email change request with same new email exists");
        }
        Task task = new Task();
        task.setRequestId(taskRepository.getNextRequestId());
        task.setDifference(mapper.convertToJson(dto));
        task.setInitiatedDate(new Date());
        task.setInitiatedBy(userService.getUserByUrid(dto.getRequesterUrid()));
        task.setUser(user);
        task.setType(RequestType.REQUESTED_EMAIL_CHANGE);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);

        taskRepository.save(task);
        usedTokenService.saveToken(token, tokenVerifier.getExpiredAt(token));

        eventService.publishEvent(DomainEvent.builder()
            .who(dto.getRequesterUrid())
            .when(new Date())
            .what("Request to change email address")
            .domainObject(DomainObject.create(EventType.TASK_REQUESTED.getClazz(), task.getRequestId().toString()))
            .description(dto.getNewEmail())
            .build());

        return task.getRequestId();
    }
}
