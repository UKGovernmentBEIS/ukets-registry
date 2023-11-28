package gov.uk.ets.registry.api.task.web.model;

import lombok.Data;

@Data
public class TaskTransactionDTO {

    private String transactionIdentifier;
    private String acquiringAccountFullIdentifier;
}
