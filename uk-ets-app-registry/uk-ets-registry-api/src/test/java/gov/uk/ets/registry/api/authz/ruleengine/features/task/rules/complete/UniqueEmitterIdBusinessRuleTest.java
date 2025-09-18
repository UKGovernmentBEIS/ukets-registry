package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import org.junit.jupiter.api.Test;

class UniqueEmitterIdBusinessRuleTest {

    @Test
    void error() {

        // given
        BusinessSecurityStore securityStore = new BusinessSecurityStore();
        UniqueEmitterIdBusinessRule uniqueEmitterIdBusinessRule = new UniqueEmitterIdBusinessRule(securityStore);

        // when
        ErrorBody errorBody = uniqueEmitterIdBusinessRule.error();

        // them
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void testRejectedTask() {

        // given
        BusinessSecurityStore securityStore = new BusinessSecurityStore();
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.REJECTED);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        UniqueEmitterIdBusinessRule uniqueEmitterIdBusinessRule = new UniqueEmitterIdBusinessRule(securityStore);

        // when
        BusinessRule.Outcome outcome = uniqueEmitterIdBusinessRule.permit();

        // then
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, outcome.getResult());
    }

    @Test
    void testNotExistingEmitterId() {

        // given
        BusinessSecurityStore securityStore = new BusinessSecurityStore();
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        UniqueEmitterIdBusinessRule uniqueEmitterIdBusinessRule = new UniqueEmitterIdBusinessRule(securityStore);

        // when
        BusinessRule.Outcome outcome = uniqueEmitterIdBusinessRule.permit();

        // then
        assertEquals(BusinessRule.Result.PERMITTED, outcome.getResult());
    }

    @Test
    void testExistingEmitterId() {

        // given
        BusinessSecurityStore securityStore = new BusinessSecurityStore();
        securityStore.setEmitterIdExists(true);
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        UniqueEmitterIdBusinessRule uniqueEmitterIdBusinessRule = new UniqueEmitterIdBusinessRule(securityStore);

        // when
        BusinessRule.Outcome outcome = uniqueEmitterIdBusinessRule.permit();

        // then
        assertEquals(BusinessRule.Result.FORBIDDEN, outcome.getResult());
    }
}
