package gov.uk.ets.registry.api.account.authz;

import static gov.uk.ets.registry.api.account.domain.types.AccountAccessState.ACTIVE;
import static gov.uk.ets.registry.api.account.domain.types.AccountAccessState.SUSPENDED;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountAccessDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AccountAuthorizationServiceImpl implements AccountAuthorizationService {
    /**
     * Service for users.
     */
    private final UserService userService;
    /**
     * Service for account access rights.
     */
    private final AccountAccessRepository accountAccessRepository;

    AccountAuthorizationServiceImpl(UserService userService, AccountAccessRepository accountAccessRepository) {
        this.userService = userService;
        this.accountAccessRepository = accountAccessRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccountAccessRight checkAccountAccess(Long accountIdentifier) throws AccountActionException {
        User user = userService.getCurrentUser();
        if (user == null) {
            throw AccountActionException.create(AccountActionError.builder()
                .code(AccountActionError.ACCESS_NOT_ALLOWED)
                .message("You are not allowed to view this account")
                .build());
        }
        return Optional.ofNullable(accountAccessRepository
            .findByAccount_IdentifierAndUser_Urid(accountIdentifier, user.getUrid()))
            .filter(ac -> ACTIVE.equals(ac.getState()))
            .map(AccountAccess::getRight)
            .orElse(null);
    }

    @Override
    public boolean checkAccountAccess(Long accountIdentifier, AccountAccessRight queriedAccessRight) {
        AccountAccessRight right = checkAccountAccess(accountIdentifier);
        if (right == null) {
            return false;
        }
        return right.containsRight(queriedAccessRight);
    }

    @Override
    public boolean checkAccountAccess(Long accountIdentifier, String urid, AccountAccessRight queriedAccessRight) {
        AccountAccessRight right = Optional.ofNullable(accountAccessRepository
            .findByAccount_IdentifierAndUser_Urid(accountIdentifier, urid))
            .filter(ac -> ACTIVE.equals(ac.getState()))
            .map(AccountAccess::getRight)
            .orElse(null);
        if (right == null) {
            return false;
        }
        return right.containsRight(queriedAccessRight);
    }

    @Override
    public List<AccountAccessDTO> getAccountAccessesForCurrentUser() {
        User user = userService.getCurrentUser();
        if (user != null) {
            return accountAccessRepository
                .findArsByUridAndStateInWithAccount(user.getUrid(), List.of(ACTIVE))
                .stream()
                .map(ac -> new AccountAccessDTO(ac.getAccount().getFullIdentifier(), ac.getRight()))
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<AccountAccessDTO> getActiveOrSuspendedAccountAccessesForUrid(String urid) {
        return accountAccessRepository
            .findArsByUridAndStateInWithAccount(urid, List.of(ACTIVE, SUSPENDED))
            .stream()
            .map(ac -> new AccountAccessDTO(ac.getAccount().getFullIdentifier(), ac.getRight()))
            .collect(Collectors.toList());
    }
}
