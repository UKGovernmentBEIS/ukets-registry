package gov.uk.ets.registry.api.reconciliation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationFailedEntry;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationFailedEntryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.service.ReconciliationService;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationFailedEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create"})
public class UpdateReconciliationIntegrationTest {
    @Autowired
    private ReconciliationRepository reconciliationRepository;
    @Autowired
    private ReconciliationFailedEntryRepository reconciliationFailedEntryRepository;

    ReconciliationService service;

    @BeforeEach
    void setup() {
        service = new ReconciliationService(reconciliationRepository, null, null, reconciliationFailedEntryRepository);
    }

    @Sql(statements = {"create sequence reconciliation_identifier_seq minvalue 0 maxvalue 999999999999999999 increment by 1 start with 10000 cache 1 no cycle;"})
    @Test
    @Transactional
    void test() {
        // given
        ReconciliationFailedEntrySummary reconciliationFailedEntrySummary = new ReconciliationFailedEntrySummary();
        reconciliationFailedEntrySummary.setAccountIdentifier(123213L);
        reconciliationFailedEntrySummary.setTotalInTransactionLog(1000L);
        reconciliationFailedEntrySummary.setTotalInRegistry(2000L);
        reconciliationFailedEntrySummary.setUnitType(UnitType.CER);
        Reconciliation reconciliation = service.createReconciliation(new Date());
        ReconciliationSummary reconciliationSummary = ReconciliationSummary.builder()
            .status(ReconciliationStatus.INCONSISTENT)
            .failedEntries(List.of(reconciliationFailedEntrySummary))
            .identifier(reconciliation.getIdentifier())
            .build();

        // when
        service.updateReconciliation(reconciliationSummary);

        // then
        reconciliation = reconciliationRepository.findByIdentifier(reconciliation.getIdentifier());

        List<ReconciliationFailedEntry> failedEntries = reconciliationFailedEntryRepository.findAll();
        assertEquals(1, failedEntries.size());
        assertEquals(reconciliation.getIdentifier(), failedEntries.get(0).getReconciliation().getIdentifier());
    }
}
