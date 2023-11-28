package gov.uk.ets.registry.api.itl.reconciliation.repository;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLRecoSequenceResponseLog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ITLRecoSequenceResponseLogRepository extends JpaRepository<ITLRecoSequenceResponseLog, Long> {

}
