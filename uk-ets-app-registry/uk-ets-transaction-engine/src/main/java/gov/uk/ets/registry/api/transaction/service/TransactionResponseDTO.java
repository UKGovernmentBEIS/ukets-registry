package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionResponseDTO {
    private Transaction transaction;
    private List<BusinessCheckError> errors;

}
