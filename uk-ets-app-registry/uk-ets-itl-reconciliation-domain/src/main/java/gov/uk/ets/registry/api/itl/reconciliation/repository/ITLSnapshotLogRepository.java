package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLSnapshotLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITLSnapshotLogRepository extends JpaRepository<ITLSnapshotLog, Long> {

    Optional<ITLSnapshotLog> findByReconciliationLog_ReconId(String reconId);
}
