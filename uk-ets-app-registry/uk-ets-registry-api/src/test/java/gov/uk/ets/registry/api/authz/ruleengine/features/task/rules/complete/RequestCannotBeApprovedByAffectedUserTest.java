package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RequestCannotBeApprovedByAffectedUserTest {

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
        ErrorBody errorBody = new RequestCannotBeApprovedByAffectedUser(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }
    
    @Test
    void permit() {
        User affectedUser = new User();
        affectedUser.setId(123456L);
        Task task = new Task();
        task.setUser(affectedUser);
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(task);
        
        taskBusinessRuleInfoList.add(ruleInfo);

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        when(user.getId()).thenReturn(123456L);
        RequestCannotBeApprovedByAffectedUser rule = new RequestCannotBeApprovedByAffectedUser(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
        
        when(user.getId()).thenReturn(654321L);
        rule = new RequestCannotBeApprovedByAffectedUser(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
        
    }
}
