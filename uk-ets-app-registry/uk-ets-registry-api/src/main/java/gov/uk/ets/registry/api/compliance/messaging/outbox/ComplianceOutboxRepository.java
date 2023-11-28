package gov.uk.ets.registry.api.compliance.messaging.outbox;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ComplianceOutboxRepository extends JpaRepository<ComplianceOutbox, Long> {

    List<ComplianceOutbox> findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus status);
    
    Optional<ComplianceOutbox> findByEventId(UUID eventId);
}
