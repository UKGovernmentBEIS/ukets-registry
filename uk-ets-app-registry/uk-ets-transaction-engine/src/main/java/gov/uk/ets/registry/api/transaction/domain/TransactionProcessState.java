package gov.uk.ets.registry.api.transaction.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionProcessState {
    FINALISE("Transaction authorised"),
    TERMINATE("Transaction rejected"),
    CANCEL("Transaction canceled");

    /**
     * The description of state
     */
    private String description;
}
