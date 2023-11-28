package gov.uk.ets.registry.api.transaction;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.processor.TransactionProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.stereotype.Service;

/**
 * Provides the appropriate transaction processors.
 */
@Service
public class TransactionFactory {

    /**
     * The bean factory.
     */
    @Autowired
    private BeanFactory context;

    /**
     * Returns the appropriate processor for the provided transaction type.
     * @param type The transaction type.
     * @return a transaction processor
     */
    public TransactionProcessor getTransactionProcessor(TransactionType type) {
        return BeanFactoryAnnotationUtils.qualifiedBeanOfType(context, TransactionProcessor.class, type.toString());
    }

}
