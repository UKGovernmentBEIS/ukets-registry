package gov.uk.ets.registry.api.user.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.common.security.UsedTokenService;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyChange;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskRequest;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskResponse;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonEmergencyChangeServiceTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "0sa8yasd97yads97ydsa9t78afsd8t7gds0juhd";
    private static final String TEST_URID = "UK123456";
    private static final Long EXPIRATION = 1L;
    private static final String APPLICATION_URL = "test-url/";
    private static final String VERIFICATION_PATH = "verification-url?token=";
    private static final List<UserStatus> statuses = List.of(UserStatus.SUSPENDED, UserStatus.UNENROLLED,
        UserStatus.UNENROLLEMENT_PENDING, UserStatus.DEACTIVATION_PENDING, UserStatus.DEACTIVATED);

    @Mock()
    private TokenVerifier tokenVerifier;
    @Mock
    private UsedTokenService usedTokenService;
    @Mock
    private LostPasswordAndTokenTaskService lostPasswordAndTokenTaskService;
    @Mock
    private UserService userService;
    @Mock
    private UserStatusService userStatusService;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    CommonEmergencyChangeService sut;

    @BeforeEach
    public void setUp() {
        sut = new CommonEmergencyChangeService(
            tokenVerifier, APPLICATION_URL, usedTokenService, lostPasswordAndTokenTaskService, userService,
            userStatusService,
            serviceAccountAuthorizationService
        );
    }

    @Test
    public void shouldGenerateCorrectVerificationUrl() {

        User user = new User();
        user.setUrid(TEST_URID);
        given(tokenVerifier.generateToken(any())).willReturn(TEST_TOKEN);
        given(userService.findByEmailNotInStatuses(TEST_EMAIL, statuses.toArray(UserStatus[]::new)))
            .willReturn(Optional.of(user));
        EmergencyChange emergencyChange = sut.requestEmergencyChange(TEST_EMAIL, EXPIRATION, VERIFICATION_PATH);

        assertThat(emergencyChange.getVerificationUrl())
            .isEqualTo(APPLICATION_URL + VERIFICATION_PATH + TEST_TOKEN);
    }

    @Test
    public void shouldThrowWhenConfirmingChangeForNonExistingUser() {
        EmergencyOtpChangeTaskRequest request = new EmergencyOtpChangeTaskRequest(TEST_TOKEN);

        assertThrows(IllegalArgumentException.class, () -> sut.confirmChange(request));
    }

    @Test
    public void shouldResetOTP() {

        Date expirationDate = new Date();
        given(usedTokenService.isTokenAlreadyUsed(TEST_TOKEN)).willReturn(false);
        given(tokenVerifier.getPayload(TEST_TOKEN)).willReturn(TEST_EMAIL);
        given(tokenVerifier.getExpiredAt(TEST_TOKEN)).willReturn(expirationDate);

        User user = new User();
        user.setUrid(TEST_URID);
        user.setIamIdentifier("iamIdentifier");
        given(userService.findByEmailNotInStatuses(TEST_EMAIL, statuses.toArray(UserStatus[]::new)))
            .willReturn(Optional.of(user));

        EmergencyOtpChangeTaskRequest request = new EmergencyOtpChangeTaskRequest(TEST_TOKEN);
        EmergencyOtpChangeTaskResponse emergencyOtpChangeTaskResponse = sut.confirmChange(request);

        assertThat(emergencyOtpChangeTaskResponse.getRequestId()).isNull();
        assertThat(emergencyOtpChangeTaskResponse.isTokenExpired()).isFalse();

        verify(serviceAccountAuthorizationService, times(1))
            .addRequiredActionToUser("iamIdentifier", "CONFIGURE_TOTP");
        verify(usedTokenService, times(1))
            .saveToken(TEST_TOKEN, expirationDate);
    }

    @Test
    public void shouldReturnedExpiredToken() {

        given(usedTokenService.isTokenAlreadyUsed(TEST_TOKEN)).willReturn(true);

        EmergencyOtpChangeTaskRequest request = new EmergencyOtpChangeTaskRequest(TEST_TOKEN);
        EmergencyOtpChangeTaskResponse emergencyOtpChangeTaskResponse = sut.confirmChange(request);

        assertThat(emergencyOtpChangeTaskResponse.getRequestId()).isNull();
        assertThat(emergencyOtpChangeTaskResponse.isTokenExpired()).isTrue();
    }

    @Test
    public void shouldReturnNullWhenRequestingChangeForNonExistingUser() {
        User user = new User();
        user.setUrid(TEST_URID);
        given(userService.findByEmailNotInStatuses(TEST_EMAIL, statuses.toArray(UserStatus[]::new)))
            .willReturn(Optional.ofNullable(null));
        EmergencyChange emergencyChange = sut.requestEmergencyChange(TEST_EMAIL, EXPIRATION, VERIFICATION_PATH);

        assertThat(emergencyChange).isEqualTo(null);
        verify(tokenVerifier, times(0)).generateToken(any());
    }
}
