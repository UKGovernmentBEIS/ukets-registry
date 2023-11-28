package gov.uk.ets.registry.api.reconciliation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationFailedEntry;
import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationHistory;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationFailedEntryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationHistoryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})
@ContextConfiguration(classes = ReconciliationTestApplication.class)
class ReconciliationModelTest {

    @Autowired
    ReconciliationRepository reconciliationRepository;

    @Autowired
    ReconciliationFailedEntryRepository reconciliationFailedEntryRepository;

    @Autowired
    ReconciliationHistoryRepository reconciliationHistoryRepository;

    @Test
    @Transactional
    void testBasicPersistenceOperations() {
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setIdentifier(10000L);
        reconciliation.setCreated(new Date());
        reconciliation.setStatus(ReconciliationStatus.INITIATED);
        reconciliation.setData("Some serialised data here");
        reconciliationRepository.saveAndFlush(reconciliation);

        assertNotNull(reconciliation.getId());
        assertTrue(reconciliation.getId() > 0);

        ReconciliationFailedEntry entry = new ReconciliationFailedEntry();
        entry.setAccountIdentifier(1L);
        entry.setQuantityRegistry(100_000L);
        entry.setQuantityTransactionLog(100_001L);
        entry.setReconciliation(reconciliation);
        reconciliationFailedEntryRepository.saveAndFlush(entry);

        assertNotNull(entry.getId());
        assertTrue(entry.getId() > 0);

        ReconciliationHistory history = ReconciliationHistory.builder()
            .reconciliation(reconciliation)
            .date(new Date())
            .status(ReconciliationStatus.INITIATED)
            .build();

        reconciliationHistoryRepository.saveAndFlush(history);

        history = ReconciliationHistory.builder()
            .reconciliation(reconciliation)
            .date(new Date())
            .status(ReconciliationStatus.COMPLETED)
            .build();
        reconciliationHistoryRepository.saveAndFlush(history);

        assertEquals(2, reconciliationHistoryRepository.findAll().size());

    }

    @Test
    void testEqualsAndHashCode() {
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setIdentifier(1L);

        Reconciliation reconciliation2 = new Reconciliation();
        reconciliation2.setIdentifier(2L);

        ReconciliationFailedEntry entry1 = new ReconciliationFailedEntry();
        entry1.setReconciliation(reconciliation);
        entry1.setAccountIdentifier(100L);

        ReconciliationFailedEntry entry2 = new ReconciliationFailedEntry();
        entry2.setReconciliation(reconciliation);
        entry2.setAccountIdentifier(200L);

        ReconciliationHistory history1 = ReconciliationHistory.builder()
            .reconciliation(reconciliation)
            .status(ReconciliationStatus.INITIATED)
            .build();

        ReconciliationHistory history2 = ReconciliationHistory.builder()
            .reconciliation(reconciliation)
            .status(ReconciliationStatus.COMPLETED)
            .build();

        EqualsVerifier.forClass(Reconciliation.class)
            .withOnlyTheseFields("identifier")
            .withPrefabValues(ReconciliationFailedEntry.class, entry1, entry2)
            .withPrefabValues(ReconciliationHistory.class, history1, history2)
            .verify();

        EqualsVerifier.forClass(ReconciliationFailedEntry.class)
            .withOnlyTheseFields("reconciliation", "accountIdentifier")
            .withPrefabValues(Reconciliation.class, reconciliation, reconciliation2)
            .verify();

        EqualsVerifier.forClass(ReconciliationHistory.class)
            .withOnlyTheseFields("reconciliation", "status")
            .withPrefabValues(Reconciliation.class, reconciliation, reconciliation2)
            .verify();
    }

}