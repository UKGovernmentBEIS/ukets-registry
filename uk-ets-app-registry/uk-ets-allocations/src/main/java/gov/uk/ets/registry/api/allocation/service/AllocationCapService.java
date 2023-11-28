package gov.uk.ets.registry.api.allocation.service;

/**
 * Allocation service for caps.
 */
public interface AllocationCapService {

    /**
     * Calculates the remaining cap.
     * @param year The year.
     * @return the remaining cap.
     */
    Long getRemainingCap(Integer year);

    /**
     * Reserves the cap for the provided year.
     * @param value The value.
     * @param year The year.
     */
    void reserveCap(Long value, Integer year);

    /**
     * Consumes the value for the provided year.
     * @param value The value.
     * @param year The year.
     */
    void consumeCap(Long value, Integer year);

    /**
     * Releases the value for the provided year.
     * @param value The value.
     * @param year The year.
     */
    void releaseCap(Long value, Integer year);
}
