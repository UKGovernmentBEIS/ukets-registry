package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationStatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ITLReconciliationStatusHistoryRepository extends JpaRepository<ITLReconciliationStatusHistory, Long>{

    @Query("select h from ITLReconciliationStatusHistory h where h.reconciliationLog.reconId = ?1")
    List<ITLReconciliationStatusHistory> findByReconciliationIdentifier(String reconId);
}
