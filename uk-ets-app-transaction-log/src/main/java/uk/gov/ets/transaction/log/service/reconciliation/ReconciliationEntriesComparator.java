package uk.gov.ets.transaction.log.service.reconciliation;

import static java.util.stream.Collectors.toList;
import static uk.gov.ets.transaction.log.messaging.types.ReconciliationEntryPredicate.accountIdentifiersEqual;
import static uk.gov.ets.transaction.log.messaging.types.ReconciliationEntryPredicate.totalsUnequal;
import static uk.gov.ets.transaction.log.messaging.types.ReconciliationEntryPredicate.unitTypesEqual;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;
import lombok.Builder;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationFailedEntrySummary;

@Builder(setterPrefix = "with")
public class ReconciliationEntriesComparator {

    public static final ToLongFunction<ReconciliationEntrySummary> NEGATIVE_TOTAL_FUNCTION = e -> -1L;

    private final List<ReconciliationEntrySummary> registryReconciliationEntries;
    private final List<ReconciliationEntrySummary> ukTlReconciliationEntries;

    /**
     * Retrieves failed entries by comparing the two entry lists for unique entries.
     * <br>
     * Appends to them the failed entries due to totals mismatch.
     *
     * @return the failed entries
     */
    List<ReconciliationFailedEntrySummary> compare() {


        LefSideUniqueEntryHandler registryEntriesHandler = LefSideUniqueEntryHandler.builder()
            .withLeftSideEntries(registryReconciliationEntries)
            .withRightSideEntries(ukTlReconciliationEntries)
            .withRegistryTotalCalculator(ReconciliationEntrySummary::getTotal)
            .withUkTlTotalCalculator(NEGATIVE_TOTAL_FUNCTION)
            .build();
        List<ReconciliationFailedEntrySummary> registryOnlyFailedEntries = registryEntriesHandler.handle();

        LefSideUniqueEntryHandler ukTlEntriesHandler = LefSideUniqueEntryHandler.builder()
            .withLeftSideEntries(ukTlReconciliationEntries)
            .withRightSideEntries(registryReconciliationEntries)
            .withRegistryTotalCalculator(NEGATIVE_TOTAL_FUNCTION)
            .withUkTlTotalCalculator(ReconciliationEntrySummary::getTotal)
            .build();
        List<ReconciliationFailedEntrySummary> ukTlOnlyFailedEntries = ukTlEntriesHandler.handle();

        List<ReconciliationFailedEntrySummary> otherFailedEntries = new ArrayList<>();
        registryReconciliationEntries
            .forEach(e -> ukTlReconciliationEntries.stream()
                .filter(accountIdentifiersEqual(e).and(unitTypesEqual(e).and(totalsUnequal(e))))
                .findFirst()
                .map(e2 -> toFailedEntry(e, e2))
                .ifPresent(otherFailedEntries::add));

        return Stream.of(registryOnlyFailedEntries, ukTlOnlyFailedEntries, otherFailedEntries)
            .flatMap(List::stream)
            .collect(toList());
    }

    private ReconciliationFailedEntrySummary toFailedEntry(ReconciliationEntrySummary registryEntry,
                                                           ReconciliationEntrySummary ukTlEntry) {
        return new ReconciliationFailedEntrySummary(registryEntry, registryEntry.getTotal(), ukTlEntry.getTotal());
    }


}
