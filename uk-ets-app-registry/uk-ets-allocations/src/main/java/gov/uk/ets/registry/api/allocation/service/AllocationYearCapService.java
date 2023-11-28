package gov.uk.ets.registry.api.allocation.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllowanceReport;
import gov.uk.ets.registry.api.allocation.domain.AllocationPhase;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.repository.AllocationPhaseRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.transaction.domain.data.IssuanceBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.AllArgsConstructor;

/**
 * Service for allocation year caps.
 */
@Service
@AllArgsConstructor
public class AllocationYearCapService implements AllocationCapService {

    /**
     * Repository for allocation years.
     */
    private AllocationYearRepository allocationYearRepository;

    /**
     * Repository for allocation phase.
     */
    private AllocationPhaseRepository allocationPhaseRepository;
    
    /**
     * Configuration service.
     */
    private AllocationConfigurationService allocationConfigurationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getRemainingCap(Integer year) {
        AllocationYear allocationYear = allocationYearRepository.findByYear(year);
        return allocationYear.getInitialYearlyCap() - allocationYear.getConsumedYearlyCap() - allocationYear.getPendingYearlyCap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reserveCap(Long value, Integer year) {
        AllocationYear allocationYear = allocationYearRepository.findByYear(year);
        allocationYear.setPendingYearlyCap(allocationYear.getPendingYearlyCap() + value);
        allocationYearRepository.save(allocationYear);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void consumeCap(Long value, Integer year) {
        AllocationYear allocationYear = allocationYearRepository.findByYear(year);
        allocationYear.setPendingYearlyCap(allocationYear.getPendingYearlyCap() - value);
        allocationYear.setConsumedYearlyCap(allocationYear.getConsumedYearlyCap() + value);
        allocationYearRepository.save(allocationYear);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void releaseCap(Long value, Integer year) {
        AllocationYear allocationYear = allocationYearRepository.findByYear(year);
        allocationYear.setPendingYearlyCap(allocationYear.getPendingYearlyCap() - value);
        allocationYearRepository.save(allocationYear);
    }

    /**
     * Find all entries for the current phase.
     * @return a list of AllocationYear
     */
    public List<AllocationYear> getAllocationYearsForCurrentPhase() {
        AllocationPhase phase = allocationPhaseRepository.getAllocationPhaseOfYear(allocationConfigurationService.getAllocationYear());
        return allocationYearRepository.findByPhaseCode(phase.getCode());
    }
    
    /**
     * {@inheritDoc}
     */
    public List<AllowanceReport> getAllocationYearsForCurrentPhaseWithTotals() {

        List<AllocationYear> allocationYears = getAllocationYearsForCurrentPhase();
        List<AllowanceReport> result =  allocationYears.
                stream().
                map(this::toAllowanceReport).
                sorted(Comparator.comparing(AllowanceReport::getDescription)).
                collect(Collectors.toList());
        
        result.add(result.size(), phaseTotals(allocationYears));
        
        return result;
    }
    
    public List<IssuanceBlockSummary> getCapsForCurrentPhase() {
        Integer year = allocationConfigurationService.getAllocationYear();
        List<IssuanceBlockSummary> blocks = allocationYearRepository.getBlocks(year);
        for (IssuanceBlockSummary block : blocks) {
            block.setType(UnitType.ALLOWANCE);
        }
        return blocks;
    }
    
    private AllowanceReport toAllowanceReport(AllocationYear allocationYear) {
        AllowanceReport summary = new AllowanceReport();
        summary.setDescription(allocationYear.getYear().toString());
        summary.setCap(allocationYear.getInitialYearlyCap());
        summary.setEntitlement(allocationYear.getEntitlementYearly());
        summary.setIssued(allocationYear.getConsumedYearlyCap());
        summary.setAllocated(allocationYear.getAllocatedYearly());
        summary.setForAuction(allocationYear.getAuctionedYearly());
        summary.setAuctioned(allocationYear.getAuctionedYearly());
        
        return summary;
    }
    
    private AllowanceReport phaseTotals(List<AllocationYear> allocationYear) {
        AllowanceReport phaseTotals = new AllowanceReport();
        phaseTotals.setDescription("Total in phase");
        
        allocationYear.forEach(t-> {
            phaseTotals.setCap(phaseTotals.getCap() + t.getInitialYearlyCap());
            phaseTotals.setEntitlement(phaseTotals.getEntitlement() + t.getEntitlementYearly());
            phaseTotals.setIssued(phaseTotals.getIssued() + t.getConsumedYearlyCap());
            phaseTotals.setAllocated(phaseTotals.getAllocated() + t.getAllocatedYearly());
            phaseTotals.setForAuction(phaseTotals.getForAuction() + t.getAuctionedYearly());
            phaseTotals.setAuctioned(phaseTotals.getAuctioned() + t.getAuctionedYearly());            
        });
        
        return phaseTotals;
    }
}
