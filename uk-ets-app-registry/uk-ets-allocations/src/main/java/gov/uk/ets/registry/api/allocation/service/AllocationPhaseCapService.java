package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.domain.AllocationPhase;
import gov.uk.ets.registry.api.allocation.repository.AllocationPhaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for allocation phase caps.
 */
@Service
@AllArgsConstructor
public class AllocationPhaseCapService implements AllocationCapService {

    /**
     * Repository for allocation phases.
     */
    private AllocationPhaseRepository allocationPhaseRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getRemainingCap(Integer year) {
        AllocationPhase phase = allocationPhaseRepository.getAllocationPhaseOfYear(year);
        return phase.getInitialPhaseCap() - phase.getConsumedPhaseCap() - phase.getPendingPhaseCap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reserveCap(Long value, Integer year) {
        AllocationPhase phase = allocationPhaseRepository.getAllocationPhaseOfYear(year);
        phase.setPendingPhaseCap(phase.getPendingPhaseCap() + value);
        allocationPhaseRepository.save(phase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void consumeCap(Long value, Integer year) {
        AllocationPhase phase = allocationPhaseRepository.getAllocationPhaseOfYear(year);
        phase.setPendingPhaseCap(phase.getPendingPhaseCap() - value);
        phase.setConsumedPhaseCap(phase.getConsumedPhaseCap() + value);
        allocationPhaseRepository.save(phase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void releaseCap(Long value, Integer year) {
        AllocationPhase phase = allocationPhaseRepository.getAllocationPhaseOfYear(year);
        phase.setPendingPhaseCap(phase.getPendingPhaseCap() - value);
        allocationPhaseRepository.save(phase);
    }

}
