package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class JuniorAdminCanOnlyAssignToAnotherJuniorAdminRuleTest {

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
        ErrorBody errorBody = new JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }
    
    @Test
    void permit() {
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        Task task = new Task();
        task.setType(RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST);
        ruleInfo.setTask(task);
        taskBusinessRuleInfoList.add(ruleInfo);

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskAssigneeRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setUserRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule rule = new JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        slice.setTaskAssigneeRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        for (RequestType taskType: RequestType.values()) {
            slice.setTaskAssigneeRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
            task = new Task();
            task.setType(taskType);
            slice.getTaskBusinessRuleInfoList().get(0).setTask(task);
            securityStore.setTaskBusinessSecurityStoreSlice(slice);
            rule = new JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule(securityStore);
            if (taskType.equals(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD)) {
                assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
            } else {
                assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
            }
        }
    }
}
