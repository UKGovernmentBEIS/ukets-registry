package gov.uk.ets.registry.api.compliance.messaging.outbox;

import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"
})
class ComplianceOutboxRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    ComplianceOutboxRepository complianceOutboxRepository;
    
    private UUID eventId = UUID.randomUUID();
    
    private Long compliantEntityId = 100001L;
    
    @BeforeEach
    void setUp() {

        ComplianceOutbox complianceOutbox1 = new ComplianceOutbox();
        complianceOutbox1.setCompliantEntityId(compliantEntityId);
        complianceOutbox1.setEventId(eventId);
        complianceOutbox1.setGeneratedOn(LocalDateTime.now());
        complianceOutbox1.setStatus(ComplianceOutboxStatus.SENT);
        complianceOutbox1.setType(ComplianceEventType.COMPLIANT_ENTITY_INITIALIZATION);
        complianceOutbox1.setPayload("JSON payload");
        entityManager.persist(complianceOutbox1);
    }
    
    @Test
    void findByEventId() {
        Optional<ComplianceOutbox> result = complianceOutboxRepository.findByEventId(eventId);
        assertTrue(result.isPresent());
    }
}
