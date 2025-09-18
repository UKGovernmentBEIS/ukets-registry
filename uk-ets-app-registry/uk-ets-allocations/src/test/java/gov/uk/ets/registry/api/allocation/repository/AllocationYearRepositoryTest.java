package gov.uk.ets.registry.api.allocation.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.allocation.domain.AllocationPeriod;
import gov.uk.ets.registry.api.allocation.domain.AllocationPhase;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"
})
public class AllocationYearRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AllocationYearRepository allocationYearRepository;
    
    
    @BeforeEach
    public void setUp() throws Exception {
        AllocationPhase phase = new AllocationPhase();
        phase.setCode(1);

        AllocationPeriod period = new AllocationPeriod();
        period.setCode(1);
        period.setPhase(phase);
        
        AllocationYear y1 = new AllocationYear();
        y1.setYear(2021);
        y1.setInitialYearlyCap(100000L);
        y1.setEntitlementYearly(90000L);
        y1.setPeriod(period);
        
        AllocationYear y2 = new AllocationYear();
        y2.setYear(2022);
        y2.setInitialYearlyCap(5000L);
        y2.setEntitlementYearly(300L);
        y2.setPeriod(period);
        
        entityManager.persist(phase);
        entityManager.persist(period);
        entityManager.persist(y1);
        entityManager.persist(y2);
    }
    
    @Test
    public void findByPhaseCode() {
        List<AllocationYear> results = allocationYearRepository.findByPhaseCode(1);
        assertEquals(results.size(), 2);
    }
}
