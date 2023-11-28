package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRuleTest {

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
        ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRule rule = new ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRule(securityStore);
        assertThrows(UnsupportedOperationException.class, rule::error);
    }

    @Test
    void permit() {
        Task parentTask = new Task();
        parentTask.setRequestId(1234L);

        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(parentTask);
        Task subTask1 = new Task();
        subTask1.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        List<Task> subTasks = new ArrayList<>();
        subTasks.add(subTask1);
        ruleInfo.setSubTasks(subTasks);
        taskBusinessRuleInfoList.add(ruleInfo);

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRule rule = new ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());


        taskBusinessRuleInfoList = new ArrayList<>();
        ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(parentTask);
        subTask1 = new Task();
        subTask1.setStatus(RequestStateEnum.APPROVED);
        subTasks = new ArrayList<>();
        subTasks.add(subTask1);
        ruleInfo.setSubTasks(subTasks);
        taskBusinessRuleInfoList.add(ruleInfo);

        slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        rule = new ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        taskBusinessRuleInfoList = new ArrayList<>();
        ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(parentTask);
        subTask1 = new Task();
        subTask1.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        subTasks = new ArrayList<>();
        subTasks.add(subTask1);
        ruleInfo.setSubTasks(subTasks);
        taskBusinessRuleInfoList.add(ruleInfo);

        slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.REJECTED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        rule = new ParentTaskCanBeCompletedOnlyWhenChildTasksAreCompletedRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
    }
}
