package gov.uk.ets.registry.api.reconciliation.repository;

import static org.junit.jupiter.api.Assertions.*;

import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create"})
class ReconciliationRepositoryTest {
    @Autowired
    private ReconciliationRepository reconciliationRepository;

    @Test
    void fetchLastCompletedReconciliation() throws ParseException {

        // given
        createReconciliation(1000L, "01/01/2020", ReconciliationStatus.COMPLETED);
        createReconciliation(2000L, "01/02/2020", ReconciliationStatus.INCONSISTENT);
        createReconciliation(3000L, "01/03/2020", ReconciliationStatus.INITIATED);

        Reconciliation reconciliation = reconciliationRepository.fetchLastCompletedReconciliation();

        assertEquals(1000L, reconciliation.getIdentifier());
    }

    void createReconciliation(Long identifier, String date, ReconciliationStatus status) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setIdentifier(identifier);
        reconciliation.setCreated(sdf.parse(date));
        reconciliation.setStatus(status);
        reconciliationRepository.saveAndFlush(reconciliation);
    }

}