package gov.uk.ets.registry.api.transaction.checks.allocation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckAllocationValidTest {

    AllocationOverview overview;
    BusinessCheckContext context;

    @BeforeEach
    void setup() {
        overview = new AllocationOverview();
        context = new BusinessCheckContext();
        context.store(AllocationOverview.class.getName(), overview);
    }

    @Test
    void testZero() {
        overview.setTotalQuantity(0L);
        new CheckAllocationValid().execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testPositive() {
        overview.setTotalQuantity(500_000L);
        new CheckAllocationValid().execute(context);
        assertFalse(context.hasError());
    }

    @Test
    void testNegative() {
        overview.setTotalQuantity(-10L);
        new CheckAllocationValid().execute(context);
        assertTrue(context.hasError());
    }

}