package gov.uk.ets.registry.api.accountaccess.service;

import gov.uk.ets.registry.api.account.authz.AccountAuthorizationService;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountAccessService {

    private final AccountAccessRepository accountAccessRepository;
    private final AuthorizationService authorizationService;
    private final AccountAuthorizationService accountAuthorizationService;
    private final UserService userService;


    /**
     * Creates an account access entry for a user or set of users.
     *
     * @param account   the account.
     * @param userRoles the user roles.
     */
    public void createAccountAccess(Account account, List<String> userRoles, AccountAccessRight accessRight) {
        List<User> users = userService.findUsersByRoleName(userRoles);
        if (CollectionUtils.isNotEmpty(users)) {
            users.forEach(u -> {
                AccountAccess accountAccess = new AccountAccess();
                accountAccess.setAccount(account);
                accountAccess.setState(AccountAccessState.ACTIVE);
                accountAccess.setUser(u);
                accountAccess.setRight(accessRight);
                accountAccessRepository.save(accountAccess);
            });
        }
    }

    /**
     * Checks if the user with userId has access to the account.
     *
     * @param accountIdentifier The account identifier.
     * @return If user has account access to account.
     */
    public boolean checkAccountAccess(Long accountIdentifier) {
        if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            return accountAuthorizationService.checkAccountAccess(accountIdentifier,
                userService.getCurrentUser().getUrid(),
                AccountAccessRight.ROLE_BASED);
        } else {
            return accountAuthorizationService.checkAccountAccess(accountIdentifier,
                userService.getCurrentUser().getUrid(),
                AccountAccessRight.READ_ONLY);
        }
    }

    /**
     * Retrieves all account accesses for a user (depending if he is an admin or not).
     */
    public List<AccountAccess> getAllActiveAccessesForCurrentUser() {
        AccountAccessRight right;
        if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            right = AccountAccessRight.ROLE_BASED;
        } else {
            right = AccountAccessRight.READ_ONLY;
        }
        return accountAccessRepository.getByUser_Urid(userService.getCurrentUser().getUrid()).stream()
            .filter(accountAccess -> accountAccess.getState().equals(AccountAccessState.ACTIVE))
            .filter(accountAccess -> accountAccess.getRight().containsRight(right))
            .collect(Collectors.toList());
    }

    public boolean hasSurrenderRight(Long accountId) {
        AccountAccess userAccess =
            accountAccessRepository.findByAccount_IdentifierAndUser_Urid(accountId,
                userService.getCurrentUser().getUrid());

        return Optional.ofNullable(userAccess)
            .map(AccountAccess::getRight)
            .filter(right -> right == AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE)
            .isPresent();
    }
}
