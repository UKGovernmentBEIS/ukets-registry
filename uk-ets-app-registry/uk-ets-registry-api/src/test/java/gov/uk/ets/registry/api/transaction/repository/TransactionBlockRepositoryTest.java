package gov.uk.ets.registry.api.transaction.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import java.util.Set;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PostgresJpaTest
class TransactionBlockRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TransactionBlockRepository transactionBlockRepository;
    
    @BeforeEach
    void init() {
    }

    @Test
    void findDistinctUnitTypesByTransactionStatusInAndTransactionTypeNotInAndTransactionExecutionDateBefore() 
        throws Exception {

        Set<UnitType> types = transactionBlockRepository.
            findUnitTypesByAcquiringAccountAndStatusInAndLastUpdatedBefore(
             1L,
            Set.of(TransactionStatus.COMPLETED,TransactionStatus.REVERSED),
            new Date());

        assertEquals(0, types.size());
    }    
    
}
