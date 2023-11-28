package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.util.Constants;

/**
 * Encapsulates the result of unit reservation.
 */
public class UnitReservationResult {

    /**
     * Whether the reservation was successful.
     */
    private boolean success = true;

    /**
     * The number of unit blocks reserved.
     */
    private Integer counter = 0;

    /**
     * Increases the counter.
     */
    public void increaseCounter() {
        counter++;
    }

    /**
     * Whether the technical limit was exceeded.
     *
     * @return false/true
     */
    public boolean technicalLimitExceeded() {
        return counter > Constants.ITL_MAXIMUM_NUMBER_OF_BLOCKS;
    }

    /**
     * Returns whether the reservation was successful.
     *
     * @return false/true
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets whether the reservation was successful.
     *
     * @param success whether the reservation was successful.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
