package gov.uk.ets.registry.api.reconciliation.repository;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.reconciliation.transfer.QReconciliationEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationEntrySummary;
import gov.uk.ets.registry.api.transaction.domain.QUnitBlock;
import java.util.List;
import java.util.Set;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Repository for retrieving the {@link ReconciliationEntrySummary} aggregated objects.
 */
@Repository
@AllArgsConstructor
public class ReconciliationEntrySummaryRepository {

    private final EntityManager entityManager;

    /**
     * Computes the total quantity per account and unit type and returns the aggregated results.
     * @param accountIdentifiers The account identifiers to fetch the aggregated results.
     * @return the {@link ReconciliationEntrySummary} aggregated objects.
     */
    public List<ReconciliationEntrySummary> fetch(Set<Long> accountIdentifiers) {
        QUnitBlock unitBlock = QUnitBlock.unitBlock;

        NumberExpression<Long> sum = unitBlock.endBlock
            .subtract(unitBlock.startBlock)
            .add(1).sum().as("total");

        QReconciliationEntrySummary projection = new QReconciliationEntrySummary(
            unitBlock.accountIdentifier,
            unitBlock.type, sum
        );

        return new JPAQuery<QReconciliationEntrySummary>(entityManager)
            .select(projection)
            .from(unitBlock)
            .where(unitBlock.accountIdentifier.in(accountIdentifiers))
            .groupBy(unitBlock.accountIdentifier)
            .groupBy(unitBlock.type)
            .orderBy(unitBlock.accountIdentifier.asc())
            .orderBy(unitBlock.type.asc())
            .fetch();
    }
}
