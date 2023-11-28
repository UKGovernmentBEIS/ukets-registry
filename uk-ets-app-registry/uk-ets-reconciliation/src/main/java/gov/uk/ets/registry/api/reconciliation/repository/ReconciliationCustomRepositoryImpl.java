package gov.uk.ets.registry.api.reconciliation.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.reconciliation.domain.QReconciliation;
import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Implementation of {@link ReconciliationCustomRepository}
 */
public class ReconciliationCustomRepositoryImpl implements ReconciliationCustomRepository {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public Reconciliation fetchLastCompletedReconciliation() {
        QReconciliation reconciliation = QReconciliation.reconciliation;
        QReconciliation innnerReconciliation = new QReconciliation("r1");
        return new JPAQuery<Reconciliation>(entityManager)
            .from(reconciliation)
            .where(reconciliation.created.eq(JPAExpressions
                .select(innnerReconciliation.created.max())
                    .from(innnerReconciliation)
                    .where(innnerReconciliation.status.eq(ReconciliationStatus.COMPLETED))))
            .fetchOne();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reconciliation fetchLatestReconciliation() {
        QReconciliation reconciliation = QReconciliation.reconciliation;
        QReconciliation innnerReconciliation = new QReconciliation("r1");
        return new JPAQuery<Reconciliation>(entityManager)
            .from(reconciliation)
            .where(reconciliation.created.eq(JPAExpressions
                .select(innnerReconciliation.created.max())
                .from(innnerReconciliation)))
            .fetchOne();
    }
}
