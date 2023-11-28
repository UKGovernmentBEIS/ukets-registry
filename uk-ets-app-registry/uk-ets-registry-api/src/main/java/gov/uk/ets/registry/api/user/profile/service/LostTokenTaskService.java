package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyRegistryAdminCanCompleteTaskRule;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.web.EmergencyTaskCompleteResponse;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service related to 2FA tasks.
 */
@Service
@RequiredArgsConstructor
public class LostTokenTaskService implements TaskTypeService<TokenTaskDetailsDTO> {

    public static final String CONFIGURE_OTP_REQUIRED_ACTION = "CONFIGURE_TOTP";
    public static final String CONFIGURE_UPDATE_PASSWORD_REQUIRED_ACTION = "UPDATE_PASSWORD";
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final UserStatusService userStatusService;
    @Value("${application.url}")
    private String applicationUrl;
    @Value("${login.redirect.url:/dashboard}")
    private String loginRedirectUrl;

    /**
     * Service for users.
     */
    private final UserService userService;

    private final CommonEmergencyTaskService taskService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.LOST_TOKEN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        return taskService.getDetails(taskDetailsDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public String proposeRequest(User user, String email, RequestType requestType) {
        return taskService.proposeRequest(user, email, requestType);
    }

    /**
     * Un-suspends the user and resets the OTP so that the user has to re-configure.
     */
    @Override
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.EMERGENCY_OTP_CHANGE_COMPLETE)
    @Protected({
        FourEyesPrincipleRule.class,
        OnlyRegistryAdminCanCompleteTaskRule.class
    })
    public TaskCompleteResponse complete(TokenTaskDetailsDTO taskDTO, TaskOutcome taskOutcome, String comment) {
        User user = userService.getUserByUrid(taskDTO.getReferredUserURID());

        userStatusService.changeUserStatus(taskService.createUnsuspendedUserChange(user),
            serviceAccountAuthorizationService.obtainAccessToken().getToken());

        if (TaskOutcome.APPROVED.equals(taskOutcome)) {

            serviceAccountAuthorizationService
                .addRequiredActionToUser(user.getIamIdentifier(), CONFIGURE_OTP_REQUIRED_ACTION);

            return EmergencyTaskCompleteResponse
                .emergencyTaskCompleteResponseBuilder()
                .email(taskDTO.getEmail())
                .requestIdentifier(taskDTO.getRequestId())
                .loginUrl(String.format("%s%s", applicationUrl, loginRedirectUrl))
                .build();
        }
        return EmergencyTaskCompleteResponse
            .emergencyTaskCompleteResponseBuilder()
            .requestIdentifier(taskDTO.getRequestId())
            .build();
    }


}
