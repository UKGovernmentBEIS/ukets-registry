package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ITLReconciliationLogRepository
    extends JpaRepository<ITLReconciliationLog, String> {

	
	@Query("select r from ITLReconciliationLog r where r.reconSnapshotDatetime = (select max(i.reconSnapshotDatetime) from ITLReconciliationLog i)")
	Optional<ITLReconciliationLog> findByLatestSnapshotDatetime();
}
