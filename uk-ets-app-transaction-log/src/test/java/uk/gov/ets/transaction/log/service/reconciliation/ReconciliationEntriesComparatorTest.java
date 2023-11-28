package uk.gov.ets.transaction.log.service.reconciliation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.helper.BaseTest;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationFailedEntrySummary;

class ReconciliationEntriesComparatorTest extends BaseTest {


    private static final Long TEST_ACCOUNT_IDENTIFIER_1 = 1L;
    private static final Long TEST_ACCOUNT_IDENTIFIER_2 = 2L;
    private static final Long TEST_ACCOUNT_IDENTIFIER_3 = 3L;
    private static final Long TEST_ACCOUNT_IDENTIFIER_4 = 4L;

    @Test
    public void shouldReconcileWhenEntriesAreCorrect() {

        ArrayList<ReconciliationEntrySummary> registryReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e1 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e2 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 12345L);
        registryReconciliationEntries.add(e1);
        registryReconciliationEntries.add(e2);
        ArrayList<ReconciliationEntrySummary> ukTlReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e3 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e4 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 12345L);
        ukTlReconciliationEntries.add(e3);
        ukTlReconciliationEntries.add(e4);

        ReconciliationEntriesComparator comparator = ReconciliationEntriesComparator.builder()
            .withRegistryReconciliationEntries(registryReconciliationEntries)
            .withUkTlReconciliationEntries(ukTlReconciliationEntries)
            .build();

        List<ReconciliationFailedEntrySummary> failedEntries = comparator.compare();

        assertThat(failedEntries).hasSize(0);
    }

    @Test
    public void shouldNotReconcileWhenTotalsMismatch() {
        ArrayList<ReconciliationEntrySummary> registryReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e1 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 12345L);
        ReconciliationEntrySummary e2 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 12345L);
        registryReconciliationEntries.add(e1);
        registryReconciliationEntries.add(e2);
        ArrayList<ReconciliationEntrySummary> ukTlReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e3 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e4 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 12345L);
        ukTlReconciliationEntries.add(e3);
        ukTlReconciliationEntries.add(e4);

        ReconciliationEntriesComparator comparator = ReconciliationEntriesComparator.builder()
            .withRegistryReconciliationEntries(registryReconciliationEntries)
            .withUkTlReconciliationEntries(ukTlReconciliationEntries)
            .build();

        List<ReconciliationFailedEntrySummary> failedEntries = comparator.compare();

        assertThat(failedEntries).hasSize(1);
        ReconciliationFailedEntrySummary fe = failedEntries.get(0);
        assertThat(fe.getTotalInRegistry()).isEqualTo(12345L);
        assertThat(fe.getTotalInTransactionLog()).isEqualTo(1234L);
    }

    @Test
    public void shouldNotReconcileWhenRegistryHasMoreEntries() {
        ArrayList<ReconciliationEntrySummary> registryReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e1 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e2 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e5 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_3, UnitType.ALLOWANCE, 1234L);
        registryReconciliationEntries.add(e1);
        registryReconciliationEntries.add(e2);
        registryReconciliationEntries.add(e5);
        ArrayList<ReconciliationEntrySummary> ukTlReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e3 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e4 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 1234L);
        ukTlReconciliationEntries.add(e3);
        ukTlReconciliationEntries.add(e4);

        ReconciliationEntriesComparator comparator = ReconciliationEntriesComparator.builder()
            .withRegistryReconciliationEntries(registryReconciliationEntries)
            .withUkTlReconciliationEntries(ukTlReconciliationEntries)
            .build();

        List<ReconciliationFailedEntrySummary> failedEntries = comparator.compare();

        assertThat(failedEntries).hasSize(1);
        ReconciliationFailedEntrySummary fe = failedEntries.get(0);
        assertThat(fe.getTotalInRegistry()).isEqualTo(1234L);
        assertThat(fe.getTotalInTransactionLog()).isEqualTo(-1L);
    }

    @Test
    public void shouldNotReconcileWhenTransactionLogHasMoreEntries() {
        ArrayList<ReconciliationEntrySummary> registryReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e1 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e2 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 1234L);
        registryReconciliationEntries.add(e1);
        registryReconciliationEntries.add(e2);
        ArrayList<ReconciliationEntrySummary> ukTlReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e3 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e4 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e5 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_3, UnitType.ALLOWANCE, 1234L);

        ukTlReconciliationEntries.add(e3);
        ukTlReconciliationEntries.add(e4);
        ukTlReconciliationEntries.add(e5);

        ReconciliationEntriesComparator comparator = ReconciliationEntriesComparator.builder()
            .withRegistryReconciliationEntries(registryReconciliationEntries)
            .withUkTlReconciliationEntries(ukTlReconciliationEntries)
            .build();

        List<ReconciliationFailedEntrySummary> failedEntries = comparator.compare();

        assertThat(failedEntries).hasSize(1);
        ReconciliationFailedEntrySummary fe = failedEntries.get(0);
        assertThat(fe.getTotalInRegistry()).isEqualTo(-1L);
        assertThat(fe.getTotalInTransactionLog()).isEqualTo(1234L);
    }

    @Test
    public void shouldNotReconcileWhenBothRegistryAndTransactionLogHaveDifferentEntriesAndTotalsMismatch() {
        ArrayList<ReconciliationEntrySummary> registryReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e1 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 12345L);
        ReconciliationEntrySummary e2 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e6 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_3, UnitType.ALLOWANCE, 1234L);
        registryReconciliationEntries.add(e1);
        registryReconciliationEntries.add(e2);
        registryReconciliationEntries.add(e6);
        ArrayList<ReconciliationEntrySummary> ukTlReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e3 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        ReconciliationEntrySummary e4 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 12345L);
        ReconciliationEntrySummary e5 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_4, UnitType.ALLOWANCE, 1234L);

        ukTlReconciliationEntries.add(e3);
        ukTlReconciliationEntries.add(e4);
        ukTlReconciliationEntries.add(e5);

        ReconciliationEntriesComparator comparator = ReconciliationEntriesComparator.builder()
            .withRegistryReconciliationEntries(registryReconciliationEntries)
            .withUkTlReconciliationEntries(ukTlReconciliationEntries)
            .build();

        List<ReconciliationFailedEntrySummary> failedEntries = comparator.compare();

        assertThat(failedEntries).hasSize(4);
        failedEntries.sort(Comparator.comparing(ReconciliationEntrySummary::getAccountIdentifier));
        assertThat(failedEntries).extracting(ReconciliationFailedEntrySummary::getTotalInRegistry)
            .containsExactly(12345L, 1234L, 1234L, -1L);
        assertThat(failedEntries).extracting(ReconciliationFailedEntrySummary::getTotalInTransactionLog)
            .containsExactly(1234L, 12345L, -1L, 1234L);
    }

    @Test
    public void shouldNotReconcileWhenUnitTypesAreDifferent() {
        ArrayList<ReconciliationEntrySummary> registryReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e1 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 1234L);
        registryReconciliationEntries.add(e1);
        ArrayList<ReconciliationEntrySummary> ukTlReconciliationEntries = new ArrayList<>();
        ReconciliationEntrySummary e3 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.AAU, 1234L);
        ukTlReconciliationEntries.add(e3);

        ReconciliationEntriesComparator comparator = ReconciliationEntriesComparator.builder()
            .withRegistryReconciliationEntries(registryReconciliationEntries)
            .withUkTlReconciliationEntries(ukTlReconciliationEntries)
            .build();

        List<ReconciliationFailedEntrySummary> failedEntries = comparator.compare();

        assertThat(failedEntries).hasSize(2);
    }

}
