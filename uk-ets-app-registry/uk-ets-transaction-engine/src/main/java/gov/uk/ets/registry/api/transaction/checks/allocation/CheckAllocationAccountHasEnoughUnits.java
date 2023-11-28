package gov.uk.ets.registry.api.transaction.checks.allocation;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.service.AccountHoldingService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Business check: An allocation job cannot start if the allowances available in the transferring account are less
 * than the sum of all allowances remaining to be allocated.
 */
@Service("check8003")
@AllArgsConstructor
public class CheckAllocationAccountHasEnoughUnits extends ParentBusinessCheck {

    /**
     * Service for holdings.
     */
    private AccountHoldingService holdingService;

    /**
     * Persistence service for transactions.
     */
    private TransactionPersistenceService persistenceService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {

        AllocationOverview allocationOverview = context.get(AllocationOverview.class.getName(), AllocationOverview.class);
        AllocationOverview[] allocationOverviews = context.get(AllocationOverview[].class.getName(), AllocationOverview[].class);

        List<AllocationOverview> overviews = new ArrayList<>();
        Optional.ofNullable(allocationOverview).ifPresent(overviews::add);
        Optional.ofNullable(allocationOverviews).map(Arrays::asList).ifPresent(overviews::addAll);

        if (!isValid(overviews)) {
            addError(context, "An allocation job cannot start if the allowances available in the transferring account are less than the sum of all allowances remaining to be allocated");
        }

    }

    private boolean isValid(List<AllocationOverview> overviews) {

        Map<AllocationType, Long> totalQuantityPerAllocationType = overviews.stream()
            .map(AllocationOverview::getRows)
            .map(Map::entrySet)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getQuantity(), Long::sum));

        Long allocationRequested = totalQuantityPerAllocationType.getOrDefault(AllocationType.NAT, 0L) +
            totalQuantityPerAllocationType.getOrDefault(AllocationType.NAVAT, 0L);
        Long nerRequested = totalQuantityPerAllocationType.getOrDefault(AllocationType.NER, 0L);

        Long allocationAccountQuantity = getQuantity(AccountType.UK_ALLOCATION_ACCOUNT);
        Long nerAccountQuantity = getQuantity(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT);

        return allocationAccountQuantity >= allocationRequested && nerAccountQuantity >= nerRequested;
    }

    private Long getQuantity(AccountType accountType) {
        AccountSummary account = persistenceService.getAccount(accountType, AccountStatus.OPEN);
        TransactionBlockSummary block = new TransactionBlockSummary();
        block.setType(UnitType.ALLOWANCE);
        return holdingService.getQuantity(account.getIdentifier(), block);
    }


}
