package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
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

class RegistryAdminCanApproveTaskWhenAccountNotClosedRuleTest {
    @Mock
    private User user;

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setUser(user);
    }

    @Test
    void error() {
        ErrorBody errorBody = new RegistryAdminCanApproveTaskWhenAccountNotClosedRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        Task task = new Task();
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(task);

        taskBusinessRuleInfoList.add(ruleInfo);

        securityStore.setUserRoles(Arrays.asList(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        for (AccountStatus status : AccountStatus.values()) {
            Account account = new Account();
            account.setAccountStatus(status);
            securityStore.setAccount(account);
            RegistryAdminCanApproveTaskWhenAccountNotClosedRule rule = new RegistryAdminCanApproveTaskWhenAccountNotClosedRule(securityStore);
            if (status.equals(AccountStatus.CLOSED)) {
                assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
            } else {
                assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
            }
        }
    }
}
