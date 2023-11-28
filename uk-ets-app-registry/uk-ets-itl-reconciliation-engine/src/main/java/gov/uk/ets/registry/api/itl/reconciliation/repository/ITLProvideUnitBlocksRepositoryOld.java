package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotBlock;
import gov.uk.ets.registry.api.itl.reconciliation.service.UnitBlockDto;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ITLProvideUnitBlocksRepositoryOld
    extends JpaRepository<ITLSnapshotBlock, Long>, QuerydslPredicateExecutor<ITLSnapshotBlock> {

    @Query(
        "select new gov.uk.ets.registry.api.itl.reconciliation.service.UnitBlockDto(" +
            "startBlock, " +
            "endBlock, " +
            "originatingCountryCode," +
            "type, " +
            "accountType, " +
            "accountIdentifier, " +
            "applicablePeriod " +
            ") " +
            "from ITLSnapshotBlock " +
            "where snapshotLog.reconciliationLog.reconId = ?1"
    )
    List<UnitBlockDto> generateUnitBlocks(String reconciliationId);

    @Query(
        "select new gov.uk.ets.registry.api.itl.reconciliation.service.UnitBlockDto(" +
            "startBlock, " +
            "endBlock, " +
            "originatingCountryCode," +
            "type, " +
            "accountType, " +
            "accountIdentifier, " +
            "applicablePeriod " +
            ") " +
            "from ITLSnapshotBlock " +
            "where snapshotLog.reconciliationLog.reconId = ?1 " +
            "and accountType = ?2 " +
            "and type = ?3 " +
            "and accountPeriod = ?4"
    )
    List<UnitBlockDto> generateUnitBlocks(String reconciliationId, KyotoAccountType accountType, UnitType unitType,
                                          CommitmentPeriod accountPeriod);
}
