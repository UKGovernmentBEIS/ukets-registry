package gov.uk.ets.registry.api.allocation.service;

import com.fasterxml.jackson.databind.JsonNode;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.service.dto.AggregatedAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.AnnualAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.TotalAllocationDTO;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.Mapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Factory for creating data transfer objects.
 */
@Component
@RequiredArgsConstructor
public class AllocationDTOFactory {

    private final Mapper mapper;

    public AggregatedAllocationDTO createAggregatedAllocationDTO(List<AllocationSummary> allocationSummaries,
                                                                 List<String> transactionAttributes, boolean isNer) {
        if(allocationSummaries == null) {
            return null;
        }

        List<AnnualAllocationDTO> annualAllocationDTOs = allocationSummaries.stream()
            .map(allocationSummary ->
                AnnualAllocationDTO.builder()
                    .year(allocationSummary.getYear())
                    .allocated(allocationSummary.getAllocated())
                    .entitlement(allocationSummary.getEntitlement())
                    .remaining(allocationSummary.getRemaining())
                    .status(allocationSummary.getStatus().name())
                    .eligibleForReturn(allocationSummary.getRemaining() < 0 &&
                                       isEligibleForReturn(allocationSummary.getYear(), transactionAttributes, isNer))
                    .excluded(allocationSummary.getExcluded())
                    .build())
            .collect(Collectors.toList());
        return AggregatedAllocationDTO.builder()
            .annuals(annualAllocationDTOs)
            .totals(annualAllocationDTOs.stream()
                .reduce(this::sum).map(annualAllocationDTO ->
                TotalAllocationDTO.builder()
                    .remaining(annualAllocationDTO.getRemaining())
                    .entitlement(annualAllocationDTO.getEntitlement())
                    .allocated(annualAllocationDTO.getAllocated())
                    .build()
            ).orElse(TotalAllocationDTO.builder().build()))
            .build();
    }

    private boolean isEligibleForReturn(Integer year, List<String> transactionAttributes, boolean isNer) {
        return transactionAttributes.stream()
                                    .map(ta ->
                                             AllocationTransactionAttributesDTO
                                                 .builder()
                                                 .allocationType(mapValuefromAttributes(ta, "AllocationType"))
                                                 .allocationYear(Integer.valueOf(mapValuefromAttributes(ta, "AllocationYear")))
                                                 .build())
                                    .noneMatch(a -> year.equals(a.getAllocationYear()) &&
                                                    isNer == attributeIsNer(a.getAllocationType()));
    }

    private boolean attributeIsNer(String allocationType) {
        return AllocationType.NER.equals(AllocationType.parse(allocationType));
    }

    private String mapValuefromAttributes(String transactionAttributes, String property) {
        JsonNode node = mapper.convertToJsonNode(transactionAttributes);
        return node.get(property).asText();
    }


    private AnnualAllocationDTO sum(AnnualAllocationDTO dto1, AnnualAllocationDTO dto2) {
        return AnnualAllocationDTO.builder()
            .remaining(calculateSum(dto1.getRemaining(), dto2.getRemaining()))
            .entitlement(calculateSum(dto1.getEntitlement(), dto2.getEntitlement()))
            .allocated(calculateSum(dto1.getAllocated(), dto2.getAllocated()))
            .build();
    }

    private Long calculateSum(Long l1, Long l2) {
        if (l1 == null && l2 == null) {
            return null;
        }
        return (l1 == null ? 0 : l1) + (l2 == null ? 0 : l2);
    }
}
