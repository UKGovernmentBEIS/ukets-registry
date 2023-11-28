package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ARsOfSameAccountTaskRuleTest {
    private BusinessSecurityStore securityStore;

    @Mock
    private User user;

    @Mock
    private Account account;

    @Mock
    private List<UserRole> userRoles;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(account);
        securityStore.setUser(user);
        securityStore.setUserRoles(userRoles);
    }

    @Test
    void error() {
        ErrorBody errorBody = new ARsOfSameAccountTaskRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        Task task = new Task();
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);

        securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        ARsOfSameAccountTaskRule rule = new ARsOfSameAccountTaskRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsOfSameAccountTaskRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.AUTHORITY_USER));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsOfSameAccountTaskRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.READONLY_ADMINISTRATOR));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsOfSameAccountTaskRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        /*
         * Assignee is AR of the account with ACTIVE account access
         */
        taskBusinessRuleInfoList = new ArrayList<>();
        ruleInfo = new TaskBusinessRuleInfo();
        task = new Task();
        Account account = new Account();
        AccountAccess accountAccess = new AccountAccess();
        User aaUser = new User();
        aaUser.setId(1L);
        accountAccess.setUser(aaUser);
        accountAccess.setState(AccountAccessState.ACTIVE);
        account.setAccountAccesses(List.of(accountAccess));
        task.setAccount(account);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        User user = new User();
        user.setId(1L);
        slice.setTaskAssignee(user);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsOfSameAccountTaskRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        /*
         * Assignee is AR of the account with SUSPENDED account access
         */
        taskBusinessRuleInfoList = new ArrayList<>();
        ruleInfo = new TaskBusinessRuleInfo();
        task = new Task();
        account = new Account();
        accountAccess = new AccountAccess();
        aaUser = new User();
        aaUser.setId(1L);
        accountAccess.setUser(aaUser);
        accountAccess.setState(AccountAccessState.SUSPENDED);
        account.setAccountAccesses(List.of(accountAccess));
        task.setAccount(account);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        user = new User();
        user.setId(1L);
        slice.setTaskAssignee(user);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsOfSameAccountTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        /*
         * Assignee is NOT AR of the account
         */
        taskBusinessRuleInfoList = new ArrayList<>();
        ruleInfo = new TaskBusinessRuleInfo();
        task = new Task();
        account = new Account();
        accountAccess = new AccountAccess();
        aaUser = new User();
        aaUser.setId(2L);
        accountAccess.setUser(aaUser);
        accountAccess.setState(AccountAccessState.ACTIVE);
        account.setAccountAccesses(List.of(accountAccess));
        task.setAccount(account);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        user = new User();
        user.setId(1L);
        slice.setTaskAssignee(user);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsOfSameAccountTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }
}
