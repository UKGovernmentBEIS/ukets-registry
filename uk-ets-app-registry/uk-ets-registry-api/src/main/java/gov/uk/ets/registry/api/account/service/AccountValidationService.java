package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AccountValidationService {

    private final AuthorizationService authorizationService;
    private final UserService userService;

    /**
     * Validation checks for the account closure request.
     *
     * @param account The account.
     */
    boolean checkAccountClosureRequestEligibility(Account account) {

        if (Set.of(AccountStatus.CLOSURE_PENDING,
                   AccountStatus.CLOSED,
                   AccountStatus.ALL_TRANSACTIONS_RESTRICTED).contains(account.getAccountStatus())) {
            return false;
        }
        return checkClosureRequestBasedOnRole(account);
    }

    /**
     * Validations for the account status change operation.
     *
     * @param accountStatus The account status.
     * @param requestChangeStatus The account status request option.
     */
    void validateAccountStatusChange(AccountStatus accountStatus, AccountStatus requestChangeStatus) {
                 
        if (AccountStatus.CLOSED.equals(accountStatus)) {
            throw AccountActionException
                .create(AccountActionError
                    .builder()
                    .code(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED)
                    .message("Accounts with status CLOSED cannot be updated.")
                    .build());
        }
    	 
		if (AccountStatus.CLOSURE_PENDING.equals(accountStatus)) {
			if (AccountStatus.OPEN.equals(requestChangeStatus)) {
				throw AccountActionException
					.create(AccountActionError
						.builder()
						.code(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED)
						.message("You cannot remove restrictions for an account with outstanding closure requests.")
						.build());
			}
			if (AccountStatus.SOME_TRANSACTIONS_RESTRICTED.equals(requestChangeStatus)) {
				throw AccountActionException
						.create(AccountActionError
							.builder()
							.code(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED)
							.message("You cannot restrict some transactions for an account with "
									+ "outstanding closure requests.")
							.build());
			}
			if (AccountStatus.SUSPENDED_PARTIALLY.equals(requestChangeStatus)) {
				throw AccountActionException
					.create(AccountActionError
						.builder()
						.code(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED)
						.message("You cannot partially suspend an account with outstanding closure requests.")
						.build());
			}
		}
    	 
    	 
		if (AccountStatus.TRANSFER_PENDING.equals(accountStatus)) {
			throw AccountActionException
					.create(AccountActionError
						.builder()
						.code(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED)
						.message("Accounts with status TRANSFER PENDING cannot be updated.")
						.build());
		}

		if (AccountStatus.ALL_TRANSACTIONS_RESTRICTED.equals(accountStatus)) {
			throw AccountActionException
					.create(AccountActionError
						.builder()
						.code(AccountActionError.ACCOUNT_STATUS_CHANGE_NOT_ALLOWED)
						.message("You cannot remove restrictions for an account for which "
								+ "all transactions have been restricted by the system.")
						.build());
		}
    }

    private boolean checkClosureRequestBasedOnRole(Account account) {

        User currentUser = userService.getCurrentUser();
        List<UserRole> roles = authorizationService
            .getClientLevelRoles(currentUser.getIamIdentifier())
            .stream()
            .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
            .collect(Collectors.toList());

        if (roles.stream().anyMatch(UserRole::isSeniorRegistryAdministrator)) {
            return checkClosureRequestBasedOnAccountType(account, true, false, currentUser);
        } else if (roles.stream().anyMatch(UserRole::isJuniorAdministrator)) {
            return checkClosureRequestBasedOnAccountType(account, false, true, currentUser);
        } else if (roles.stream().anyMatch(UserRole::isAuthorizedRepresentative)) {
            return checkClosureRequestBasedOnAccountType(account, false, false, currentUser);
        }
        else return false;
    }

    private boolean checkClosureRequestBasedOnAccountType(Account account, boolean isSeniorAdmin, boolean isJuniorAdmin, User currentUser) {

        AccountType accountType = Optional.ofNullable(AccountType.get(account.getRegistryAccountType(),
                                                                      account.getKyotoAccountType()))
                                          .orElseThrow(() -> new UkEtsException("Account type is invalid"));
        if (isSeniorAdmin) {
            Optional<AccountType> isKPGovernment = AccountType.getAllKyotoGovernmentTypes().stream()
                    .filter(at -> at.equals(accountType)).findFirst();
            return isKPGovernment.isPresent() || !accountType.isGovernmentAccount();
        }
        if (!isJuniorAdmin) {
            if (accountType.isOHAorAOHA() || accountType.isGovernmentAccount()) {
                return false;
            }
            return checkClosureRequestBasedOnAccountAccess(account, currentUser);
        }
        return !accountType.isGovernmentAccount();
    }

    private boolean checkClosureRequestBasedOnAccountAccess(Account account, User currentUser) {
        return account.getAccountAccesses().stream()
                .filter(ac-> ac.getUser().getId().equals(currentUser.getId())
                        && ac.getAccount().getId().equals(account.getId())
                        && ac.getRight().equals(AccountAccessRight.READ_ONLY))
                .findFirst()
                .isEmpty();
    }

	boolean checkExcludedFromBillingRequest(Account account) {
		if (!AccountType.getTypesWithBillingDetails().contains(AccountType.get(account.getAccountType()))) {
			return false;
		}
		if (Set.of(AccountStatus.SUSPENDED, AccountStatus.CLOSED, AccountStatus.TRANSFER_PENDING)
				.contains(account.getAccountStatus())) {
			return false;
		}
		return true;
	}
}
