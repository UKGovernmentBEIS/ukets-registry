package uk.gov.ets.transaction.log.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import uk.gov.ets.transaction.log.domain.Reconciliation;
import uk.gov.ets.transaction.log.domain.type.ReconciliationStatus;
import uk.gov.ets.transaction.log.helper.BaseJpaTest;


class ReconciliationRepositoryTest extends BaseJpaTest {

    @Autowired
    private ReconciliationRepository reconciliationRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void shouldRetrieveMaximumDate() {

        Reconciliation r1 = new Reconciliation();
        LocalDateTime now = LocalDateTime.now();
        r1.setCreated(convertToDate(now.plus(1, ChronoUnit.SECONDS)));
        r1.setStatus(ReconciliationStatus.COMPLETED);
        testEntityManager.persistAndFlush(r1);

        Reconciliation r2 = new Reconciliation();
        r2.setCreated(convertToDate(now.plus(2, ChronoUnit.SECONDS)));
        r2.setStatus(ReconciliationStatus.COMPLETED);
        testEntityManager.persistAndFlush(r2);

        Date latestReconciliationDate = reconciliationRepository.findLatestReconciliationDate();

        assertThat(latestReconciliationDate).isEqualToIgnoringMillis(convertToDate(now.plus(2, ChronoUnit.SECONDS)));
    }

    @Test
    public void shouldRetrieveMaximumDateOnlyBetweenCompletedReconciliations() {
        // given
        LocalDateTime now = LocalDateTime.now();
        saveReconciliation(convertToDate(now.plus(1, ChronoUnit.SECONDS)), ReconciliationStatus.COMPLETED);
        Date expectedLastCompletedDate = convertToDate(now.plus(2, ChronoUnit.SECONDS));
        saveReconciliation(expectedLastCompletedDate, ReconciliationStatus.COMPLETED);
        saveReconciliation(convertToDate(now.plus(3, ChronoUnit.SECONDS)), ReconciliationStatus.INCONSISTENT);
        saveReconciliation(convertToDate(now.plus(4, ChronoUnit.SECONDS)), ReconciliationStatus.INITIATED);
        saveReconciliation(convertToDate(now.plus(5, ChronoUnit.SECONDS)), ReconciliationStatus.INCONSISTENT);

        // when
        Date latestReconciliationDate = reconciliationRepository.findLatestReconciliationDate();
        // then
        assertThat(latestReconciliationDate).isEqualToIgnoringMillis(expectedLastCompletedDate);
    }

    private void saveReconciliation(Date created, ReconciliationStatus status) {
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setCreated(created);
        reconciliation.setStatus(status);
        testEntityManager.persistAndFlush(reconciliation);
    }
}
