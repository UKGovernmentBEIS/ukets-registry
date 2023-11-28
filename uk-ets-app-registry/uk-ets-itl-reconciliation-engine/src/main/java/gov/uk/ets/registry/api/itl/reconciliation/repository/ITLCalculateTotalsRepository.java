package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotBlock;
import gov.uk.ets.registry.api.itl.reconciliation.service.TotalDto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface ITLCalculateTotalsRepository extends JpaRepository<ITLSnapshotBlock, Long> {
    
    @Query("select new gov.uk.ets.registry.api.itl.reconciliation.service.TotalDto(" +
            "accountType, " +
            "accountPeriod, " +
            "type, " +
            "sum(endBlock - startBlock + 1)) " +
            "from ITLSnapshotBlock " +
            "where snapshotLog.reconciliationLog.reconId = ?1 " +
            "group by accountType, accountPeriod, type"
        )
        List<TotalDto> calculateTotals(String reconciliationId);
}
