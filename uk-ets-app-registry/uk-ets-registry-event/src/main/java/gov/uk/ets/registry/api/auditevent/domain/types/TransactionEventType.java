package gov.uk.ets.registry.api.auditevent.domain.types;

/**
 * The transaction event type enumeration.
 */
public enum TransactionEventType {

    TRANSACTION_COMPLETED("Transaction completed"),
    TRANSACTION_CANCELLED("Transaction cancelled"),
    TRANSACTION_FAILED("Transaction failed"),
    TRANSACTION_DELAYED("Transaction delayed"),
    TRANSACTION_MANUALLY_CANCELED("Transaction cancelled by user");

    private final String eventAction;

    TransactionEventType(String eventAction) {
        this.eventAction = eventAction;
    }

    public String getEventAction() {
        return eventAction;
    }
}
