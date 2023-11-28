package gov.uk.ets.registry.api.task.shared;

import lombok.Getter;

public enum TaskSearchAliases {

    TRANSACTION_IDENTIFIERS("transactionIdentifiers"),
    RECIPIENT_ACCOUNT_NUMBERS("recipientAccountNumbers");

    @Getter
    private final String alias;

    TaskSearchAliases(String name) {
        this.alias = name;
    }
}
