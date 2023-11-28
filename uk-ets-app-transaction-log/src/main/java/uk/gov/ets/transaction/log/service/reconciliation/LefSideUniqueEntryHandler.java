package uk.gov.ets.transaction.log.service.reconciliation;

import static java.util.stream.Collectors.toList;
import static uk.gov.ets.transaction.log.messaging.types.ReconciliationEntryPredicate.accountIdentifiersEqual;
import static uk.gov.ets.transaction.log.messaging.types.ReconciliationEntryPredicate.unitTypesEqual;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import lombok.Builder;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationFailedEntrySummary;

/**
 * Given two lists left and right, creates a {@link ReconciliationFailedEntrySummary}
 * for each entry found in the left list and not found in the right list
 * (= an entry that satisfies a predefined Predicate).
 */
@Builder(setterPrefix = "with")
class LefSideUniqueEntryHandler {
    /**
     * the list we want to filter for unique entries.
     */
    private final List<ReconciliationEntrySummary> leftSideEntries;
    /**
     * the list we will compare with.
     */
    private final List<ReconciliationEntrySummary> rightSideEntries;
    /**
     * a {@link Function} that retrieves the total for the registry side.
     */
    private final ToLongFunction<ReconciliationEntrySummary> registryTotalCalculator;
    /**
     * a {@link Function} that retrieves the total for the UKTL side.
     */
    private final ToLongFunction<ReconciliationEntrySummary> ukTlTotalCalculator;

    /**
     * Filters the leftSideEntries list for entries that are not in rightSideEntries list given a predefined Predicate.
     * <br>
     * Maps every found entry to a {@link ReconciliationFailedEntrySummary} using the provided {@link Function}s for total calculations.
     *
     * @return a list of {@link ReconciliationFailedEntrySummary}s for every record found in leftSideEntries and not found in rightSideEntries
     */
    List<ReconciliationFailedEntrySummary> handle() {
        return leftSideEntries.stream()
            .filter(e -> rightSideEntries.stream()
                .noneMatch(accountIdentifiersEqual(e).and(unitTypesEqual(e))))
            .map(e -> new ReconciliationFailedEntrySummary(e, registryTotalCalculator.applyAsLong(e),
                ukTlTotalCalculator.applyAsLong(e)))
            .collect(toList());
    }
}
