package gov.uk.ets.registry.api.user.profile;

import com.auth0.jwt.exceptions.TokenExpiredException;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.OTPBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules.NoOtherUserHasNewEmailAsWorkingEmail;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules.OtherPendingEmailChangesOfCurrentUserShouldNotExist;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules.OtherPendingEmailChangesWithSameNewEmailShouldNotExist;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.user.profile.service.EmailChangeDTO;
import gov.uk.ets.registry.api.user.profile.service.EmailChangeService;
import gov.uk.ets.registry.api.user.profile.service.EmergencyOtpChangeService;
import gov.uk.ets.registry.api.user.profile.service.EmergencyPasswordOtpChangeService;
import gov.uk.ets.registry.api.user.profile.service.PasswordChangeDTO;
import gov.uk.ets.registry.api.user.profile.service.PasswordChangeService;
import gov.uk.ets.registry.api.user.profile.service.TokenTaskService;
import gov.uk.ets.registry.api.user.profile.web.EmailChangeRequestedResponse;
import gov.uk.ets.registry.api.user.profile.web.EmailChangeVerifiedResponse;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeRequest;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskRequest;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RPC endpoint that handles requests related to user profile.
 */

@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class UserProfileController {
    private final EmailChangeService emailChangeService;
    private final TokenVerifier tokenVerifier;
    private final EmergencyOtpChangeService emergencyOtpChangeService;
    private final EmergencyPasswordOtpChangeService emergencyPasswordOtpChangeService;
    private final TokenTaskService tokenTaskService;
    private final PasswordChangeService passwordChangeService;
    private final Mapper mapper;

    /**
     * Initiates a change email request.
     *
     * @param dto
     * @return The {@link EmailChangeRequestedResponse}
     */
    @Protected(
        {
            OTPBusinessRule.class,
            NoOtherUserHasNewEmailAsWorkingEmail.class,
            OtherPendingEmailChangesOfCurrentUserShouldNotExist.class,
            OtherPendingEmailChangesWithSameNewEmailShouldNotExist.class
        }
    )
    @PostMapping(path = "user-profile.update.email", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmailChangeRequestedResponse requestEmailChange(
        @RuleInput(RuleInputType.NEW_EMAIL) @RequestBody @Valid EmailChangeDTO dto,
        @RuleInput(RuleInputType.OTP) @RequestParam String otp) {
        String mailedTo = emailChangeService.requestEmailChange(dto).getNewEmail();
        return new EmailChangeRequestedResponse(mailedTo);
    }

    /**
     * TODO: Implementation of this service
     * Confirms the email change request and creates the relative task.
     *
     * @param token
     * @return
     */
    @PostMapping(path = "user-profile.confirm.email")
    public EmailChangeVerifiedResponse confirmEmailChange(@RequestParam String token) {
        EmailChangeVerifiedResponse response = new EmailChangeVerifiedResponse();
        try {
            String payload = tokenVerifier.getPayload(token);
            EmailChangeDTO dto = mapper.convertToPojo(payload, EmailChangeDTO.class);
            Long requestId = emailChangeService.openEmailChangeTask(dto);
            response.setRequestId(requestId);
        } catch (TokenExpiredException exception) {
            response.setTokenExpired(true);
        }
        return response;
    }

    @Protected({OTPBusinessRule.class})
    @PostMapping(path = "validate-otp")
    public void validateOtp(@RuleInput(RuleInputType.OTP) @RequestParam String otp) {
        // nothing to implement here, since the logic is applied via an annotation.
    }

    @PostMapping(path = "user-profile.update.token")
    public String requestTokenUpdate(@RequestParam String reason) {
        return tokenTaskService.proposeRequest(reason);
    }

    @PostMapping(path = "token-change.request.link")
    public Boolean proceedToTokenChange(@RequestParam String token) {
        return tokenTaskService.proceed(token);
    }

    @GetMapping(path = "user-profile.get.token-date")
    public EmailChangeRequestedResponse getTokenDate() {
        return new EmailChangeRequestedResponse(tokenTaskService.getTokenLastUpdateDate());
    }

    /**
     * Generates the email for the emergency OTP change request.
     *
     * @param request
     * @return an empty response regardless the user's email exists in the registry database
     */
    @PostMapping(path = "user-profile.emergency.otp.request", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> requestEmergencyOtpChange(
        @RequestBody @Valid EmergencyOtpChangeRequest request) {
        emergencyOtpChangeService.requestChange(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates the task for the emergency OTP change request.
     */
    @PostMapping(path = "user-profile.emergency.otp.task", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmergencyOtpChangeTaskResponse confirmEmergencyOtpChange(
        @RequestBody @Valid EmergencyOtpChangeTaskRequest request) {
        return emergencyOtpChangeService.confirmChange(request);
    }

    /**
     * Generates the email for the emergency OTP change request.
     *
     * @return an empty response regardless the user's email exists in the registry database
     */
    @PostMapping(path = "user-profile.emergency.password-otp.request", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> requestEmergencyPasswordOtpChange(
        @RequestBody @Valid EmergencyOtpChangeRequest request) {
        emergencyPasswordOtpChangeService.requestChange(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    /**
     * Creates the task for the emergency OTP change request.
     */
    @PostMapping(path = "user-profile.emergency.password-otp.task", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EmergencyOtpChangeTaskResponse confirmEmergencyPasswordOtpChange(
        @RequestBody @Valid EmergencyOtpChangeTaskRequest request) {
        return emergencyPasswordOtpChangeService.confirmChange(request);
    }

    @Protected({OTPBusinessRule.class})
    @PostMapping(path = "user-profile.update.password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void requestPasswordChange(@RequestBody @Valid PasswordChangeDTO dto,
                                      @RuleInput(RuleInputType.OTP) @RequestParam String otp) {
        passwordChangeService.changePassword(dto);
    }
}
