package gov.uk.ets.registry.api.authz.ruleengine.features.allocation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.allocation.AllocationSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SufficientAllocationUnitsRuleTest {

    private BusinessSecurityStore securityStore;
    private AllocationSecurityStoreSlice allocationSecurityStoreSlice;

    @BeforeEach
    public void setUp() {
        allocationSecurityStoreSlice = new AllocationSecurityStoreSlice();
        securityStore = new BusinessSecurityStore();
    }

    @Test
    void error() {
        ErrorBody errorBody = new SufficientAllocationUnitsRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void shouldPermitWhenEnoughUnitsExists() {
        // given
        allocationSecurityStoreSlice.setEnoughUnitsOnAllocationAccounts(true);
        securityStore.setAllocationSecurityStoreSlice(allocationSecurityStoreSlice);
        SufficientAllocationUnitsRule rule = new SufficientAllocationUnitsRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertEquals(BusinessRule.Result.PERMITTED, result.getResult());
    }

    @Test
    void shouldForbidWhenNotEnoughUnitsExists() {
        // given
        allocationSecurityStoreSlice.setEnoughUnitsOnAllocationAccounts(false);
        securityStore.setAllocationSecurityStoreSlice(allocationSecurityStoreSlice);
        SufficientAllocationUnitsRule rule = new SufficientAllocationUnitsRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertEquals(BusinessRule.Result.FORBIDDEN, result.getResult());
    }

    @Test
    void shouldIgnoreWhenSliceIsNull() {
        // given
        SufficientAllocationUnitsRule rule = new SufficientAllocationUnitsRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, result.getResult());
    }
}
