package gov.uk.ets.registry.api.itl.reconciliation.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import java.time.LocalDateTime;
import java.util.List;
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
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"})
public class InconsistentTransactionBlockRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private InconsistentTransactionBlockRepository auditTrailTransactionBlockRepository;

    @BeforeEach
    public void setUp() {
    }
    
    @Test
    void queryExecutes() {

        List<TransactionBlock> inconsistentTransactionBlocks = auditTrailTransactionBlockRepository.
                findInconsistentTransactionBlocks(
                LocalDateTime.of(2020, 12, 8, 21, 42, 25), 
                LocalDateTime.of(2020, 12, 9, 21, 42, 25), 
                "GR", 
                129000L, 
                134567L);
        assertNotNull(inconsistentTransactionBlocks);
    }
}
