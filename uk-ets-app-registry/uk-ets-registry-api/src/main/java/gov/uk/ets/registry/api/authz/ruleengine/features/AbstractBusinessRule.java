package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.util.CollectionUtils;

public abstract class AbstractBusinessRule implements BusinessRule {

    protected final User user;
    protected final List<UserRole> userRoles;
    protected final Account account;
    protected final Account trustedAccountCandidate;
    protected final List<TrustedAccount> trustedAccounts;
    protected final List<AccountAccess> accountAccesses;
    protected final User requestedUser;
    protected final List<UserRole> requestedUserRoles;
    protected final List<User> requestedUsers;
    protected final Map<User, List<UserRole>> requestedUsersRoles;
    protected final Set<String> userScopes;
    protected final List<AllocationSummary> allocationEntries;
    protected final OperatorDTO requestedOperatorUpdate;


    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AbstractBusinessRule(
        BusinessSecurityStore businessSecurityStore) {
        this.user = businessSecurityStore.getUser();
        this.userRoles = businessSecurityStore.getUserRoles();
        this.account = businessSecurityStore.getAccount();
        this.trustedAccountCandidate = businessSecurityStore.getTrustedAccountCandidate();
        this.trustedAccounts = businessSecurityStore.getTrustedAccounts();
        this.accountAccesses = businessSecurityStore.getAccountAccesses();
        this.requestedUser = businessSecurityStore.getRequestedUser();
        this.requestedUserRoles = businessSecurityStore.getRequestedUserRoles();
        this.requestedUsers = businessSecurityStore.getRequestedUsers();
        this.requestedUsersRoles = businessSecurityStore.getRequestedUsersRoles();
        this.userScopes = businessSecurityStore.getUserScopes();
        this.allocationEntries = businessSecurityStore.getAllocationEntries();
        this.requestedOperatorUpdate = businessSecurityStore.getRequestedOperatorUpdate();
    }

    /**
     * Return the key of the rule (the name).
     *
     * @see BusinessRule#key()
     */
    @Override
    public String key() {
        return getClass().getName();
    }

    /**
     * Returns the default forbidden outcome.
     *
     * @see BusinessRule#forbiddenOutcome()
     */
    @Override
    public Outcome forbiddenOutcome() {
        return Outcome.builder().result(Result.FORBIDDEN).errorBody(error()).failedOnKey(key()).build();
    }

    /**
     * Checks whether the user has any of the provided roles.
     *
     * @param role The user roles.
     * @return false/true.
     */
    protected boolean hasRole(UserRole... role) {
        return CollectionUtils.containsAny(this.userRoles, Arrays.asList(role));
    }

    /**
     * Checks whether the user is an authorised representative of the account.
     *
     * @param state The account access state.
     * @param right The account access right.
     * @return false/true.
     */
    protected boolean isAuthorisedRepresentative(AccountAccessState state, AccountAccessRight right) {
        return isAuthorisedRepresentative(account, state, right);
    }

    /**
     * Checks whether the user is an authorised representative of the account.
     *
     * @param account The account.
     * @param state The account access state.
     * @param right The account access right.
     * @return false/true.
     */
    protected boolean isAuthorisedRepresentative(Account account, AccountAccessState state, AccountAccessRight right) {
        return accountAccesses.stream()
            .filter(access -> access.getState().equals(state))
            .filter(access -> access.getAccount().equals(account))
            .filter(access -> access.getUser().equals(user))
            .anyMatch(access -> access.getRight().containsRight(right));
    }
}
