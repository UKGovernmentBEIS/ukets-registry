package gov.uk.ets.registry.api.task.web.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultipleTransactionsTaskInfo {


    private Long accountIdentifier;
    private String comment;
    private List<TaskTransactionDTO> taskTransactions;
}
