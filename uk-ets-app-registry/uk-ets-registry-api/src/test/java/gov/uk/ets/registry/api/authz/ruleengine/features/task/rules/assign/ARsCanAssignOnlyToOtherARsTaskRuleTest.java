package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ARsCanAssignOnlyToOtherARsTaskRuleTest {
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
        ErrorBody errorBody = new ARsCanAssignOnlyToOtherARsTaskRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        taskBusinessRuleInfoList.add(ruleInfo);

        securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        ARsCanAssignOnlyToOtherARsTaskRule rule = new ARsCanAssignOnlyToOtherARsTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsCanAssignOnlyToOtherARsTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.AUTHORITY_USER));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsCanAssignOnlyToOtherARsTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.READONLY_ADMINISTRATOR));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsCanAssignOnlyToOtherARsTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskAssigneeRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        rule = new ARsCanAssignOnlyToOtherARsTaskRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

    }
}
