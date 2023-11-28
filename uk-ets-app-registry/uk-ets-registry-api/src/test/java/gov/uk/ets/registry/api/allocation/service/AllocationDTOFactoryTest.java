package gov.uk.ets.registry.api.allocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.service.TestUtils.MockAllocationSummaryCommand;
import gov.uk.ets.registry.api.allocation.service.dto.AggregatedAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.TotalAllocationDTO;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.common.Mapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class AllocationDTOFactoryTest {

    private AllocationDTOFactory dtoFactory;

    @Mock
    private Mapper mapper;

    @BeforeEach
    public void setup() {
        dtoFactory = new AllocationDTOFactory(mapper);
    }

    @Test
    void createAccountAllocationDTO() {
        List<AllocationSummary> allocationSummaries = TestUtils.mockAllocationSummaryList(List.of(
            MockAllocationSummaryCommand.builder()
                .year(2021)
                .entitlement(10L)
                .allocated(null)
                .status(AllocationStatusType.ALLOWED)
                .build(),
            MockAllocationSummaryCommand.builder()
                .year(2022)
                .entitlement(10L)
                .allocated(6L)
                .status(AllocationStatusType.ALLOWED)
                .build(),
            MockAllocationSummaryCommand.builder()
                .year(2023)
                .entitlement(10L)
                .allocated(6L)
                .status(AllocationStatusType.ALLOWED)
                .build()
        ));

        TotalAllocationDTO expectedTotals = TotalAllocationDTO.builder()
            .entitlement(30L)
            .allocated(12L)
            .remaining(18L)
            .build();

        AggregatedAllocationDTO aggregatedAllocationDTO = dtoFactory.createAggregatedAllocationDTO(allocationSummaries,
                                                                                                   new ArrayList<>(),
                                                                                                   false);
        assertNotNull(aggregatedAllocationDTO);
        assertNotNull(aggregatedAllocationDTO.getAnnuals());
        assertTrue(aggregatedAllocationDTO.getAnnuals().size() > 0);
        TotalAllocationDTO totalAllocationDTO = aggregatedAllocationDTO.getTotals();
        assertNotNull(totalAllocationDTO);
        assertEquals(expectedTotals.getAllocated(), totalAllocationDTO.getAllocated());
        assertEquals(expectedTotals.getRemaining(), totalAllocationDTO.getRemaining());
        assertEquals(expectedTotals.getEntitlement(), totalAllocationDTO.getEntitlement());
    }
}
