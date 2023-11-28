package gov.uk.ets.registry.api.user.profile.service;

import static gov.uk.ets.registry.api.user.profile.service.LostTokenTaskService.CONFIGURE_OTP_REQUIRED_ACTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AnyAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyRegistryAdminCanCompleteTaskRule;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.profile.web.EmergencyTaskCompleteResponse;
import gov.uk.ets.registry.api.user.service.UserService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LostTokenTaskServiceTest {


    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_REQUEST_ID = "123456";
    private static final String TEST_URID = "UK12345678";
    private static final String TEST_TOKEN = "&^HH^&76ghh7";
    private static final String TEST_IAM_ID = "9776807asd5856865656697";
    private static final UserStatus TEST_PREVIOUS_STATE = UserStatus.VALIDATED;

    @Mock
    private UserService userService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    @Mock
    private UserStatusService userStatusService;
    @Mock
    private CommonEmergencyTaskService commonTaskService;

    LostTokenTaskService sut;

    @BeforeEach
    public void setUp() {
        sut =
            new LostTokenTaskService(serviceAccountAuthorizationService, userStatusService, userService,
                commonTaskService);
    }

    @Test
    void shouldOnlyChangeStatusOnTaskRejection() {
        User user = new User();
        user.setUrid(TEST_URID);
        user.setPreviousState(TEST_PREVIOUS_STATE);

        given(userService.getUserByUrid(TEST_URID)).willReturn(user);

        UserStatusChangeDTO userStatusChangeDTO = new UserStatusChangeDTO();
        userStatusChangeDTO.setUrid(user.getUrid());
        userStatusChangeDTO.setUserStatus(TEST_PREVIOUS_STATE);
        given(commonTaskService.createUnsuspendedUserChange(user)).willReturn(userStatusChangeDTO);

        TokenTaskDetailsDTO taskDTO = new TokenTaskDetailsDTO();
        taskDTO.setReferredUserURID(TEST_URID);
        EmergencyTaskCompleteResponse response =
            (EmergencyTaskCompleteResponse) sut.complete(taskDTO, TaskOutcome.REJECTED, null);

        assertThat(response.getLoginUrl()).isNull();
        ArgumentCaptor<UserStatusChangeDTO> userChangeCapture = ArgumentCaptor.forClass(UserStatusChangeDTO.class);
        ArgumentCaptor<String> tokenCapture = ArgumentCaptor.forClass(String.class);
        verify(userStatusService, times(1)).changeUserStatus(userChangeCapture.capture(), tokenCapture.capture());

        assertThat(userChangeCapture.getValue().getUserStatus()).isEqualTo(TEST_PREVIOUS_STATE);
        assertThat(userChangeCapture.getValue().getUrid()).isEqualTo(TEST_URID);
        verify(serviceAccountAuthorizationService, times(0)).addRequiredActionToUser(any(), any());
    }

    @Test
    void shouldChangeStatusAndAddRequiredActionOnTaskApproval() {
        User user = new User();
        user.setUrid(TEST_URID);
        user.setIamIdentifier(TEST_IAM_ID);
        user.setPreviousState(TEST_PREVIOUS_STATE);
        given(userService.getUserByUrid(TEST_URID)).willReturn(user);
        given(serviceAccountAuthorizationService.obtainAccessToken().getToken()).willReturn(TEST_TOKEN);
        UserStatusChangeDTO userStatusChangeDTO = new UserStatusChangeDTO();
        userStatusChangeDTO.setUrid(user.getUrid());
        userStatusChangeDTO.setUserStatus(TEST_PREVIOUS_STATE);
        given(commonTaskService.createUnsuspendedUserChange(user)).willReturn(userStatusChangeDTO);

        TokenTaskDetailsDTO taskDTO = new TokenTaskDetailsDTO();
        taskDTO.setEmail(TEST_EMAIL);
        taskDTO.setRequestId(Long.parseLong(TEST_REQUEST_ID));
        taskDTO.setReferredUserURID(TEST_URID);

        EmergencyTaskCompleteResponse response =
            (EmergencyTaskCompleteResponse) sut.complete(taskDTO, TaskOutcome.APPROVED, null);

        assertThat(response.getLoginUrl()).isNotNull();
        assertThat(response.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.getRequestIdentifier()).isEqualTo(Long.parseLong(TEST_REQUEST_ID));

        ArgumentCaptor<UserStatusChangeDTO> userChangeCapture = ArgumentCaptor.forClass(UserStatusChangeDTO.class);
        ArgumentCaptor<String> tokenCapture = ArgumentCaptor.forClass(String.class);
        verify(userStatusService, times(1)).changeUserStatus(userChangeCapture.capture(), tokenCapture.capture());
        verify(serviceAccountAuthorizationService, times(1))
            .addRequiredActionToUser(TEST_IAM_ID, CONFIGURE_OTP_REQUIRED_ACTION);

        assertThat(userChangeCapture.getValue().getUserStatus()).isEqualTo(TEST_PREVIOUS_STATE);
        assertThat(userChangeCapture.getValue().getUrid()).isEqualTo(TEST_URID);
        assertThat(tokenCapture.getValue()).isEqualTo(TEST_TOKEN);

    }

    @Test
    void testProtectedRulesForCompleteMethod() throws Exception {
        Method method = sut.getClass().getMethod("complete", TokenTaskDetailsDTO.class, TaskOutcome.class, String.class);

        assertTrue(method.isAnnotationPresent(Protected.class));

        Protected protectedAnnotation = method.getAnnotation(Protected.class);
        Optional<Class<? extends AbstractBusinessRule>> fourEyesPrincipleRule = Arrays.stream(protectedAnnotation.value()).filter(annotation ->
                annotation.getName().contains(FourEyesPrincipleRule.class.getName())).findAny();
        assertTrue(fourEyesPrincipleRule.isPresent());

        Optional<Class<? extends AbstractBusinessRule>> onlyRegistryAdminCanCompleteTaskRule = Arrays.stream(protectedAnnotation.value()).filter(annotation ->
                annotation.getName().contains(OnlyRegistryAdminCanCompleteTaskRule.class.getName())).findAny();
        assertTrue(onlyRegistryAdminCanCompleteTaskRule.isPresent());
    }

}
