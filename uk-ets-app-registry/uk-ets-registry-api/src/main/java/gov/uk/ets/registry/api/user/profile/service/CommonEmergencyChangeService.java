package gov.uk.ets.registry.api.user.profile.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.security.GenerateTokenCommand;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyChange;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskRequest;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskResponse;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class CommonEmergencyChangeService {
    private final TokenVerifier tokenVerifier;
    private final String applicationUrl;
    private final LostTokenTaskService lostTokenTaskService;
    private final LostPasswordAndTokenTaskService lostPasswordAndTokenTaskService;
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    public CommonEmergencyChangeService(TokenVerifier tokenVerifier, @Value("${application.url}") String applicationUrl,
                                        LostTokenTaskService lostTokenTaskService,
                                        LostPasswordAndTokenTaskService lostPasswordAndTokenTaskService,
                                        UserService userService,
                                        UserStatusService userStatusService,
                                        ServiceAccountAuthorizationService serviceAccountAuthorizationService) {
        this.tokenVerifier = tokenVerifier;
        this.applicationUrl = applicationUrl;
        this.lostTokenTaskService = lostTokenTaskService;
        this.lostPasswordAndTokenTaskService = lostPasswordAndTokenTaskService;
        this.userService = userService;
        this.userStatusService = userStatusService;
        this.serviceAccountAuthorizationService = serviceAccountAuthorizationService;
    }


    EmergencyChange requestEmergencyChange(String email, Long expiration, String verificationPath) {
        if (userService.findByEmailNotInStatuses(email, UserStatus.SUSPENDED, UserStatus.UNENROLLED,
            UserStatus.UNENROLLEMENT_PENDING, UserStatus.DEACTIVATION_PENDING, UserStatus.DEACTIVATED).isEmpty()) {
            String message =
                String.format("Valid user with email %s was not found. Emergency otp email not send", email);
            log.warn(message);
            return null; // this is checked in the notification appliance so the email will not be sent
        }
        String token = tokenVerifier.generateToken(GenerateTokenCommand
            .builder()
            .payload(email)
            .expiration(expiration)
            .build());

        String confirmationUrl = applicationUrl + verificationPath + token;

        return EmergencyChange.builder()
            .email(email)
            .expiration(expiration)
            .verificationUrl(confirmationUrl)
            .build();
    }

    /**
     * Verifies token, creates task and suspends user.
     */
    @Transactional
    public EmergencyOtpChangeTaskResponse confirmChange(EmergencyOtpChangeTaskRequest request) {
        try {
            String email = tokenVerifier.getPayload(request.getToken());

            User user = userService.findByEmailNotInStatuses(email, UserStatus.SUSPENDED, UserStatus.UNENROLLED,
                UserStatus.UNENROLLEMENT_PENDING, UserStatus.DEACTIVATION_PENDING, UserStatus.DEACTIVATED)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

            String requestId = lostTokenTaskService.proposeRequest(user, email, RequestType.LOST_TOKEN);
            serviceAccountAuthorizationService.invalidateUserSessions(user.getIamIdentifier());
            userStatusService.changeUserStatus(createSuspendedUserChange(user),
                serviceAccountAuthorizationService.obtainAccessToken().getToken());
            return new EmergencyOtpChangeTaskResponse(requestId, false);

        } catch (TokenExpiredException e) {
            return new EmergencyOtpChangeTaskResponse(null, true);
        }
    }


    private UserStatusChangeDTO createSuspendedUserChange(User user) {
        UserStatusChangeDTO statusChange = new UserStatusChangeDTO();
        statusChange.setUrid(user.getUrid());
        statusChange.setUserStatus(UserStatus.SUSPENDED);
        return statusChange;
    }

    @Transactional
    public EmergencyOtpChangeTaskResponse confirmPasswordOtpChange(EmergencyOtpChangeTaskRequest request) {
        try {
            String email = tokenVerifier.getPayload(request.getToken());

            User user = userService.findByEmailNotInStatuses(email, UserStatus.SUSPENDED, UserStatus.UNENROLLED,
                UserStatus.UNENROLLEMENT_PENDING, UserStatus.DEACTIVATION_PENDING, UserStatus.DEACTIVATED)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

            String requestId =
                lostPasswordAndTokenTaskService.proposeRequest(user, email, RequestType.LOST_PASSWORD_AND_TOKEN);

            serviceAccountAuthorizationService.invalidateUserSessions(user.getIamIdentifier());
            userStatusService.changeUserStatus(createSuspendedUserChange(user),
                serviceAccountAuthorizationService.obtainAccessToken().getToken());
            return new EmergencyOtpChangeTaskResponse(requestId, false);

        } catch (TokenExpiredException e) {
            return new EmergencyOtpChangeTaskResponse(null, true);
        }
    }
}
