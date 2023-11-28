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
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakAuthorityUserRoleModifiedEventHandler implements KeycloakEventHandler {

    private final KeycloakEventHelper helper;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountAccessRepository accountAccessRepository;

    @Override
    public boolean canHandle(KeycloakEvent event) {
        return helper.isClientRoleMappingEvent(event) &&
            isAuthorityUserRoleMapping(event) &&
            isAdditionOrDeletionMapping(event);
    }

    @Override
    public void handle(KeycloakEvent event) {
        String userIamIdentifier = helper.extractIamIdentifier(event.getResourcePath());
        User user = userRepository.findByIamIdentifier(userIamIdentifier);

        if (event.getOperationType().equals(OperationType.CREATE)) {
            // retrieve all ets accounts besides the central ones
            Iterable<Account> etsAccounts = findEtsExceptCentralAccounts();
            List<AccountAccess> accountAccesses = StreamSupport.stream(etsAccounts.spliterator(), false)
                    .map(etsAccount -> {
                        AccountAccess accountAccess = new AccountAccess();
                        accountAccess.setState(AccountAccessState.ACTIVE);
                        accountAccess.setAccount(etsAccount);
                        accountAccess.setUser(user);
                        accountAccess.setRight(AccountAccessRight.ROLE_BASED);
                        return accountAccess;
                    })
                    .collect(toList());
            accountAccessRepository.saveAll(accountAccesses);
        } else if (event.getOperationType().equals(OperationType.DELETE)) {
            List<AccountAccess> accountAccessesToDelete =
                accountAccessRepository.findByUser_Urid(user.getUrid()).stream()
                    .filter(accessRight -> accessRight.getRight().equals(AccountAccessRight.ROLE_BASED))
                    .collect(toList());
            accountAccessRepository.deleteInBatch(accountAccessesToDelete);
        }

    }

    private boolean isAuthorityUserRoleMapping(KeycloakEvent event) {
        AdminEventRepresentation representation = helper.deserializeRepresentationValues(event.getRepresentation());
        return UserRole.AUTHORITY_USER.getKeycloakLiteral().equals(representation.getName());
    }

    private boolean isAdditionOrDeletionMapping(KeycloakEvent event) {
        return event.getOperationType() != null && (event.getOperationType().equals(OperationType.CREATE) ||
            event.getOperationType().equals(OperationType.DELETE));
    }

    private Iterable<Account> findEtsExceptCentralAccounts() {
        return accountRepository.findAll(
           QAccount.account.accountType
               .in(AccountType.getEtsExceptCentralAccountTypes().stream()
                    .map(AccountType::getLabel)
                    .collect(toList())
            )
        );
    }
}
