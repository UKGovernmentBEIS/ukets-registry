package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OnlyEnrolledUsersCanCompleteTaskRuleTest {

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

        ErrorBody errorBody = new OnlyEnrolledUsersCanCompleteTaskRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        User testUser = new User();
        testUser.setState(UserStatus.VALIDATED);
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        Task task = new Task();
        task.setType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        securityStore.setUser(testUser);
        OnlyEnrolledUsersCanCompleteTaskRule rule = new OnlyEnrolledUsersCanCompleteTaskRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        taskBusinessRuleInfoList = new ArrayList<>();
        ruleInfo = new TaskBusinessRuleInfo();
        task.setType(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new OnlyEnrolledUsersCanCompleteTaskRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        taskBusinessRuleInfoList = new ArrayList<>();
        ruleInfo = new TaskBusinessRuleInfo();
        task.setType(RequestType.ALLOCATION_REQUEST);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new OnlyEnrolledUsersCanCompleteTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        testUser.setState(UserStatus.ENROLLED);
        taskBusinessRuleInfoList = new ArrayList<>();
        ruleInfo = new TaskBusinessRuleInfo();
        task.setType(RequestType.ALLOCATION_REQUEST);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setUser(testUser);
        rule = new OnlyEnrolledUsersCanCompleteTaskRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}
