package gov.uk.ets.registry.api.task.web.model;

import gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The transaction details class being used for the case of
 * return of excess allocation towards both NAT and NER accounts.
 */
@Getter
@Setter
public class ExcessAllocationTransactionTaskDetailsDTO extends TransactionTaskDetailsDTO {

    public ExcessAllocationTransactionTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO, TransactionType transactionType, String reference) {
        super(taskDetailsDTO, transactionType, reference);
    }

    /**
     * The NAT acquiring account info
     */
    private AcquiringAccountInfo natAcquiringAccount;

    /**
     * The NAT account transaction blocks
     */
    private List<TransactionBlockSummary> natTransactionBlocks;

    /**
     * The NER acquiring account info
     */
    private AcquiringAccountInfo nerAcquiringAccount;

    /**
     * The NER account transaction blocks
     */
    private List<TransactionBlockSummary> nerTransactionBlocks;

    /**
     * The NAT quantity to be returned
     */
    private Long natQuantity;

    /**
     * The NER quantity to be returned
     */
    private Long nerQuantity;

    /**
     * The NAT transaction identifier
     */
    private String natTransactionIdentifier;

    /**
     * The NER transaction identifier
     */
    private String nerTransactionIdentifier;
}
