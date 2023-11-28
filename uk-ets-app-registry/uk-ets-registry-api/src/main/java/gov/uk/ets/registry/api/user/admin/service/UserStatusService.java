package gov.uk.ets.registry.api.user.admin.service;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.printenrolmentletter.PrintEnrolmentLetterTaskService;
import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.UserActionError;
import gov.uk.ets.registry.api.user.UserActionException;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.admin.shared.UserStatusActionCriteria;
import gov.uk.ets.registry.api.user.admin.shared.UserStatusTransition;
import gov.uk.ets.registry.api.user.admin.web.model.UserStateActionOptionsFactory;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusActionOptionDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeResultDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation for User state service.
 */
@Service
@RequiredArgsConstructor
public class UserStatusService {

    public static final String STATE = "state";
    public static final String SUSPENDED = "SUSPENDED";

    private final UserRepository userRepository;
    private final EventService eventService;
    private final UserAdministrationService userAdministrationService;
    private final PrintEnrolmentLetterTaskService printEnrolmentLetterTaskService;
    /**
     * Service for users.
     */
    private final UserService userService;
    /**
     * The authorization service.
     */
    private final AuthorizationService authorizationService;

    /**
     * Fetch the available actions for changing User state.
     *
     * @param urid
     * @return a list of available options.
     */
    public List<UserStatusActionOptionDTO> getUserStatusAvailableActions(String urid) {
        User user = userRepository.findByUrid(urid);
        //Build the criteria
        UserStatusActionCriteria criteria = UserStatusActionCriteria.
            builder().
            currentState(user.getState()).
            previousState(user.getPreviousState()).
            build();
        return UserStateActionOptionsFactory.createAvailableUserStateActions(criteria).getOptions();
    }

    public KeycloakUser getKeycloakUser(String urid) {
        UserDTO user = userService.getUser(urid);
        KeycloakUser keycloakUser = new KeycloakUser(userAdministrationService.findByIamId(user.getKeycloakId()));
         if (keycloakUser.getEmail() != null) {
             Set<UserRole> userRoles = authorizationService
                     .getClientLevelRoles(user.getKeycloakId())
                     .stream()
                     .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
                     .collect(Collectors.toSet());
            keycloakUser.setEligibleForSpecificActions(
               userRoles.stream().noneMatch(UserRole::isAdminOrAuthorityUser) &&
               !keycloakUser.getAttributes().get(STATE).contains(SUSPENDED)
            );
            keycloakUser.setUserRoles(
               userRoles.stream().map(UserRole::name).collect(Collectors.toSet())
            );
            return keycloakUser;
         }
        return null;
    }

    /**
     * Delegates to {@link #changeUserStatus(UserStatusChangeDTO, String)} by passing a null token
     * which means that the current user should be retrieved from the context.
     */
    @Transactional
    @EmitsGroupNotifications({GroupNotificationType.EMAIL_CHANGE_STATUS})
    public UserStatusChangeResultDTO changeUserStatus(UserStatusChangeDTO patch) {
        return changeUserStatus(patch, null);
    }

    /**
     * <ol>
     * <li>Changes the user’s status to the one specified</li>
     * <li>Generates an event in the history</li>
     * <li>If the user status changes from REGISTERED to VALIDATED, the system also
     * generates the Registry Activation Code and creates a task “Print letter with
     * registry activation code” for the administrator</li>
     * <li>If this is a system action the current user is considered the same as the user</li>
     * </ol>
     */
    public UserStatusChangeResultDTO changeUserStatus(UserStatusChangeDTO patch, String token) {
        User user = userRepository.findByUrid(patch.getUrid());

        //Sanity check
        UserStatusTransition transition = UserStatusTransition.
            builder().
            currentState(user.getState()).
            newState(patch.getUserStatus()).
            comment(patch.getComment()).
            build();
        if (!transition.isValid()) {
            throw UserActionException.create(UserActionError.INVALID_USER_STATUS_TRANSITION);
        }

        //Update the state in Keycloak
        KeycloakUser keycloakUser;
        if (token == null) {
            keycloakUser = userAdministrationService.updateUserState(user.getIamIdentifier(), patch.getUserStatus());
        } else {
            // case where this is a system action so the token is provided by the system
            keycloakUser = userAdministrationService.updateUserState(user.getIamIdentifier(), patch.getUserStatus(), token);
        }

        //When the user is suspended we need to store the previous state
        //so as to roll back upon un-suspend
        if (UserStatus.SUSPENDED.equals(patch.getUserStatus())) {
            user.setPreviousState(user.getState());
        } else {
            user.setPreviousState(null);
        }

        // When a user is restored we need to set the previous state
        // so to avoid sending notifications
        if(UserStatus.SUSPENDED.equals(user.getState())) {
            user.setPreviousState(user.getState());
        }

        User currentUser = retrieveCurrentUserIfAvailable(token);
        //The request identifier of the task if any
        Long requestId = null;
        if (UserStatus.VALIDATED.equals(patch.getUserStatus()) && UserStatus.REGISTERED.equals(user.getState())) {
            user = userService.validateUser(user);
            Task printEnrolmentLetterTask =
                printEnrolmentLetterTaskService.create(user, currentUser, null);
            requestId = printEnrolmentLetterTask.getRequestId();

            eventService
                .createAndPublishEvent(user.getUrid(), currentUser != null ? currentUser.getUrid() : user.getUrid(), "",
                    EventType.REGISTRY_ACTIVATION_CODE, "Registry activation code issued");
        } else {
            user.setState(patch.getUserStatus());
            userRepository.save(user);
        }

        // currentUser is null in case of a system action (e.g. emergency OTP change) where there is no logged in user
        String currentUserUrid = currentUser == null ? user.getUrid() : currentUser.getUrid();
        String action = createActionName(currentUser);

        eventService.createAndPublishEvent(user.getUrid(), currentUserUrid, patch.getUserStatus().name(),
            EventType.USER_STATUS_CHANGED, action);

        if (patch.getComment() != null) {
            eventService
                .createAndPublishEvent(user.getUrid(), currentUserUrid, patch.getComment(),
                    EventType.USER_TASK_COMMENT, String.format("%s (comment)", action));
        }

        return UserStatusChangeResultDTO.
            builder().
            userStatus(user.getState()).
            previousUserStatus(user.getPreviousState()).
            requestId(Optional.ofNullable(requestId)).
            iamIdentifier(user.getIamIdentifier()).
            user(keycloakUser).
            build();
    }

    private User retrieveCurrentUserIfAvailable(String token) {
        return token == null ? userService.getCurrentUser() : null;
    }

    private String createActionName(User currentUser) {
        return "Change user status" + (currentUser == null ? " (initiated by system)" : "");
    }
}
