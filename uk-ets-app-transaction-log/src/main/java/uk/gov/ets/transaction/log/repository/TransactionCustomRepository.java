package uk.gov.ets.transaction.log.repository;

import java.util.Date;
import java.util.List;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;


public interface TransactionCustomRepository {
    /**
     * Retrieves transactions started after a specific date, being in a specific status.
     * If the date is null, it does not take part in the query.
     */
    List<Transaction> findByStartedAfterAndStatusEquals(Date date, TransactionStatus status);
}
