package gov.uk.ets.compliance.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceOutboxRepository extends JpaRepository<ComplianceOutbox, Long> {

    List<ComplianceOutbox> findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus status);
}
