package gov.uk.ets.registry.api.itl.reconciliation.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.itl.reconciliation.domain.QITLSnapshotBlock;
import gov.uk.ets.registry.api.itl.reconciliation.service.QUnitBlockDto;
import gov.uk.ets.registry.api.itl.reconciliation.service.UnitBlockDto;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ITLProvideUnitBlocksQueryDslRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public List<UnitBlockDto> generateUnitBlocks(String reconciliationId) {
        return generateUnitBlocks(reconciliationId, null, null, null);
    }

    /**
     * Calculates unit blocks for reconciliation.
     */
    public List<UnitBlockDto> generateUnitBlocks(String reconciliationId, KyotoAccountType accountType,
                                                 UnitType unitType,
                                                 CommitmentPeriod accountPeriod) {

        QITLSnapshotBlock snapshotBlock = QITLSnapshotBlock.iTLSnapshotBlock;

        BooleanBuilder predicate =
            createWhereClause(snapshotBlock, reconciliationId, accountType, unitType, accountPeriod);

        return new JPAQuery<UnitBlockDto>(entityManager)
            .select(
                new QUnitBlockDto(
                    snapshotBlock.startBlock,
                    snapshotBlock.endBlock,
                    snapshotBlock.originatingCountryCode,
                    snapshotBlock.type,
                    snapshotBlock.accountType,
                    snapshotBlock.accountIdentifier,
                    snapshotBlock.applicablePeriod
                )
            )
            .from(snapshotBlock)
            .where(predicate)
            .fetch();
    }

    private BooleanBuilder createWhereClause(QITLSnapshotBlock snapshotBlock, String reconciliationId,
                                             KyotoAccountType accountType,
                                             UnitType unitType, CommitmentPeriod accountPeriod) {
        BooleanBuilder predicate =
            new BooleanBuilder(snapshotBlock.snapshotLog.reconciliationLog.reconId.eq(reconciliationId));
        if (accountType != null) {
            predicate.and(snapshotBlock.accountType.eq(accountType));
        }
        if (unitType != null) {
            predicate.and(snapshotBlock.type.eq(unitType));
        }
        if (accountPeriod != null) {
            predicate.and(snapshotBlock.accountPeriod.eq(accountPeriod));
        }
        return predicate;
    }
}
