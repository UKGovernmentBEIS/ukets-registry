package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.keycloak.PasswordValidator;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.UserActionError;
import gov.uk.ets.registry.api.user.UserActionException;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.profile.web.PasswordValidationRequest;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PasswordChangeService {

    private final UserService userService;
    private final UserAdministrationService userAdministrationService;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final PasswordValidator passwordValidator;
    private final EventService eventService;

    /**
     * Changes user password after validating his current password and then logs out the user.
     */
    @EmitsGroupNotifications(GroupNotificationType.PASSWORD_CHANGE_SUCCESS)
    @Transactional
    public void changePassword(PasswordChangeDTO dto) {
        // check if user is signed in
        User currentUser = userService.getCurrentUser();
        // check if the user is active (not locked)???
        UserRepresentation userRepresentation = userAdministrationService.findByIamId(currentUser.getIamIdentifier());
        if (!userRepresentation.isEnabled()) {
            throw UserActionException.create(UserActionError.USER_NOT_ACTIVE);
        }
        // check if current password is valid in keycloak
        boolean isPasswordValid = passwordValidator.validate(new PasswordValidationRequest(dto.getCurrentPassword()));
        if (!isPasswordValid) {
            throw UserActionException.create(UserActionError.INVALID_PASSWORD);
        }
        // reset password in keycloak
        serviceAccountAuthorizationService.resetUserPassword(userRepresentation.getEmail(), dto.getNewPassword());
        // emit event
        String urid = currentUser.getUrid();
        eventService.createAndPublishEvent(urid, urid, "", EventType.USER_PASSWORD_CHANGED, "Change password");
        // sign out user in keycloak
        serviceAccountAuthorizationService.logoutUser(currentUser.getIamIdentifier());
    }
}
