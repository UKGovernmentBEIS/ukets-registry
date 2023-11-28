package gov.uk.ets.registry.api.itl.reconciliation.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.itl.reconciliation.domain.ITLReconAuditTrailTxLog;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
public class ITLReconAuditTrailTxLogRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ITLReconAuditTrailTxLogRepository reconAuditTrailTxLogRepository;

    @BeforeEach
    public void setUp() {

    }
    
    @Test
    void save() {
        ITLReconAuditTrailTxLog reconAuditTrailTxLog = new ITLReconAuditTrailTxLog();
        reconAuditTrailTxLog.setAccountRegistryCode("GB");
        reconAuditTrailTxLog.setAcquiringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        reconAuditTrailTxLog.setAcquiringRegistryAccount(123L);
        reconAuditTrailTxLog.setNotificationIdentifier(99999L);
        reconAuditTrailTxLog.setTransactionDate(new Date());
        reconAuditTrailTxLog.setTransactionId("GB0000000");
        reconAuditTrailTxLog.setTransferringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        reconAuditTrailTxLog.setTransferringRegistryAccount(321L);
        reconAuditTrailTxLog.setTransferringRegistryCode("GB");
        reconAuditTrailTxLog.setType(TransactionType.IssueOfAAUsAndRMUs);
        
        reconAuditTrailTxLog = reconAuditTrailTxLogRepository.save(reconAuditTrailTxLog);
        
        assertNotNull(reconAuditTrailTxLog.getId());
    }
}
