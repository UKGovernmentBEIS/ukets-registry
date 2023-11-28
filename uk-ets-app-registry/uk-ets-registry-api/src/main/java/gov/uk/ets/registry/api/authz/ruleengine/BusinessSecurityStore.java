package gov.uk.ets.registry.api.authz.ruleengine;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.AccountSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.AccountHolderSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.allocation.AllocationSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.TransactionBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.EmailChangeSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.StatusChangeSecurityStoreSlice;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Contains contextual information that is used for business rules.
 */
@Getter
@Setter
public class BusinessSecurityStore {
    private User user;
    private List<UserRole> userRoles;
    private Account account;
    private Account trustedAccountCandidate;
    private List<TrustedAccount> trustedAccounts;
    private AccountHolder accountHolder;
    private List<AccountAccess> accountAccesses;
    private Set<String> userScopes;
    private TaskBusinessSecurityStoreSlice taskBusinessSecurityStoreSlice;
    private ARBusinessSecurityStoreSlice arUpdateStoreSlice;
    private TransactionBusinessSecurityStoreSlice transactionBusinessSecurityStoreSlice;
    private AccountHolderSecurityStoreSlice accountHolderSecurityStoreSlice;
    private AccountSecurityStoreSlice accountSecurityStoreSlice;
    private User requestedUser;
    private List<User> requestedUsers;
    private List<UserRole> requestedUserRoles;
    private Map<User, List<UserRole>> requestedUsersRoles;
    private EmailChangeSecurityStoreSlice emailChangeSecuritySlice;
    private Boolean otpValid;
    private Task task;
    private int maxNumOfARs;
    private int minNumOfARs;
    private List<AllocationSummary> allocationEntries;
    private InstallationOrAircraftOperatorDTO requestedOperatorUpdate;
    private StatusChangeSecurityStoreSlice statusChangeSecurityStoreSlice;
    private AllocationSecurityStoreSlice allocationSecurityStoreSlice;
}
