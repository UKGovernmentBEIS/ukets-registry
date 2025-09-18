package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.UserAttributes;
import gov.uk.ets.registry.api.user.profile.domain.EmailChangeBooleanExpressionFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

/**
 * Email change request checker.
 */
@Component
@RequiredArgsConstructor
public class EmailChangeChecker {
    private final EmailChangeBooleanExpressionFactory emailChangeBooleanExpressionFactory;
    private final TaskRepository taskRepository;
    private final UserAdministrationService userAdministrationService;

    /**
     * Returns flag that indicates if other current user pending request exist.
     * @return The flag
     */
    public boolean otherCurrentUserPendingRequests() {
        long currentUserPendingEmailChangeRequests = taskRepository
            .count(emailChangeBooleanExpressionFactory.getCurrentUserPendingEmailChangesExpression());
        return currentUserPendingEmailChangeRequests > 0;
    }

    /**
     * Returns flag that indicates if other pending email change requests of the user of urid exist.
     * @param urid The user unique business identifier
     * @return The flag
     */
    public boolean otherPendingRequestsOfUser(String urid) {
        long currentUserPendingEmailChangeRequests = taskRepository
            .count(emailChangeBooleanExpressionFactory.getOtherUserPendingEmailChangesExpression(urid));
        return currentUserPendingEmailChangeRequests > 0;
    }

    /**
     * Returns flag that indicates if other pending email change requests with same new email exist.
     * @param newEmail The new email
     * @return The flag
     */
    public boolean pendingRequestsWithSameNewEmail(String newEmail) {
        return !taskRepository.findChangeEmailTasksByNewEmail(newEmail).isEmpty();
    }

    /**
     * Returns flag that indicates if the new email is other user's working email address.
     * @param newEmail The new email
     * @return The flag
     */
    public boolean otherUserHasNewEmailAsWorkingEmail(String newEmail) {
       return userAdministrationService.userExists(newEmail);
    }

    /**
     * Returns flag that indicates if the new email is used as a recovery email already.
     * @param newEmail The new email
     * @param userIamIdentifier The user unique identifier
     * @return The flag
     */
    public boolean sameEmailWithRecoveryEmail(String newEmail, String userIamIdentifier) {
        UserRepresentation userRepresentation = userAdministrationService.findByIamId(userIamIdentifier);
        return userRepresentation.getAttributes()
            .getOrDefault(UserAttributes.RECOVERY_EMAIL_ADDRESS.getAttributeName(), List.of())
            .stream()
            .anyMatch(recoveryEmail -> Objects.equals(newEmail, recoveryEmail));
    }
}
