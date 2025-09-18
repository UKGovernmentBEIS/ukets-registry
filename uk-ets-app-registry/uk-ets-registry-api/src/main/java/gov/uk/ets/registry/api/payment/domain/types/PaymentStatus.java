package gov.uk.ets.registry.api.payment.domain.types;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The valid payment statuses as defined by GOV.UK Pay service.
 */
public enum PaymentStatus {

    CREATED(false), 
    STARTED(false), 
    SUBMITTED(false), 
    CAPTURABLE(false), 
    SUCCESS(true), 
    FAILED(true), 
    CANCELLED(true), 
    ERROR(true);

    private boolean finished;

    PaymentStatus(boolean finished) {
        this.finished = finished;
    }
    
    public boolean isFinished() {
        return finished;
    }

    @JsonCreator
    public static PaymentStatus fromString(String value) {
        return value == null ? null : PaymentStatus.valueOf(value.toUpperCase());
    }
}
