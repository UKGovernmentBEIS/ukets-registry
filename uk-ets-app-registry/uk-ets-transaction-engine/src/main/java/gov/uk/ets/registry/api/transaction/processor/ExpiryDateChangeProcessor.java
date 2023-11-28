package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;

@Service("ExpiryDateChange")
@Log4j2
public class ExpiryDateChangeProcessor extends InternalTransferProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Transaction createInitialTransaction(TransactionSummary transactionSummary) {
        Transaction transaction = super.createInitialTransaction(transactionSummary);
        transaction.setAttributes(transactionSummary.getAttributes());
        return transaction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propose(TransactionSummary transaction) {
        super.propose(transaction);
        Map<String, Serializable> attributes = getAdditionalAttributes(transaction.getAttributes());
        OffsetDateTime odt = OffsetDateTime.parse((String) attributes.get("targetDate"));
        unitMarkingService.changeExpireDate(
                transactionPersistenceService.getTransactionBlocks(transaction.getIdentifier()),
                new Date(odt.toInstant().toEpochMilli()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        Map<String, Serializable> attributes = getAdditionalAttributes(transaction.getAttributes());
        OffsetDateTime odt = OffsetDateTime.parse((String) attributes.get("targetDate"));
        unitMarkingService.changeExpireDate(
                transactionPersistenceService.getUnitBlocks(transaction.getIdentifier()),
                new Date(odt.toInstant().toEpochMilli()));
        super.finalise(transaction);
    }
}
