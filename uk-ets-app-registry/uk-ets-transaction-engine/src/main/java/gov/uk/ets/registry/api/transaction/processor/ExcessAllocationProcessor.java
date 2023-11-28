package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ExcessAllocation")
public class ExcessAllocationProcessor extends ParentAllocationProcessor {

    public static final String RELATED_NAT_TRANSACTION_IDENTIFER = "RelatedNATTransactionIdentifer";
    public static final String IS_TRIGGERED_BY_NER_FINALIZATION = "TriggeredByFinalisation";
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Transaction createInitialTransaction(TransactionSummary transactionSummary) {
        Map<String, Serializable> map = Optional.ofNullable(transactionSummary.getAdditionalAttributes()).orElse(new HashMap<>());
        map.put(AllocationYear.class.getSimpleName(), transactionSummary.getAllocationYear());
        map.put(AllocationType.class.getSimpleName(), transactionSummary.getAllocationType());
        transactionSummary.setAdditionalAttributes(map);
        return super.createInitialTransaction(transactionSummary);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        super.finalise(transaction);
        calculateAllocations(transaction, transaction.getTransferringAccount().getAccountIdentifier());
    }

}
