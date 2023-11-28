package gov.uk.ets.registry.api.transaction.checks.allocation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.repository.AllocationJobRepository;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CheckPendingAllocationJobTest {

    @InjectMocks
    CheckPendingAllocationJob check;

    @Mock
    AllocationJobRepository allocationJobRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testPendingJobExists() {
        Mockito.when(allocationJobRepository.findByStatus(any())).thenReturn(List.of(new AllocationJob()));
        BusinessCheckContext context = new BusinessCheckContext();
        check.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testNoPendingJob() {
        Mockito.when(allocationJobRepository.findByStatus(any())).thenReturn(Collections.emptyList());
        BusinessCheckContext context = new BusinessCheckContext();
        check.execute(context);
        assertFalse(context.hasError());
    }


}