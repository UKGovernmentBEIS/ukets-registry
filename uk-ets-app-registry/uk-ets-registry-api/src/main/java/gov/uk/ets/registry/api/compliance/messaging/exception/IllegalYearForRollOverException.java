package gov.uk.ets.registry.api.compliance.messaging.exception;

import lombok.Getter;

/**
 * Indicates that the compliant entity cannot be excluded via roll over for the
 * provided year.
 * 
 * @author P35036
 *
 */
@Getter
public class IllegalYearForRollOverException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private long compliantEntityIdentifier;
    private long lastYear;
    private long emissions;

    public IllegalYearForRollOverException(long compliantEntityIdentifier,
            long lastYear, long emissions) {
        this.compliantEntityIdentifier = compliantEntityIdentifier;
        this.lastYear = lastYear;
        this.emissions = emissions;
    }

}
