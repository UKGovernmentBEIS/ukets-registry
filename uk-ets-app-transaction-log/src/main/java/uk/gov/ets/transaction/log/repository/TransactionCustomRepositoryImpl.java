package uk.gov.ets.transaction.log.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import uk.gov.ets.transaction.log.domain.QTransaction;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;

public class TransactionCustomRepositoryImpl implements TransactionCustomRepository {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Transaction> findByStartedAfterAndStatusEquals(Date date, TransactionStatus status) {
        QTransaction transaction = QTransaction.transaction;
        BooleanExpression condition = transaction.status.eq(status);
        if (date != null) {
            condition = condition.and(transaction.started.after(date));
        }
        return new JPAQuery<Transaction>(entityManager).from(transaction).where(condition).fetch();
    }
}
