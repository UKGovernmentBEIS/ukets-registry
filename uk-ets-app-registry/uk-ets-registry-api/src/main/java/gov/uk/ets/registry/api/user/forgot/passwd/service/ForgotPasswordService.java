package gov.uk.ets.registry.api.user.forgot.passwd.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.JsonMappingException;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.keycloak.OTPValidator;
import gov.uk.ets.registry.api.common.security.GenerateTokenCommand;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.UserAttributes;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ResetPasswordRequest;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ResetPasswordResponse;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ValidateTokenResponse;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class ForgotPasswordService {

    private final UserAdministrationService userAdministrationService;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final EventService eventService;
    private final OTPValidator otpValidator;
    private final TokenVerifier tokenVerifier;
    private final Long expiration;
    private final String applicationUrl;
    private final String verificationPath;
    private final Mapper mapper;

    public ForgotPasswordService(UserAdministrationService userAdministrationService,
                                 ServiceAccountAuthorizationService serviceAccountAuthorizationService,
                                 EventService eventService,
                                 OTPValidator otpValidator,
                                 TokenVerifier tokenVerifier,
                                 @Value("${application.url}") String applicationUrl,
                                 @Value("${change.email.verification.path:/forgot-password/reset-password/}")
                                     String verificationPath,
                                 @Value("${business.property.reset.password.url.expiration:60}") Long expiration,
                                 Mapper Mapper) {
        this.userAdministrationService = userAdministrationService;
        this.serviceAccountAuthorizationService = serviceAccountAuthorizationService;
        this.eventService = eventService;
        this.otpValidator = otpValidator;
        this.tokenVerifier = tokenVerifier;
        this.applicationUrl = applicationUrl;
        this.verificationPath = verificationPath;
        this.expiration = expiration;
        this.mapper = Mapper;
    }


    /**
     * @param newEmail
     * @return
     */
    @EmitsGroupNotifications(GroupNotificationType.REQUEST_RESET_PASSWORD_LINK)
    public ForgotPasswordEmailDTO requesResetPasswordEmail(String email) {
            Optional<UserRepresentation> userOptional = userAdministrationService.findByEmail(email);
            if (userOptional.isPresent()) {
                UserRepresentation userRepresentation = userOptional.get();
                Optional<List<String>> userStateOptional = Optional
                    .ofNullable(userRepresentation.getAttributes().get(UserAttributes.STATE.toString().toLowerCase()));
                if (userStateOptional.isPresent()) {
                    Optional<String> userState = userStateOptional.get().stream().findFirst();
                    Set<UserStatus> validUserStates =
                        EnumSet.of(UserStatus.REGISTERED, UserStatus.VALIDATED, UserStatus.ENROLLED);
                    if (userRepresentation.isEnabled() && userState.isPresent() &&
                        validUserStates.contains(UserStatus.valueOf(userState.get()))) {
                        String token = tokenVerifier.generateToken(GenerateTokenCommand
                            .builder()
                            .payload(mapper.convertToJson(email))
                            .expiration(expiration)
                            .build());

                        String verificationUrl = applicationUrl + verificationPath + token;

                        return ForgotPasswordEmailDTO.builder()
                            .success(true)
                            .confirmationUrl(verificationUrl)
                            .email(email)
                            .expiration(expiration)
                            .build();
                    } else {
                        return ForgotPasswordEmailDTO.builder()
                            .success(false)
                            .build();
                    }
                } else {
                    return ForgotPasswordEmailDTO.builder()
                        .success(false)
                        .build();
                }
            } else {
                return ForgotPasswordEmailDTO.builder()
                    .success(false)
                    .build();
            }
    }

    public ValidateTokenResponse validateToken(String token) {
        try {
            if (Optional.ofNullable(tokenVerifier.getPayload(token)).isPresent()) {
                return new ValidateTokenResponse(true);
            }
        } catch (TokenExpiredException e) {
            log.warn("Email token for forgot password has expired", e);
        }

        return new ValidateTokenResponse(false);
    }

    @EmitsGroupNotifications(GroupNotificationType.RESET_PASSWORD_SUCCESS)
    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswdRequest) {
        try {
            String payload = tokenVerifier.getPayload(resetPasswdRequest.getToken());
            String email = mapper.convertToPojo(payload, String.class);
            //Validate OTP
            Boolean otpValidationResult = otpValidator.validate(resetPasswdRequest.getOtp(), email);
            Optional<UserRepresentation> userOptional = userAdministrationService.findByEmail(email);

            if (otpValidationResult) {
                //Reset Password in Keycloak
                serviceAccountAuthorizationService.resetUserPassword(email, resetPasswdRequest.getNewPasswd());
                // record event
                if (userOptional.isEmpty()) {
                    log.warn("User with email:{} not found", email);
                    throw new IllegalArgumentException(String.format("Invalid email.%s", email));
                }
                UserRepresentation userRepresentation = userOptional.get();
                serviceAccountAuthorizationService.invalidateUserSessions(userRepresentation.getId());
                String urid = userRepresentation.getAttributes().get(UserAttributes.URID.getAttributeName()).get(0);
                eventService.createAndPublishEvent(urid, urid, "",
                    EventType.USER_STATUS_CHANGED, "Reset password");
                return new ResetPasswordResponse(true, email);
            } else {
                throw new IllegalArgumentException("Invalid OTP code.");
            }
        } catch (JsonMappingException e) {
            log.warn("Email token cannot be parsed", e);
        } catch (TokenExpiredException e) {
            log.warn("Email token for forgot password has expired", e);
        }

        return new ResetPasswordResponse(false, null);
    }
}
