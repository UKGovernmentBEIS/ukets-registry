package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view;

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
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdminsOrInitiatorCanViewAccountOpeningFileRuleTest {

    private BusinessSecurityStore securityStore;
    private TaskBusinessRuleInfo ruleInfo;

    @BeforeEach
    void setUp() {
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        ruleInfo = new TaskBusinessRuleInfo();
        slice.setTaskBusinessRuleInfoList(List.of(ruleInfo));

        securityStore = new BusinessSecurityStore();
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
    }

    @Test
    void error() {
        ErrorBody errorBody = new AdminsOrInitiatorCanViewAccountOpeningFileRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void testRuleForAdmin() {
        // given
        List<UserRole> userRoles = List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR);
        securityStore.setUserRoles(userRoles);

        // when
        BusinessRule.Outcome outcome = new AdminsOrInitiatorCanViewAccountOpeningFileRule(securityStore).permit();

        // then
        assertNotNull(outcome);
        assertEquals(BusinessRule.Result.PERMITTED, outcome.getResult());
    }

    @Test
    void testRuleForOtherTasks() {
        // given
        Task task = new Task();
        task.setType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        ruleInfo.setTask(task);
        securityStore.setUserRoles(Collections.emptyList());

        // when
        BusinessRule.Outcome outcome = new AdminsOrInitiatorCanViewAccountOpeningFileRule(securityStore).permit();

        // then
        assertNotNull(outcome);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, outcome.getResult());
    }

    @Test
    void testRuleForInitiator() {
        // given
        User user = new User();
        user.setUrid("userId");

        Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setInitiatedBy(user);

        securityStore.setUser(user);
        ruleInfo.setTask(task);
        securityStore.setUserRoles(Collections.emptyList());

        // when
        BusinessRule.Outcome outcome = new AdminsOrInitiatorCanViewAccountOpeningFileRule(securityStore).permit();

        // then
        assertNotNull(outcome);
        assertEquals(BusinessRule.Result.PERMITTED, outcome.getResult());
    }

    @Test
    void testRuleForOtherUsers() {
        // given
        User user = new User();
        user.setUrid("userId");

        Task task = new Task();
        task.setType(RequestType.ACCOUNT_OPENING_REQUEST);
        task.setInitiatedBy(user);

        User initiator = new User();
        initiator.setUrid("initiator");

        securityStore.setUser(initiator);
        ruleInfo.setTask(task);
        securityStore.setUserRoles(Collections.emptyList());

        // when
        BusinessRule.Outcome outcome = new AdminsOrInitiatorCanViewAccountOpeningFileRule(securityStore).permit();

        // then
        assertNotNull(outcome);
        assertEquals(BusinessRule.Result.FORBIDDEN, outcome.getResult());
    }
}
