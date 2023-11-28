package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRuleTest {

    @Mock
    private User user;

    @Mock
    private Account account;

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(account);
        securityStore.setUser(user);
    }

    @Test
    void error() {
        ErrorBody errorBody = new ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        taskBusinessRuleInfoList.add(ruleInfo);

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.REJECTED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule rule =
                new ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setUserRoles(Arrays.asList(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        rule =
                new ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        AccountAccess accountAccess = new AccountAccess();

        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));
        accountAccess.setAccount(account);
        accountAccess.setUser(user);
        accountAccess.setRight(AccountAccessRight.APPROVE);
        accountAccess.setState(AccountAccessState.ACTIVE);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));
        accountAccess.setAccount(account);
        accountAccess.setUser(user);
        accountAccess.setRight(AccountAccessRight.INITIATE);
        accountAccess.setState(AccountAccessState.ACTIVE);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));
        accountAccess.setAccount(account);
        accountAccess.setUser(user);
        accountAccess.setRight(AccountAccessRight.READ_ONLY);
        accountAccess.setState(AccountAccessState.ACTIVE);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));
        accountAccess.setAccount(account);
        accountAccess.setUser(user);
        accountAccess.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        accountAccess.setState(AccountAccessState.ACTIVE);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}
