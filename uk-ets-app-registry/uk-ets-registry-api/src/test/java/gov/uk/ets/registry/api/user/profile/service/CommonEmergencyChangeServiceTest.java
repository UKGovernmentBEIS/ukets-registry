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
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.profile.domain.EmergencyChange;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskRequest;
import gov.uk.ets.registry.api.user.profile.web.EmergencyOtpChangeTaskResponse;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonEmergencyChangeServiceTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "0sa8yasd97yads97ydsa9t78afsd8t7gds0juhd";
    private static final String TEST_REQUEST_ID = "12345";
    private static final String TEST_URID = "UK123456";
    private static final Long EXPIRATION = 1L;
    private static final String APPLICATION_URL = "test-url/";
    private static final String VERIFICATION_PATH = "verification-url?token=";
    private static final List<UserStatus> statuses = List.of(UserStatus.SUSPENDED, UserStatus.UNENROLLED,
        UserStatus.UNENROLLEMENT_PENDING, UserStatus.DEACTIVATION_PENDING, UserStatus.DEACTIVATED);

    @Mock()
    private TokenVerifier tokenVerifier;
    @Mock
    private LostTokenTaskService taskService;
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
            tokenVerifier, APPLICATION_URL, taskService, lostPasswordAndTokenTaskService, userService,
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
    public void shouldReturnCorrectRequestIdAndExpired() {

        given(tokenVerifier.getPayload(TEST_TOKEN)).willReturn(TEST_EMAIL);
        User user = new User();
        user.setUrid(TEST_URID);
        given(userService.findByEmailNotInStatuses(TEST_EMAIL, statuses.toArray(UserStatus[]::new)))
            .willReturn(Optional.of(user));
        given(taskService.proposeRequest(user, TEST_EMAIL, RequestType.LOST_TOKEN)).willReturn(TEST_REQUEST_ID);
        given(serviceAccountAuthorizationService.obtainAccessToken().getToken()).willReturn(TEST_TOKEN);

        EmergencyOtpChangeTaskRequest request = new EmergencyOtpChangeTaskRequest(TEST_TOKEN);
        EmergencyOtpChangeTaskResponse emergencyOtpChangeTaskResponse = sut.confirmChange(request);

        assertThat(emergencyOtpChangeTaskResponse.getRequestId()).isEqualTo(TEST_REQUEST_ID);
        assertThat(emergencyOtpChangeTaskResponse.isTokenExpired()).isEqualTo(false);

        ArgumentCaptor<UserStatusChangeDTO> statusChangeCapture = ArgumentCaptor.forClass(UserStatusChangeDTO.class);
        ArgumentCaptor<String> tokenCapture = ArgumentCaptor.forClass(String.class);
        verify(userStatusService, times(1)).changeUserStatus(statusChangeCapture.capture(), tokenCapture.capture());

        assertThat(statusChangeCapture.getValue().getUrid()).isEqualTo(TEST_URID);
        assertThat(statusChangeCapture.getValue().getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
        assertThat(tokenCapture.getValue()).isEqualTo(TEST_TOKEN);
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
