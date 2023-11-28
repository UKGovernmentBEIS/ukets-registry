package uk.gov.ets.transaction.log.messaging.types;

import java.util.function.Predicate;

/**
 * Reusable predicates for the {@link ReconciliationEntrySummary} class.
 */
public class ReconciliationEntryPredicate {

    private ReconciliationEntryPredicate() {

    }

    public static Predicate<ReconciliationEntrySummary> accountIdentifiersEqual(ReconciliationEntrySummary e1) {
        return e2 -> e2.getAccountIdentifier().equals(e1.getAccountIdentifier());
    }

    public static Predicate<ReconciliationEntrySummary> unitTypesEqual(ReconciliationEntrySummary e1) {
        return e2 -> e2.getUnitType().equals(e1.getUnitType());
    }

    public static Predicate<ReconciliationEntrySummary> totalsUnequal(ReconciliationEntrySummary e1) {
        return e2 -> !e2.getTotal().equals(e1.getTotal());
    }
}
