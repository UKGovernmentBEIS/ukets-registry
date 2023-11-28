package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.data.AllocationClassificationSummary;
import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.data.AllocationOverviewRow;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.domain.AllocationEntry;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for calculating the allocations.
 */
@Service
@AllArgsConstructor
public class AllocationCalculationService {

    /**
     * Repository for allocation entries.
     */
    private final AllocationEntryRepository allocationEntryRepository;

    /**
     * Repository for allocation years.
     */
    private final AllocationYearRepository allocationYearRepository;

    /**
     * Performs calculations for the provided allocation year and category.
     *
     * @param allocationYear The allocation year.
     * @param allocationCategory The allocation category.
     * @return the allocation overview.
     */
    public AllocationOverview calculateAllocationsOverview(Integer allocationYear, AllocationCategory allocationCategory) {
        AllocationOverview overview = new AllocationOverview();
        overview.setYear(allocationYear);
        overview.setCategory(allocationCategory);

        List<AllocationSummary> beneficiaryRecipients = new ArrayList<>();

        Set<Long> closedEntities = allocationEntryRepository.retrieveEntitiesWithInvalidAccountStatus();
        Set<Long> transferPendingEntities = allocationEntryRepository.retrieveEntitiesWithTransferPendingAccountStatus();

        List<AllocationType> allocationTypes = Arrays.stream(AllocationType.values())
                .filter(type -> type.getCategory() == allocationCategory)
                .toList();

        for (AllocationType allocationType : allocationTypes) {
            AllocationOverviewRow row = new AllocationOverviewRow();

            Integer accounts = 0;
            Integer withheldAccounts = 0;
            int excludedAccounts = 0;

            Integer closed = 0;
            Integer transferPending = 0;
            Long totalQuantity = 0L;

            final List<AllocationSummary> allocationSummaries =
                allocationEntryRepository.calculateAllocations(allocationYear, allocationType);
            for (AllocationSummary entry : allocationSummaries) {
                Long compliantEntityIdentifier = allocationEntryRepository.retrieveIdentifier(entry.getCompliantEntityId());
                excludedAccounts += allocationEntryRepository.
                        retrieveNumberOfExcludedEmissionsByCompliantEntityAndAllocationYear(compliantEntityIdentifier, allocationYear);
                if (closedEntities.contains(entry.getCompliantEntityId())) {
                    closed += 1;
                } else if (transferPendingEntities.contains(entry.getCompliantEntityId())) {
                    transferPending += 1;
                } else if (AllocationStatusType.WITHHELD.equals(entry.getStatus())) {
                    withheldAccounts += 1;

                } else if (entry.getRemaining() > 0) {
                    if (isCompliantEntityInactive(allocationYear, entry.getCompliantEntityId())) {
                        closed += 1;
                    } else {
                        accounts += 1;
                        totalQuantity += entry.getRemaining();
                        entry.setIdentifier(compliantEntityIdentifier);
                        entry.setAccountFullIdentifier(allocationEntryRepository.retrieveAccountFullIdentifier(entry.getCompliantEntityId()));
                        entry.setYear(allocationYear);
                        entry.setType(allocationType);
                        beneficiaryRecipients.add(entry);
                    }
                }
            }

            row.setAccounts(accounts);
            row.setAllocationType(allocationType);
            row.setQuantity(totalQuantity);
            row.setExcludedAccounts(excludedAccounts);
            row.setWithheldAccounts(withheldAccounts);
            row.setClosedAndFullySuspendedAccounts(closed);
            row.setTransferPendingAccounts(transferPending);
            overview.add(allocationType, row);
        }
        overview.setTotal(calculateTotals(overview.getRows().values()));
        overview.setTotalQuantity(overview.getTotal().getQuantity());
        overview.setBeneficiaryRecipients(beneficiaryRecipients);

        return overview;
    }

    /**
     * Calculates the totals.
     *
     * @param rows The rows.
     * @return the total row.
     */
    private AllocationOverviewRow calculateTotals(Collection<AllocationOverviewRow> rows) {
        AllocationOverviewRow total = new AllocationOverviewRow();
        total.setAccounts(0);
        total.setQuantity(0L);
        total.setWithheldAccounts(0);
        total.setExcludedAccounts(0);
        total.setClosedAndFullySuspendedAccounts(0);
        total.setTransferPendingAccounts(0);

        for (AllocationOverviewRow row : rows) {
            total.setQuantity(total.getQuantity() + row.getQuantity());
            total.setWithheldAccounts(total.getWithheldAccounts() + row.getWithheldAccounts());
            total.setExcludedAccounts(total.getExcludedAccounts() + row.getExcludedAccounts());
            total.setAccounts(total.getAccounts() + row.getAccounts());
            total.setClosedAndFullySuspendedAccounts(
                total.getClosedAndFullySuspendedAccounts() + row.getClosedAndFullySuspendedAccounts());
            total.setTransferPendingAccounts(total.getTransferPendingAccounts() + row.getTransferPendingAccounts());
        }
        return total;
    }

    /**
     * Calculates the compliant entity's allocation classification.
     *
     * @param year              the allocation year
     * @param compliantEntityId the compliant entity id
     * @return an AllocationClassificationSummary object
     */
    public AllocationClassificationSummary calculateAllocationClassification(Integer year, Long compliantEntityId) {

        List<AllocationClassificationSummary> classificationSummaries = allocationEntryRepository
            .calculateAllocationClassification(year, compliantEntityId);

        if (classificationSummaries.isEmpty()) {
            return new AllocationClassificationSummary(compliantEntityId, null);
        }

        return new AllocationClassificationSummary().transform(classificationSummaries);
    }

    /**
     * Performs update to the allocation status of an installation / aircraft operator.
     * @param accountIdentifier The account identifier.
     * @param quantity The quantity.
     * @param allocationType The allocation type.
     * @param allocationYear The allocation year.
     */
    @Transactional
    public void updateAllocationStatus(Long accountIdentifier, Long quantity, AllocationType allocationType,
                                       Integer allocationYear, TransactionType transactionType) {
        Long compliantEntityId = allocationEntryRepository.retrieveCompliantEntityId(accountIdentifier);
        AllocationEntry allocationEntry = allocationEntryRepository.findByCompliantEntityIdAndTypeAndAllocationYear_Year(compliantEntityId, allocationType, allocationYear);
        if (TransactionType.AllocateAllowances.equals(transactionType)) {
            allocationEntry.setAllocated(ObjectUtils.firstNonNull(allocationEntry.getAllocated(), 0L) + quantity);

        } else if (TransactionType.ReverseAllocateAllowances.equals(transactionType)) {
            allocationEntry.setReversed(ObjectUtils.firstNonNull(allocationEntry.getReversed(), 0L) + quantity);

        } else if (TransactionType.ExcessAllocation.equals(transactionType)) {
            allocationEntry.setReturned(ObjectUtils.firstNonNull(allocationEntry.getReturned(), 0L) + quantity);
        }
        allocationEntryRepository.save(allocationEntry);
        AllocationClassificationSummary summary = calculateAllocationClassification(allocationYear, compliantEntityId);
        String classification = null;
        if (Objects.nonNull(summary.getAllocationClassification())) {
            classification = AllocationClassification.valueOf(summary.getAllocationClassification()).name();            
        }

        allocationEntryRepository.updateAllocationClassification(compliantEntityId, classification);
    }

    /**
     * Updates the allocation status for the whole registry, for a specific
     * @param year The allocation year.
     */
    @Transactional
    public void calculateAndUpdateRegistryStatus(Integer year) {
        AllocationYear allocationYear = allocationYearRepository.findByYear(year);
        allocationYear.setEntitlementYearly(allocationYearRepository.calculateYearlyEntitlement(year));
        allocationYear.setAllocatedYearly(allocationYearRepository.calculateYearlyAllocated(year));
        allocationYear.setReversedYearly(allocationYearRepository.calculateYearlyReversed(year));
        allocationYear.setReturnedYearly(allocationYearRepository.calculateYearlyReturned(year));
        allocationYearRepository.save(allocationYear);
    }

    public Optional<AllocationSummary> getAllocationEntry(
        Long accountIdentifier, AllocationType allocationType, Integer allocationYear) {
        Long compliantEntityId = allocationEntryRepository.retrieveCompliantEntityId(accountIdentifier);
        return Optional.ofNullable(allocationEntryRepository.
            retrieveAllocationEntry(compliantEntityId, allocationType, allocationYear));
    }

    private boolean isCompliantEntityInactive(Integer year, Long compliantEntityId) {
        Integer startYear = allocationEntryRepository.retrieveEntityStartYear(compliantEntityId);
        Integer endYear = allocationEntryRepository.retrieveEntityEndYear(compliantEntityId);

        return startYear != null && year < startYear || endYear != null && year > endYear;
    }
}
