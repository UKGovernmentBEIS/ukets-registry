package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

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
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountARsLimitShouldNotBeExceededRuleTest {

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
    }

    @Test
    void error() {
        ErrorBody errorBody = new AccountARsLimitShouldNotBeExceededRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        Account account = new Account();
        List<AccountAccess> list = new ArrayList();

        for (int i = 0; i < 8; i++) {
            AccountAccess accountAccess = new AccountAccess();
            User user = new User();
            user.setId((long) (i + 1));
            accountAccess.setUser(user);
            accountAccess.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
            accountAccess.setState(AccountAccessState.ACTIVE);
            list.add(accountAccess);
        }

        for (int i = 8; i < 9; i++) {
            AccountAccess accountAccess = new AccountAccess();
            User user = new User();
            user.setId((long) (i + 1));
            accountAccess.setUser(user);
            accountAccess.setRight(AccountAccessRight.ROLE_BASED);
            accountAccess.setState(AccountAccessState.ACTIVE);
            list.add(accountAccess);
        }
        account.setAccountAccesses(list);

        Task task = new Task();
        task.setType(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST);
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setAccount(account);

        // Account has 8 ARs and the maximum number of allowed ARs is 8
        securityStore.setMaxNumOfARs(8);
        AccountARsLimitShouldNotBeExceededRule rule = new AccountARsLimitShouldNotBeExceededRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        // Account has 8 ARs and the maximum number of allowed ARs is 8 and the task gets rejected
        slice.setTaskOutcome(TaskOutcome.REJECTED);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new AccountARsLimitShouldNotBeExceededRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        // Account has 8 ARs and the maximum number of allowed ARs is 9
        securityStore.setMaxNumOfARs(9);
        rule = new AccountARsLimitShouldNotBeExceededRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        // Account has 8 ARs, the maximum number of allowed ARs is 8 and the request type is AR access rights update
        task.setType(RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setMaxNumOfARs(8);
        rule = new AccountARsLimitShouldNotBeExceededRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}
