package gov.uk.ets.registry.api.user.messaging.handlers;

import static java.util.stream.Collectors.toList;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.messaging.AdminEventRepresentation;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.messaging.KeycloakEventHelper;
import gov.uk.ets.registry.api.user.messaging.OperationType;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakAdminRoleModifiedEventHandler implements KeycloakEventHandler {

    private final KeycloakEventHelper helper;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountAccessRepository accountAccessRepository;

    @Override
    public boolean canHandle(KeycloakEvent event) {
        return helper.isClientRoleMappingEvent(event) && 
            isAdminRoleMapping(event) &&
            isAdditionOrDeletionMapping(event);
    }

    @Override
    public void handle(KeycloakEvent event) {
        log.info("Handling event id: {}", event.getId());
        AdminEventRepresentation representation = helper.deserializeRepresentationValues(event.getRepresentation());
        String userIamIdentifier = helper.extractIamIdentifier(event.getResourcePath());
        User user = userRepository.findByIamIdentifier(userIamIdentifier);

        // check if user has other admin roles and if so do not process the event:
        String roleIamIdentifier = representation.getId();
        if (hasUserOtherAdminRoles(user, roleIamIdentifier)) {
            log.info("User {} has OtherAdminRoles", user.getUrid());
            return;
        }

        if (event.getOperationType().equals(OperationType.CREATE)) {
            log.info("Creating Admin user {} accesses", user.getUrid());
            // retrieve all accounts that the admins have access:
            // TODO: list of accounts still pending
            Iterable<Account> nonCentralAccounts = findNonCentralAccounts();
            List<AccountAccess> accountAccesses = StreamSupport.stream(nonCentralAccounts.spliterator(), false)
                .map(account -> {
                    AccountAccess accountAccess = new AccountAccess();
                    accountAccess.setAccount(account);
                    accountAccess.setState(AccountAccessState.ACTIVE);
                    accountAccess.setUser(user);
                    accountAccess.setRight(AccountAccessRight.ROLE_BASED);
                    return accountAccess;
                })
                .collect(toList());
            accountAccessRepository.saveAll(accountAccesses);
        } else if (event.getOperationType().equals(OperationType.DELETE)) {
            log.info("Deleting Admin user {} accesses", user.getUrid());
            accountAccessRepository.deleteByUserAndRight(user, AccountAccessRight.ROLE_BASED);
        }
    }

    private boolean isAdminRoleMapping(KeycloakEvent event) {
        AdminEventRepresentation representation = helper.deserializeRepresentationValues(event.getRepresentation());
        return helper.isAdminRoleMapping(representation.getName());
    }

    private boolean isAdditionOrDeletionMapping(KeycloakEvent event) {
        return event.getOperationType().equals(OperationType.CREATE) ||
            event.getOperationType().equals(OperationType.DELETE);
    }

    private Iterable<Account> findNonCentralAccounts() {
        return accountRepository.findAll(
            QAccount.account.accountType
                .notIn(Stream.concat(
                            AccountType.getCentralTypes().stream(),
                            AccountType.getReadOnlyCentralTypes().stream()
                        )
                        .map(AccountType::getLabel)
                        .collect(toList())
                )
        );
    }

    /**
     * Checks current user roles, excluding the one that is being removed.
     * Return true if it finds other admin roles.
     */
    private boolean hasUserOtherAdminRoles(User user, String roleIamIdentifier) {
        return user.getUserRoles()
            .stream()
            .filter(urm -> !urm.getRole().getIamIdentifier().equals(roleIamIdentifier))
            .map(urm -> urm.getRole().getRoleName())
            .filter(UserRole::belongsToRegistryRoles)
            .anyMatch(roleName -> UserRole.getAdminRoles().contains(UserRole.fromKeycloakLiteral(roleName)));
    }
}
