package gov.uk.ets.registry.api.user.profile.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.AnyAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service related to 2FA tasks (BAU).
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class TokenTaskService implements TaskTypeService<TokenTaskDetailsDTO> {

    public static final String CONFIGURE_OTP_REQUIRED_ACTION = "CONFIGURE_TOTP";

    /**
     * Repository for tasks.
     */
    private final TaskRepository taskRepository;

    /**
     * Service for users.
     */
    private final UserService userService;
    
    private final UserStatusService userStatusService;

    /**
     * Service for token changes.
     */
    private final TokenChangeService tokenChangeService;

    private final TokenVerifier tokenVerifier;

    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    private final TaskEventService taskEventService;

    private final Mapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.CHANGE_TOKEN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        TokenTaskDetailsDTO result = new TokenTaskDetailsDTO(taskDetailsDTO);
        TokenTaskDetailsDTO temp = mapper.convertToPojo(taskDetailsDTO.getDifference(), TokenTaskDetailsDTO.class);
        result.setEmail(temp.getEmail());
        result.setComment(temp.getComment());
        result.setFirstName(temp.getFirstName());
        result.setLastName(temp.getLastName());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public String proposeRequest(String reason) {
        User user = userService.getCurrentUser();

        if (!taskRepository.findPendingTasksByTypeAndUser(RequestType.CHANGE_TOKEN, user.getUrid()).isEmpty()) {
            throw new BusinessRuleErrorException(ErrorBody.from("There is already another device change request pending approval."));
        }

        UserRepresentation keycloakUser = serviceAccountAuthorizationService.getUser(user.getIamIdentifier());

        TokenTaskDetailsDTO details = new TokenTaskDetailsDTO();
        details.setInitiatorUrid(user.getUrid());
        details.setEmail(keycloakUser.getEmail());
        details.setFirstName(keycloakUser.getFirstName());
        details.setLastName(keycloakUser.getLastName());
        details.setComment(reason);

        Task task = new Task();
        task.setInitiatedBy(user);
        task.setUser(user);
        task.setRequestId(taskRepository.getNextRequestId());
        task.setType(RequestType.CHANGE_TOKEN);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setDifference(mapper.convertToJson(details));
        task.setInitiatedDate(new Date());
        taskRepository.save(task);
        
        userStatusService.changeUserStatus(createUserStatusChange(user,UserStatus.SUSPENDED),
            serviceAccountAuthorizationService.obtainAccessToken().getToken());

        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, user.getUrid(), " submitted.", reason);

        // sign out user in keycloak
        serviceAccountAuthorizationService.logoutUser(user.getIamIdentifier());
        
        return task.getRequestId().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Protected({
        FourEyesPrincipleRule.class,
        AnyAdminRule.class
    })
    @Override
    @Transactional
    public TaskCompleteResponse complete(TokenTaskDetailsDTO taskDTO, TaskOutcome taskOutcome, String comment) {
        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            tokenChangeService.sendEmailMessage(taskDTO);
        } else {
            User user = userService.getUserByUrid(taskDTO.getReferredUserURID());
            userStatusService.changeUserStatus(createUserStatusChange(user,user.getPreviousState()),
                serviceAccountAuthorizationService.obtainAccessToken().getToken());            
        }
        return null;
    }

    @Transactional
    public Boolean proceed(String token) {
        boolean result = true;
        try {
            String payload = tokenVerifier.getPayload(token);
            User user = userService.getUserByUrid(Utils.deserialiseFromJson(payload, String.class));

            // If the user is not SUSPENDED it means that the user has clicked the link for a second time.
            if (user.getState() != UserStatus.SUSPENDED) {
                return true;
            }

            serviceAccountAuthorizationService.addRequiredActionToUser(user.getIamIdentifier(), CONFIGURE_OTP_REQUIRED_ACTION);
            userStatusService.changeUserStatus(createUserStatusChange(user,user.getPreviousState()),
                serviceAccountAuthorizationService.obtainAccessToken().getToken());
        } catch(TokenExpiredException exc) {
            log.info("Email token for 2FA change has expired", exc);
            result = false;
        }
        return result;
    }

    /**
     * Returns when the current user has updated his 2FA token.
     * @return a formatted date.
     */
    public String getTokenLastUpdateDate() {
        String result = null;
        User user = userService.getCurrentUser();
        LocalDateTime date = serviceAccountAuthorizationService.getTokenLastUpdateDate(user.getIamIdentifier());
        if (date != null) {
            result = Utils.formatDay(date, true);
        }
        return result;
    }
    
    private UserStatusChangeDTO createUserStatusChange(User user,UserStatus userStatus) {
        UserStatusChangeDTO statusChange = new UserStatusChangeDTO();
        statusChange.setUrid(user.getUrid());
        statusChange.setUserStatus(userStatus);
        return statusChange;
    }
}
