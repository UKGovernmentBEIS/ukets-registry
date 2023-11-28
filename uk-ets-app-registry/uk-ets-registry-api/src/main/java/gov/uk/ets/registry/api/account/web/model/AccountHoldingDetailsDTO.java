package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.account.domain.UnitBlockFilter;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class AccountHoldingDetailsDTO {
    public static AccountHoldingDetailsDTO create(UnitBlockFilter filter, List<UnitBlock> unitBlocks) {
        return AccountHoldingDetailsDTO.builder()
            .applicablePeriod(filter.getApplicablePeriod().name())
            .originalPeriod(filter.getOriginalPeriod().name())
            .unit(filter.getUnitType().name())
            .results(unitBlocks.stream().map(unitBlock ->
                UnitBlockDTO.builder()
                    .project(unitBlock.getProjectNumber())
                    .activity(unitBlock.getEnvironmentalActivity() != null ? unitBlock.getEnvironmentalActivity().name() : null)
                    .reserved(unitBlock.getReservedForTransaction() != null)
                    .serialNumberStart(unitBlock.getStartBlock())
                    .serialNumberEnd(unitBlock.getEndBlock())
                    .quantity(
                        unitBlock.getStartBlock() != null && unitBlock.getEndBlock() != null ?
                            unitBlock.getEndBlock() - unitBlock.getStartBlock() + 1
                            : null)
                    .build()
            ).collect(Collectors.toList()))
            .build();
    }

    private String unit;
    private String originalPeriod;
    private String applicablePeriod;
    @Setter
    private List<UnitBlockDTO> results;

    @Getter
    @Setter
    @Builder
    public static class UnitBlockDTO {
        private String project;
        private String activity;
        private Long serialNumberStart;
        private Long serialNumberEnd;
        private Long quantity;
        private boolean reserved;
    }
}
