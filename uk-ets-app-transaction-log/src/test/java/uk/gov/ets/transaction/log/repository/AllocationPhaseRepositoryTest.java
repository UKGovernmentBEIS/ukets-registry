package uk.gov.ets.transaction.log.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.ets.transaction.log.domain.AllocationPhase;
import uk.gov.ets.transaction.log.helper.BaseJpaTest;

class AllocationPhaseRepositoryTest extends BaseJpaTest {

    @Autowired
    AllocationPhaseRepository allocationPhaseRepository;

    @BeforeEach
    public void setUp() {
        AllocationPhase phase = new AllocationPhase();
        phase.setCode(1);
        phase.setInitialPhaseCap(1_000_000L);
        phase.setConsumedPhaseCap(500_000L);
        phase.setFirstYear(2021);
        phase.setLastYear(2030);
        allocationPhaseRepository.save(phase);
    }

    @Test
    @DisplayName("Makes sure the current allocation phase is retrieved correctly")
    void testGetCurrentAllocationPhase() {
        assertEquals(Optional.empty(), allocationPhaseRepository.getAllocationPhaseBasedOnYear(2020));
        assertEquals(Optional.empty(), allocationPhaseRepository.getAllocationPhaseBasedOnYear(2031));

        assertTrue(allocationPhaseRepository.getAllocationPhaseBasedOnYear(2021).isPresent());
        assertTrue(allocationPhaseRepository.getAllocationPhaseBasedOnYear(2025).isPresent());
        assertTrue(allocationPhaseRepository.getAllocationPhaseBasedOnYear(2030).isPresent());
    }

}