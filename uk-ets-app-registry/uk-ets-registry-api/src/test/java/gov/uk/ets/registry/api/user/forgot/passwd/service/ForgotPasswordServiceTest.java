package gov.uk.ets.registry.api.user.forgot.passwd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ForgotPasswordServiceTest {

    private ForgotPasswordService forgotPasswordService;
    @Mock
    private TokenVerifier tokenVerifier;
    @Mock
    private UserAdministrationService userAdministrationService;
    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    @Mock
    private EventService eventService;
    @Mock
    private OTPValidator otpValidator;
    @Mock
    private Mapper mapper;

    private String applicationUrl = "localhost:8080";
    private String verificationPath = "verification-path";
    private Long expiration = 60L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        forgotPasswordService = new ForgotPasswordService(
            userAdministrationService,
            serviceAccountAuthorizationService,
            eventService,
            otpValidator,
            tokenVerifier,
            applicationUrl,
            verificationPath,
            expiration,
            mapper);
    }

    @Test
    void requesResetPasswordEmailSuccess() throws JsonProcessingException {
        // given
        String resetEmail = "reset@passwd.com";
        String resetEmailJson = new ObjectMapper().writeValueAsString(resetEmail);
        String verificationToken = "verificationToken";
        given(tokenVerifier.generateToken(any())).willReturn(verificationToken);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setAttributes(new HashMap<String, List<String>>());
        userRepresentation.getAttributes()
            .put(UserAttributes.STATE.toString().toLowerCase(), Arrays.asList(UserStatus.ENROLLED.toString()));
        given(userAdministrationService.findByEmail(resetEmail)).willReturn(Optional.of(userRepresentation));
        given(mapper.convertToJson(resetEmail)).willReturn(resetEmailJson);

        // when
        ForgotPasswordEmailDTO verification = forgotPasswordService.requesResetPasswordEmail(resetEmail);

        //then
        assertTrue(verification.isSuccess());
        assertEquals(resetEmail, verification.getEmail());
        ArgumentCaptor<GenerateTokenCommand> captor = ArgumentCaptor.forClass(GenerateTokenCommand.class);
        then(tokenVerifier).should(times(1)).generateToken(captor.capture());
        assertNotNull(captor.getValue().getPayload());
        String payload = new ObjectMapper().readValue(captor.getValue().getPayload(), String.class);
        assertEquals(resetEmail, payload);

        assertEquals(applicationUrl + verificationPath + verificationToken, verification.getConfirmationUrl());
    }

    @Test
    void requesResetPasswordEmailFailureSuspendedUser() throws JsonProcessingException {
        // given
        String resetEmail = "reset@passwd.com";
        UserRepresentation userRepresentation = new UserRepresentation();
        //Suspended User
        userRepresentation.setEnabled(false);
        userRepresentation.setAttributes(new HashMap<String, List<String>>());
        userRepresentation.getAttributes()
            .put(UserAttributes.STATE.toString().toLowerCase(), Arrays.asList(UserStatus.ENROLLED.toString()));
        given(userAdministrationService.findByEmail(resetEmail)).willReturn(Optional.of(userRepresentation));

        // when
        ForgotPasswordEmailDTO verification = forgotPasswordService.requesResetPasswordEmail(resetEmail);

        //then
        assertFalse(verification.isSuccess());
        assertNull(verification.getEmail());
        ArgumentCaptor<GenerateTokenCommand> captor = ArgumentCaptor.forClass(GenerateTokenCommand.class);
        then(tokenVerifier).should(times(0)).generateToken(captor.capture());
    }

    @Test
    void requesResetPasswordEmailFailureUnenrolledUser() throws JsonProcessingException {
        // given
        String resetEmail = "reset@passwd.com";
        UserRepresentation userRepresentation = new UserRepresentation();
        //Suspended User
        userRepresentation.setEnabled(true);
        userRepresentation.setAttributes(new HashMap<String, List<String>>());
        userRepresentation.getAttributes()
            .put(UserAttributes.STATE.toString().toLowerCase(), Arrays.asList(UserStatus.UNENROLLED.toString()));
        given(userAdministrationService.findByEmail(resetEmail)).willReturn(Optional.of(userRepresentation));

        // when
        ForgotPasswordEmailDTO verification = forgotPasswordService.requesResetPasswordEmail(resetEmail);

        //then
        assertFalse(verification.isSuccess());
        assertNull(verification.getEmail());
        ArgumentCaptor<GenerateTokenCommand> captor = ArgumentCaptor.forClass(GenerateTokenCommand.class);
        then(tokenVerifier).should(times(0)).generateToken(captor.capture());
    }

    @Test
    void requesResetPasswordEmailFailureNotExistingUser() throws JsonProcessingException {
        // given
        String resetEmail = "reset@passwd.com";
        given(userAdministrationService.findByEmail(resetEmail)).willReturn(Optional.empty());

        // when
        ForgotPasswordEmailDTO verification = forgotPasswordService.requesResetPasswordEmail(resetEmail);

        //then
        assertFalse(verification.isSuccess());
        assertNull(verification.getEmail());
        ArgumentCaptor<GenerateTokenCommand> captor = ArgumentCaptor.forClass(GenerateTokenCommand.class);
        then(tokenVerifier).should(times(0)).generateToken(captor.capture());
    }

    @Test
    void resetPassword() throws JsonProcessingException {
        // given
        String resetEmail = "reset@passwd.com";
        String resetEmailJson = new ObjectMapper().writeValueAsString(resetEmail);
        String verificationToken = "verificationToken";
        String otp = "123456";

        String userUrid = "test-urid";
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setAttributes(new HashMap<>());
        userRepresentation.getAttributes()
            .put(UserAttributes.URID.getAttributeName(), Arrays.asList(userUrid));

        ResetPasswordRequest request = new ResetPasswordRequest(verificationToken, otp, resetEmail);
        given(tokenVerifier.getPayload(verificationToken)).willReturn(resetEmailJson);
        given(mapper.convertToPojo(resetEmailJson, String.class)).willReturn(resetEmail);
        given(otpValidator.validate(otp, resetEmail)).willReturn(Boolean.TRUE);

        given(userAdministrationService.findByEmail(resetEmail)).willReturn(Optional.of(userRepresentation));

        // when
        ResetPasswordResponse response = forgotPasswordService.resetPassword(request);

        //then
        assertTrue(response.isSuccess());
        assertEquals(resetEmail, response.getEmail());
        String urid = userRepresentation.getAttributes().get(UserAttributes.URID.getAttributeName()).get(0);
        verify(eventService, times(1))
            .createAndPublishEvent(urid, urid,
                "", EventType.USER_STATUS_CHANGED, "Reset password");
    }

    @Test
    void resetPasswordInvalidOTP() throws JsonProcessingException {
        // given
        String resetEmail = "reset@passwd.com";
        String resetEmailJson = new ObjectMapper().writeValueAsString(resetEmail);
        String verificationToken = "verificationToken";
        String otp = "123456";
        ResetPasswordRequest request = new ResetPasswordRequest(verificationToken, otp, resetEmail);
        given(tokenVerifier.getPayload(verificationToken))
            .willReturn(new ObjectMapper().writeValueAsString(resetEmail));
        given(mapper.convertToPojo(resetEmailJson, String.class)).willReturn(resetEmail);
        given(otpValidator.validate(otp, resetEmail)).willReturn(Boolean.FALSE);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            forgotPasswordService.resetPassword(request);
        });

        String expectedMessage = "Invalid OTP code.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void resetPasswordExpiredToken() throws JsonProcessingException {
        // given
        String resetEmail = "reset@passwd.com";
        String verificationToken = "verificationToken";
        String otp = "123456";
        ResetPasswordRequest request = new ResetPasswordRequest(verificationToken, otp, resetEmail);
        given(tokenVerifier.getPayload(verificationToken)).willThrow(new TokenExpiredException("Token expired."));

        // when
        ResetPasswordResponse response = forgotPasswordService.resetPassword(request);

        //then
        assertFalse(response.isSuccess());
        assertNull(response.getEmail());
    }

    @Test
    void validateToken() throws JsonProcessingException {
        // given
        String resetEmail = "reset@passwd.com";
        String verificationToken = "verificationToken";
        // when
        given(tokenVerifier.getPayload(verificationToken))
            .willReturn(new ObjectMapper().writeValueAsString(resetEmail));

        // when
        ValidateTokenResponse response = forgotPasswordService.validateToken(verificationToken);

        //then
        assertTrue(response.isSuccess());
    }

    @Test
    void validateExpiredToken() throws JsonProcessingException {
        // given
        String verificationToken = "verificationToken";
        given(tokenVerifier.getPayload(verificationToken)).willThrow(new TokenExpiredException("Token expired."));

        // when
        ValidateTokenResponse response = forgotPasswordService.validateToken(verificationToken);

        //then
        assertFalse(response.isSuccess());
    }
}
