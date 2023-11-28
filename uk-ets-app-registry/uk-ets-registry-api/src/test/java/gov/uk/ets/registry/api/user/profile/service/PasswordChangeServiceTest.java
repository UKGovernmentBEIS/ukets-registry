package gov.uk.ets.registry.api.user.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.keycloak.PasswordValidator;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.user.UserActionError;
import gov.uk.ets.registry.api.user.UserActionException;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.web.PasswordValidationRequest;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PasswordChangeServiceTest {

    private static final String TEST_CURRENT_PASSWORD = "current";
    private static final String TEST_NEW_PASSWORD = "new";
    private static final String TEST_IAM_ID = "iu34h23huhiu23huo24o3iu";
    private static final String TEST_EMAIL = "test@test.com";

    @Mock
    private UserService userService;
    @Mock
    private UserAdministrationService userAdministrationService;
    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    @Mock
    private PasswordValidator passwordValidator;
    @Mock
    private EventService eventService;

    @InjectMocks
    PasswordChangeService cut;

    User user;
    UserRepresentation userRepresentation;
    PasswordValidationRequest passwordValidationRequest;

    @Test
    public void shouldResetPasswordCorrectly() {
        setUpMocks(true, true);

        cut.changePassword(new PasswordChangeDTO(TEST_CURRENT_PASSWORD, TEST_NEW_PASSWORD));

        verify(serviceAccountAuthorizationService, times(1)).resetUserPassword(TEST_EMAIL, TEST_NEW_PASSWORD);
        verify(serviceAccountAuthorizationService, times(1)).logoutUser(TEST_IAM_ID);
    }


    @Test
    public void shouldThrowWhenUserIsDisabledInKeycloak() {
        setUpMocks(false, true);

        UserActionException exception = assertThrows(UserActionException.class,
            () -> cut.changePassword(new PasswordChangeDTO(TEST_CURRENT_PASSWORD, TEST_NEW_PASSWORD)));
        assertThat(exception.getUserActionError().getMessage()).isEqualTo(UserActionError.USER_NOT_ACTIVE.getMessage());
    }

    @Test
    public void shouldThrowWhenGivenPasswordIsInvalid() {
        setUpMocks(true, false);

        UserActionException exception = assertThrows(UserActionException.class,
            () -> cut.changePassword(new PasswordChangeDTO(TEST_CURRENT_PASSWORD, TEST_NEW_PASSWORD)));
        assertThat(exception.getUserActionError().getMessage())
            .isEqualTo(UserActionError.INVALID_PASSWORD.getMessage());
    }


    private void setUpMocks(boolean isEnabled, boolean isPasswordValid) {
        user = new User();
        user.setIamIdentifier(TEST_IAM_ID);
        userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(isEnabled);
        userRepresentation.setEmail(TEST_EMAIL);
        passwordValidationRequest = new PasswordValidationRequest(TEST_CURRENT_PASSWORD);
        when(userService.getCurrentUser()).thenReturn(user);
        when(userAdministrationService.findByIamId(TEST_IAM_ID)).thenReturn(userRepresentation);
        when(passwordValidator.validate(passwordValidationRequest)).thenReturn(isPasswordValid);
    }
}
