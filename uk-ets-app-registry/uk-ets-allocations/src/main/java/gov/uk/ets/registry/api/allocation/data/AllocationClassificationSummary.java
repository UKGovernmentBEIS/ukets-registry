package gov.uk.ets.registry.api.allocation.data;

import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.allocation.type.AllocationType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import gov.uk.ets.registry.api.allocation.util.AllocationUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AllocationClassificationSummary {

    private Long compliantEntityId;
    private long remainingQuantity;
    private Long entitlement;
    private Long allocated;
    private Boolean excluded;
    private String allocationClassification;
    private AllocationType allocationType;

    public AllocationClassificationSummary(Long compliantEntityId, String allocationClassification) {
        this.compliantEntityId = compliantEntityId;
        this.allocationClassification = allocationClassification;
    }

    public AllocationClassificationSummary(Long compliantEntityId, String allocationClassification,
                                           Long entitlement, Long allocated, long remainingQuantity) {
        this.compliantEntityId = compliantEntityId;
        this.allocationClassification = allocationClassification;
        this.entitlement = entitlement;
        this.allocated = allocated;
        this.remainingQuantity = remainingQuantity;
    }

    public AllocationClassificationSummary(Long compliantEntityId, Long entitlement, Long allocated, AllocationType allocationType,Boolean excluded) {
        this.compliantEntityId = compliantEntityId;
        this.remainingQuantity = AllocationUtils.calculateRemainingValue(entitlement, allocated, excluded);
        this.allocationType = allocationType;
        this.entitlement = entitlement;
        this.allocated = allocated;
        this.excluded = excluded;
    }

    public AllocationClassificationSummary(Long compliantEntityId, long remainingQuantity, String allocationClassification) {
        this.compliantEntityId = compliantEntityId;
        this.remainingQuantity = remainingQuantity;
        this.allocationClassification = allocationClassification;
    }

    /**
     * Utility method that returns the allocation classification based on some rules.
     */
    public AllocationClassificationSummary transform(List<AllocationClassificationSummary> classificationSummaries) {
        long remaining = classificationSummaries.stream().mapToLong(AllocationClassificationSummary::getRemainingQuantity).sum();
        long entitlementQuantity = classificationSummaries.stream()
            .filter( t -> !Optional.ofNullable(t.getExcluded()).orElse(false))
            .mapToLong(AllocationClassificationSummary::getEntitlement)
            .sum();
        long allocatedQuantity = classificationSummaries.stream().mapToLong(AllocationClassificationSummary::getAllocated).sum();
        String resultClassification = null;
        if (entitlementQuantity !=0 && entitlementQuantity == allocatedQuantity && remaining == 0) {
            resultClassification = AllocationClassification.FULLY_ALLOCATED.name();
        } else if (remaining < 0) {
            resultClassification = AllocationClassification.OVER_ALLOCATED.name();
        } else if (remaining > 0 && allocatedQuantity > 0) {
            resultClassification = AllocationClassification.UNDER_ALLOCATED.name();
        } else if (entitlementQuantity != 0 && allocatedQuantity == 0){
            resultClassification = AllocationClassification.NOT_YET_ALLOCATED.name();
        }
        return new AllocationClassificationSummary(this.compliantEntityId, resultClassification, entitlementQuantity,
                allocatedQuantity, remaining);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AllocationClassificationSummary)) {
            return false;
        }
        AllocationClassificationSummary that = (AllocationClassificationSummary) o;
        return Objects.equals(compliantEntityId, that.compliantEntityId) &&
               (Objects.equals(allocationType, that.allocationType) ||
                Objects.equals(remainingQuantity, that.remainingQuantity));
    }

    @Override
    public int hashCode() {
        return Objects.hash(compliantEntityId);
    }
}
