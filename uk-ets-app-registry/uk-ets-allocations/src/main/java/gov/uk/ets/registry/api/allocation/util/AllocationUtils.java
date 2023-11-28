package gov.uk.ets.registry.api.allocation.util;

public class AllocationUtils {

    private AllocationUtils() {}

    public static Long calculateRemainingValue(Long entitlement, Long allocated, Boolean excluded) {
        if (allocated == null) {
            allocated = 0L;
        }
        return excluded != null && excluded ? -allocated : entitlement - allocated;
    }
}
