package gov.uk.ets.registry.api.transaction.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.helper.persistence.TransactionAccountBalanceTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TransactionAccountBalanceTestHelper.AddTransactionAccountBalanceCommand;
import gov.uk.ets.registry.api.transaction.domain.AccountBalance;
import gov.uk.ets.registry.api.transaction.domain.TransactionAccountBalance;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PostgresJpaTest
class TransactionAccountBalanceRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TransactionAccountBalanceRepository transactionAccountBalanceRepository;
    
    TransactionAccountBalanceTestHelper helper;
    
    private final Long transferringAccountIdentifer = 1000082L;
    
    @BeforeEach
    void init() {
        helper = new TransactionAccountBalanceTestHelper(entityManager);
        helper.addTransactionAccountBalance(
            AddTransactionAccountBalanceCommand
            .builder()
            .transactionIdentifier("UK100043")
            .transferringAccountIdentifer(transferringAccountIdentifer)
            .transferringAccountBalance(79L)
            .transferringAccountBalanceUnitType(UnitType.ALLOWANCE)
            .acquiringAccountIdentifer(1000091L)
            .acquiringAccountBalance(109L)
            .acquiringAccountBalanceUnitType(UnitType.ALLOWANCE)
            .lastUpdated(Date.from(LocalDateTime.now().minusDays(12).atOffset(ZoneOffset.UTC).toInstant()))
            .build()
        );
        helper.addTransactionAccountBalance(
            AddTransactionAccountBalanceCommand
            .builder()
            .transactionIdentifier("UK100045")
            .transferringAccountIdentifer(transferringAccountIdentifer)
            .transferringAccountBalance(802L)
            .transferringAccountBalanceUnitType(UnitType.ALLOWANCE)
            .acquiringAccountIdentifer(1000227L)
            .acquiringAccountBalance(109L)
            .acquiringAccountBalanceUnitType(UnitType.ALLOWANCE)
            .lastUpdated(Date.from(LocalDateTime.now().minusDays(5).atOffset(ZoneOffset.UTC).toInstant()))
            .build()
        );
        
        helper.addTransactionAccountBalance(
            AddTransactionAccountBalanceCommand
            .builder()
            .transactionIdentifier("UK100047")
            .transferringAccountIdentifer(transferringAccountIdentifer)
            .transferringAccountBalance(99L)
            .transferringAccountBalanceUnitType(UnitType.ALLOWANCE)
            .acquiringAccountIdentifer(1000654L)
            .acquiringAccountBalance(109L)
            .acquiringAccountBalanceUnitType(UnitType.ALLOWANCE)
            .lastUpdated(Date.from(LocalDateTime.now().minusDays(1).atOffset(ZoneOffset.UTC).toInstant()))
            .build()
        );
    }

    @Test
    void findByTransactionIdentifier() {

        Optional<TransactionAccountBalance> balance = transactionAccountBalanceRepository.findByTransactionIdentifier("UK100043");

        assertTrue(balance.isPresent());
    } 
    
    
    @Test
    void findByAccountIdentifierAndDate() {

        Optional<AccountBalance> balance = transactionAccountBalanceRepository.findByAccountIdentifierAndDate(transferringAccountIdentifer,Date.from(LocalDateTime.now().minusDays(3).atOffset(ZoneOffset.UTC).toInstant()));

        assertTrue(balance.isPresent());
        assertEquals(802L,balance.get().getBalance());
    } 
}
