package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class TestUtils {

    public static List<AllocationSummary> mockAllocationSummaryList(List<MockAllocationSummaryCommand> commands) {
        List<AllocationSummary> allocationSummaries = new ArrayList<>();
        commands.stream().forEach(command -> {
            allocationSummaries.add(
                new AllocationSummary(command.year,
                    command.entitlement,
                    command.allocated,
                    command.status,
                    command.excluded));
        });
        return allocationSummaries;
    }

    @Builder
    @Getter
    public static class MockAllocationSummaryCommand {

        /**
         * The year.
         */
        private Integer year;

        /**
         * The planned quantity (entitlement).
         */
        private Long entitlement;

        /**
         * The allocated quantity.
         */
        private Long allocated;

        /**
         * The remaining quantity.
         */
        private Long remaining;

        /**
         * The allocation status.
         */
        AllocationStatusType status;

        private Boolean excluded;
    }

    public static AllocationJob createAllocationJob(AllocationJobStatus status) {
        var allocationJob = new AllocationJob();
        allocationJob.setStatus(status);
        allocationJob.setCreated(new Date());
        allocationJob.setRequestIdentifier(12345L);
        allocationJob.setYear(2021);
        return allocationJob;
    }
}
