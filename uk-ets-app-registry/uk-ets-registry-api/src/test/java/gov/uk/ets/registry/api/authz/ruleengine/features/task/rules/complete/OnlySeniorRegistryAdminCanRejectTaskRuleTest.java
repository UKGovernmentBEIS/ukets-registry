package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OnlySeniorRegistryAdminCanRejectTaskRuleTest {

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
        ErrorBody errorBody = new OnlySeniorRegistryAdminCanRejectTaskRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        taskBusinessRuleInfoList.add(ruleInfo);

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        securityStore.setUserRoles(Arrays.asList(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        OnlySeniorRegistryAdminCanRejectTaskRule rule = new OnlySeniorRegistryAdminCanRejectTaskRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        slice.setTaskOutcome(TaskOutcome.REJECTED);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setUserRoles(Arrays.asList(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        rule = new OnlySeniorRegistryAdminCanRejectTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        rule = new OnlySeniorRegistryAdminCanRejectTaskRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}
