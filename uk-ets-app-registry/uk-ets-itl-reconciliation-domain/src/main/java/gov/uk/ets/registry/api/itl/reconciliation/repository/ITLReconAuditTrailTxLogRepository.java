package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconAuditTrailTxLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITLReconAuditTrailTxLogRepository extends JpaRepository<ITLReconAuditTrailTxLog, Long> {

}
