package gov.uk.ets.registry.api.reconciliation.transfer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.ArrayList;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class ReconciliationSummaryTest {
    @Test
    void testTransferObjects() {
        ReconciliationSummary reconciliation = new ReconciliationSummary();
        reconciliation.setIdentifier(1000L);
        reconciliation.setStatus(ReconciliationStatus.INCONSISTENT);

        ReconciliationSummary reconciliation2 = new ReconciliationSummary();
        reconciliation2.setIdentifier(1001L);

        reconciliation.setEntries(
            new ArrayList<>() {{
                add(new ReconciliationEntrySummary() {{
                    setAccountIdentifier(1L);
                    setUnitType(UnitType.ALLOWANCE);
                    setTotal(100000L);
                }});
                add(new ReconciliationEntrySummary() {{
                    setAccountIdentifier(2L);
                    setUnitType(UnitType.ALLOWANCE);
                    setTotal(200L);
                }});
        }});

        reconciliation.setFailedEntries(new ArrayList<>() {{
            add(new ReconciliationFailedEntrySummary() {{
                setAccountIdentifier(1L);
                setTotalInRegistry(100000L);
                setTotalInTransactionLog(100020L);
                setUnitType(UnitType.ALLOWANCE);
            }});
            add(new ReconciliationFailedEntrySummary() {{
                setAccountIdentifier(2L);
                setTotalInRegistry(200L);
                setTotalInTransactionLog(400L);
                setUnitType(UnitType.ALLOWANCE);
            }});
        }});

        assertNotNull(reconciliation.getEntries());

        EqualsVerifier.forClass(ReconciliationSummary.class)
            .withOnlyTheseFields("identifier")
            .withPrefabValues(ReconciliationEntrySummary.class,
                reconciliation.getEntries().get(0),
                reconciliation.getEntries().get(1))
            .withPrefabValues(ReconciliationFailedEntrySummary.class,
                reconciliation.getFailedEntries().get(0),
                reconciliation.getFailedEntries().get(1))
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();

        EqualsVerifier.forClass(ReconciliationEntrySummary.class)
            .withOnlyTheseFields("accountIdentifier", "unitType")
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();

        EqualsVerifier.forClass(ReconciliationFailedEntrySummary.class)
            .withOnlyTheseFields("accountIdentifier", "unitType")
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
            .verify();

        assertNotNull(ReconciliationStatus.getFinalStatuses());
        assertFalse(ReconciliationStatus.getFinalStatuses().isEmpty());
        assertNotNull(ReconciliationStatus.getPendingStatuses());
        assertFalse(ReconciliationStatus.getPendingStatuses().isEmpty());

    }

}