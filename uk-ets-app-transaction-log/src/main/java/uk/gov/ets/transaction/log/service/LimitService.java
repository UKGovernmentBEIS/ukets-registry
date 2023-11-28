package uk.gov.ets.transaction.log.service;

import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ets.transaction.log.domain.AllocationPhase;
import uk.gov.ets.transaction.log.repository.AllocationPhaseRepository;

/**
 * Service for limits (e.g. Issuance limit).
 */
@Service
@RequiredArgsConstructor
public class LimitService {

    /**
     * Repository for allocation phases.
     */
    private final AllocationPhaseRepository allocationPhaseRepository;

    /**
     * The current allocation year.
     */
    @Value("${business.property.transaction.allocation-year}")
    private Integer allocationYear;

    /**
     * Calculates the current issuance hard limit.
     * @return a number.
     */
    public Long getIssuanceLimit() {
        Long result = 0L;
        Optional<AllocationPhase> optional = allocationPhaseRepository.getAllocationPhaseBasedOnYear(getCurrentAllocationYear());
        if (optional.isPresent()) {
            AllocationPhase phase = optional.get();
            result = phase.getInitialPhaseCap() - phase.getConsumedPhaseCap();
        }
        return result;
    }

    /**
     * Consumes the provided issuance limit.
     * @param quantity The quantity to consume.
     */
    @Transactional
    public void consumeLimit(Long quantity) {
        Optional<AllocationPhase> optional = allocationPhaseRepository.getAllocationPhaseBasedOnYear(getCurrentAllocationYear());
        if (optional.isPresent()) {
            AllocationPhase phase = optional.get();
            phase.setConsumedPhaseCap(ObjectUtils.firstNonNull(phase.getConsumedPhaseCap(), 0L) +
                ObjectUtils.firstNonNull(quantity, 0L));
            allocationPhaseRepository.save(phase);
        }
    }

    /**
     * Calculates the current allocation year.
     * @return a year.
     */
    private Integer getCurrentAllocationYear() {
        return ObjectUtils.firstNonNull(allocationYear, LocalDate.now().getYear());
    }

}
