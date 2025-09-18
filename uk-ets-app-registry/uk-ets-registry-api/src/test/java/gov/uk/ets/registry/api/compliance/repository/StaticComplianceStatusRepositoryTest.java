package gov.uk.ets.registry.api.compliance.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import java.util.List;
import java.util.Optional;
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
public class StaticComplianceStatusRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    StaticComplianceStatusRepository staticComplianceStatusRepository;

    private Long compliantEntityId = 100001L;

    @BeforeEach
    void setUp() {
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        entityManager.persist(installation);

        StaticComplianceStatus complianceStatus1 = new StaticComplianceStatus();
        complianceStatus1.setCompliantEntity(installation);
        complianceStatus1.setYear(2021L);
        complianceStatus1.setComplianceStatus(ComplianceStatus.A);
        entityManager.persist(complianceStatus1);

        StaticComplianceStatus complianceStatus2 = new StaticComplianceStatus();
        complianceStatus2.setCompliantEntity(installation);
        complianceStatus2.setYear(2020L);
        complianceStatus2.setComplianceStatus(ComplianceStatus.B);
        entityManager.persist(complianceStatus2);

        StaticComplianceStatus complianceStatus3 = new StaticComplianceStatus();
        complianceStatus3.setCompliantEntity(installation);
        complianceStatus3.setYear(2022L);
        complianceStatus3.setComplianceStatus(ComplianceStatus.A);
        entityManager.persist(complianceStatus3);
    }

    @Test
    void findByCompliantEntityIdentifierAndYear() {
        Optional<StaticComplianceStatus> result = staticComplianceStatusRepository.
            findByCompliantEntityIdentifierAndYear(compliantEntityId, 2020L);
        assertTrue(result.isPresent());
    }

    @Test
    void findByCompliantEntityIdentifierAndYearGreaterThanEqual() {
        List<StaticComplianceStatus> results = staticComplianceStatusRepository.
            findByCompliantEntityIdentifierAndYearGreaterThanEqual(compliantEntityId, 2021L);
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    void findByCompliantEntityIdentifierAndYearBetween() {
        List<StaticComplianceStatus> results = staticComplianceStatusRepository.
            findByCompliantEntityIdentifierAndYearBetween(compliantEntityId, 2020L, 2021L);
        assertNotNull(results);
        assertEquals(2, results.size());
    }
}
