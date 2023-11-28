package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ARsForNewAccountMustBeActiveRuleTest {

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        securityStore = new BusinessSecurityStore();
    }

    @Test
    void error() {
        ErrorBody errorBody = new ARsForNewAccountMustBeActiveRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void testAllActiveARs() {
        // given
        User user = new User();
        user.setState(UserStatus.ENROLLED);
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setCandidateAccountARs(List.of(user));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        ARsForNewAccountMustBeActiveRule rule = new ARsForNewAccountMustBeActiveRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertEquals(BusinessRule.Result.PERMITTED, result.getResult());
    }

    @Test
    void testRejectedTask() {
        // given
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.REJECTED);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        ARsForNewAccountMustBeActiveRule rule = new ARsForNewAccountMustBeActiveRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, result.getResult());
    }

    @Test
    void testInactiveAR() {
        // given
        User activeUser = new User();
        activeUser.setState(UserStatus.ENROLLED);
        User inactiveUser = new User();
        inactiveUser.setState(UserStatus.SUSPENDED);
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setCandidateAccountARs(List.of(activeUser, inactiveUser));;
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        ARsForNewAccountMustBeActiveRule rule = new ARsForNewAccountMustBeActiveRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertTrue(result.isForbidden());
    }
}
