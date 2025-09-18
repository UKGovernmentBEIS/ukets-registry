package gov.uk.ets.registry.api.itl.reconciliation.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconciliationLog;
import gov.uk.ets.registry.api.itl.reconciliation.type.ITLReconciliationPhase;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
public class ITLReconciliationLogRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ITLReconciliationLogRepository reconciliationRepository;

    @BeforeEach
    public void setUp() {
    }

    @Test
    void findById() {

        String reconId = "GB10000118";
        ITLReconciliationLog recon = new ITLReconciliationLog();
        recon.setReconId(reconId);
        recon.setReconActionBeginDatetime(new Date());
        recon.setReconPhaseCode(ITLReconciliationPhase.TOTALS);
        entityManager.persist(recon);

        Optional<ITLReconciliationLog> reconciliation = reconciliationRepository.findById(reconId);
        assertTrue(reconciliation.isPresent());
    }
    
    @Test
    void findByLatestSnapshotDatetimeShouldReturnEmptyOptional() {

        Optional<ITLReconciliationLog> reconciliation = reconciliationRepository.findByLatestSnapshotDatetime();
        assertFalse(reconciliation.isPresent());
    }
    
    @Test
    void findByLatestSnapshotDatetimeShouldReturnLatestRecon() {

        ITLReconciliationLog recon1 = new ITLReconciliationLog();
        recon1.setReconId("GB10000118");
        recon1.setReconSnapshotDatetime(Date.from(LocalDateTime.of(2020, 12, 6, 21, 42, 25).toInstant(ZoneOffset.UTC)));
        recon1.setReconActionBeginDatetime(new Date());
        recon1.setReconPhaseCode(ITLReconciliationPhase.TOTALS);
        entityManager.persist(recon1);

        ITLReconciliationLog recon2 = new ITLReconciliationLog();
        recon2.setReconId("GB10000119");
        recon2.setReconSnapshotDatetime(Date.from(LocalDateTime.of(2020, 12, 6, 21, 55, 12).toInstant(ZoneOffset.UTC)));
        recon2.setReconActionBeginDatetime(new Date());
        recon2.setReconPhaseCode(ITLReconciliationPhase.TOTALS);
        entityManager.persist(recon2);
        
        Optional<ITLReconciliationLog> reconciliation = reconciliationRepository.findByLatestSnapshotDatetime();
        assertTrue(reconciliation.isPresent());
        assertEquals(reconciliation.get().getReconSnapshotDatetime(),recon2.getReconSnapshotDatetime());
    }
}
