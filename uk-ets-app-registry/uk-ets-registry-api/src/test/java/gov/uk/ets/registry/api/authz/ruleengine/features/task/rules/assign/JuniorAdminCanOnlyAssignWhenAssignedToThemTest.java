package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import org.mockito.MockitoAnnotations;

class JuniorAdminCanOnlyAssignWhenAssignedToThemTest {

    private BusinessSecurityStore securityStore;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        task = new Task();
        user.setId(1L);
        securityStore = new BusinessSecurityStore();
        securityStore.setUser(user);
    }

    @Test
    void error() {
        ErrorBody errorBody = new JuniorAdminCanOnlyAssignWhenAssignedToThem(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void forbidJuniorAdminAssignUnclaimedTask() {
        securityStore.setUserRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        JuniorAdminCanOnlyAssignWhenAssignedToThem rule = new JuniorAdminCanOnlyAssignWhenAssignedToThem(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void permitJuniorAdminReassignTaskAssignedToHim() {
        securityStore.setUserRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        task.setClaimedBy(user);
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        JuniorAdminCanOnlyAssignWhenAssignedToThem rule = new JuniorAdminCanOnlyAssignWhenAssignedToThem(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }

    @Test
    void notApplicableSeniorAdminCase() {
        securityStore.setUserRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));

        JuniorAdminCanOnlyAssignWhenAssignedToThem rule = new JuniorAdminCanOnlyAssignWhenAssignedToThem(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
    }
}
