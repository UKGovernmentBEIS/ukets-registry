package gov.uk.ets.registry.api.allocation.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.allocation.AccountSampleEntity;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.util.Set;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})
public class AllocationEntryRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AllocationEntryRepository allocationEntryRepository;
    
    @BeforeEach
    public void setUp() throws Exception {
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setCompliantEntityId(100L);
        account1.setAccountStatus(AccountStatus.CLOSED);
        AccountSampleEntity account2 = new AccountSampleEntity();
        account2.setCompliantEntityId(101L);
        account2.setAccountStatus(AccountStatus.SUSPENDED);
        AccountSampleEntity account3 = new AccountSampleEntity();
        account3.setCompliantEntityId(102L);
        account3.setAccountStatus(AccountStatus.TRANSFER_PENDING);
        
        entityManager.persist(account1);
        entityManager.persist(account2);
        entityManager.persist(account3);
    }
    
    @Test
    public void retrieveEntitiesWithInvalidAccountStatus() {
        Set<Long> results = allocationEntryRepository.retrieveEntitiesWithInvalidAccountStatus();
        assertEquals(2, results.size());
    }
    
    @Test
    public void retrieveEntitiesWithTransferPendingAccountStatus() {
        Set<Long> results = allocationEntryRepository.retrieveEntitiesWithTransferPendingAccountStatus();
        assertEquals(1, results.size());
    }
}
