package uk.gov.ets.transaction.log.service.reconciliation;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import uk.gov.ets.transaction.log.domain.AccountBasicInfo;
import uk.gov.ets.transaction.log.domain.Reconciliation;
import uk.gov.ets.transaction.log.domain.ReconciliationFailedEntry;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.domain.type.ReconciliationStatus;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.helper.BaseJpaTest;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationFailedEntrySummary;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationSummary;
import uk.gov.ets.transaction.log.repository.ReconciliationRepository;

//Added these properties:
//spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true
//spring.jpa.properties.hibernate.globally_quoted_identifiers=true
//when upgrading spring-boot from 2.5.6 to 2.7.10 due to
//h2 database dependency upgrade.The root cause of the problem is the use
//of the SQL reserved keyword year in entity UnitBlock and hence in the generated  SQL query
@Import({ReconciliationService.class, ObjectMapper.class})
@TestPropertySource(properties = {"spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true","spring.jpa.properties.hibernate.globally_quoted_identifiers=true"})
public class ReconciliationServiceIntegrationTest extends BaseJpaTest {

    private static final Long TEST_ACCOUNT_IDENTIFIER_1 = 1L;
    private static final Long TEST_ACCOUNT_IDENTIFIER_2 = 2L;
    private static final Long TEST_ACCOUNT_IDENTIFIER_3 = 3L;
    private static final Long TEST_ACCOUNT_IDENTIFIER_4 = 4L;

    @Autowired
    ReconciliationService service;

    @MockBean
    KafkaTemplate<String, ReconciliationSummary> kafkaTemplate;

    @Autowired
    ReconciliationRepository repository;

    @Test
    public void shouldPersistReconciliationWithFailedEntriesCorrectly() {


        ArrayList<ReconciliationFailedEntrySummary> failedEntries = new ArrayList<>();

        ReconciliationFailedEntrySummary fe1 = new ReconciliationFailedEntrySummary();
        fe1.setAccountIdentifier(1L);
        fe1.setTotalInRegistry(1234L);
        fe1.setTotalInTransactionLog(12345L);
        fe1.setUnitType(UnitType.ALLOWANCE);

        ReconciliationFailedEntrySummary fe2 = new ReconciliationFailedEntrySummary();
        fe2.setAccountIdentifier(2L);
        fe2.setTotalInRegistry(1234L);
        fe2.setTotalInTransactionLog(-1L);
        fe2.setUnitType(UnitType.ALLOWANCE);
        failedEntries.add(fe1);
        failedEntries.add(fe2);

        ReconciliationSummary summary = new ReconciliationSummary();
        summary.setFailedEntries(failedEntries);
        summary.setStatus(ReconciliationStatus.INCONSISTENT);

        service.saveReconciliation(failedEntries, ReconciliationStatus.INCONSISTENT, summary);

        // IMPORTANT! DO NOT REMOVE!
        flushAndClear();

        List<Reconciliation> reconciliations = repository.findAll();

        assertThat(reconciliations).hasSize(1);
        Reconciliation reconciliation = reconciliations.get(0);
        assertThat(reconciliation.getFailedEntries()).hasSize(2);
        assertThat(reconciliation.getStatus()).isEqualTo(ReconciliationStatus.INCONSISTENT);
    }

    @Test
    public void shouldPerformReconciliation() {
        ReconciliationSummary registryReconciliationSummary = new ReconciliationSummary();
        ArrayList<ReconciliationEntrySummary> registryReconciliationEntries = new ArrayList<>();

        ReconciliationEntrySummary e1 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_1, UnitType.ALLOWANCE, 12345L);
        ReconciliationEntrySummary e2 =
            createReconciliationEntrySummary(TEST_ACCOUNT_IDENTIFIER_2, UnitType.ALLOWANCE, 1234L);
        registryReconciliationEntries.add(e1);
        registryReconciliationEntries.add(e2);
        registryReconciliationSummary.setEntries(registryReconciliationEntries);
        registryReconciliationSummary.setIdentifier(1L);

        AccountBasicInfo a1 = new AccountBasicInfo();
        a1.setAccountIdentifier(TEST_ACCOUNT_IDENTIFIER_1);
        AccountBasicInfo a2 = new AccountBasicInfo();
        a2.setAccountIdentifier(TEST_ACCOUNT_IDENTIFIER_2);

        Transaction transaction1 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100052", TransactionStatus.COMPLETED,
                LocalDateTime.of(2020, 7, 1, 0, 0, 0), a1, a2);
        Transaction transaction2 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100053", TransactionStatus.COMPLETED,
                LocalDateTime.of(2020, 7, 1, 0, 0, 1), a2, a1);

        entityManager.persist(transaction1);
        entityManager.persist(transaction2);

        UnitBlock unitBlock1 = createUnitBlockForAccountWithBlocks(TEST_ACCOUNT_IDENTIFIER_1, 23L, 40L);
        UnitBlock unitBlock2 = createUnitBlockForAccountWithBlocks(TEST_ACCOUNT_IDENTIFIER_2, 40L, 52L);

        entityManager.persist(unitBlock1);
        entityManager.persist(unitBlock2);

        flushAndClear();

        service.performReconciliation(registryReconciliationSummary);

        flushAndClear();

        List<Reconciliation> reconciliations = repository.findAll();

        assertThat(reconciliations).hasSize(1);
        Reconciliation reconciliation = reconciliations.get(0);
        List<ReconciliationFailedEntry> failedEntries = reconciliation.getFailedEntries();
        failedEntries.sort(Comparator.comparing(ReconciliationFailedEntry::getAccountIdentifier));
        assertThat(failedEntries).hasSize(2);
        assertThat(failedEntries).extracting(ReconciliationFailedEntry::getQuantityRegistry)
            .containsExactly(12345L, 1234L);
        assertThat(failedEntries).extracting(ReconciliationFailedEntry::getQuantityTransactionLog)
            .containsExactly(18L, 13L);
        assertThat(reconciliation.getStatus()).isEqualTo(ReconciliationStatus.INCONSISTENT);


    }
}
