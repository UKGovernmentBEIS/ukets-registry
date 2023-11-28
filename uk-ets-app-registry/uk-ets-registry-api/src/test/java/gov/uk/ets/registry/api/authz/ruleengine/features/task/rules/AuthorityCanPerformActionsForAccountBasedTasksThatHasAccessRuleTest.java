package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRuleTest {

    @Mock
    private User user;

    private BusinessSecurityStore securityStore;
    private TaskBusinessSecurityStoreSlice slice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        slice = new TaskBusinessSecurityStoreSlice();
    }

    @Test
    void error() {
        ErrorBody errorBody = new AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void notApplicableAdministratorWithOneAccountAccess() {
        Account account = constructAccount(1L,AccountType.OPERATOR_HOLDING_ACCOUNT);

        slice.setTaskBusinessRuleInfoList(List.of(createTaskBusinessRuleInfo(account)));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setAccountAccesses(List.of(createAccountAccess(1L,AccountAccessRight.ROLE_BASED, account)));
        securityStore.setUserRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule rule =
            new AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
    }

    @Test
    void shouldForbidAuthorityWithMultipleAccountAccesses() {
        Account account1 = constructAccount(1L,AccountType.UK_ALLOCATION_ACCOUNT);
        Account account2 = constructAccount(2L,AccountType.UK_SURRENDER_ACCOUNT);
        slice.setTaskBusinessRuleInfoList(List.of(createTaskBusinessRuleInfo(account1),
                                                  createTaskBusinessRuleInfo(account2)));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setAccountAccesses(List.of(createAccountAccess(1L,AccountAccessRight.READ_ONLY, account1),
                                                 createAccountAccess(2L,AccountAccessRight.ROLE_BASED, account2)));
        securityStore.setUserRoles(List.of(UserRole.AUTHORITY_USER));
        AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule rule =
            new AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void shouldForbidAuthorityWithRoleBasedAccess() {
        Account account1 = constructAccount(1L,AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        Account account2 = constructAccount(2L,AccountType.UK_DELETION_ACCOUNT);
        slice.setTaskBusinessRuleInfoList(List.of(createTaskBusinessRuleInfo(account1),
                                                  createTaskBusinessRuleInfo(account2)));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setAccountAccesses(List.of(createAccountAccess(1L,AccountAccessRight.ROLE_BASED, account1)));
        securityStore.setUserRoles(List.of(UserRole.AUTHORITY_USER));
        AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule rule =
            new AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    private AccountAccess createAccountAccess(long id, AccountAccessRight right, Account account) {
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setId(id);
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setUser(user);
        accountAccess.setRight(right);
        accountAccess.setAccount(account);
        return accountAccess;
    }

    private TaskBusinessRuleInfo createTaskBusinessRuleInfo(Account account) {
        Task task = new Task();
        task.setAccount(account);
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(task);
        return ruleInfo;
    }

    private Account constructAccount(long accountId, AccountType accountType) {
        Account account = new Account();
        account.setId(accountId);
        account.setIdentifier(accountId);
        account.setFullIdentifier("UK-100-10000047-2-14");
        account.setAccountName("Account Name");
        account.setRegistryAccountType(accountType.getRegistryType());
        account.setKyotoAccountType(accountType.getKyotoType());
        account.setAccountType(accountType.name());
        return account;
    }
}
