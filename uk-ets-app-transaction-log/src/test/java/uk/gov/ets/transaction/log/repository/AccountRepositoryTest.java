package uk.gov.ets.transaction.log.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.ets.transaction.log.domain.Account;
import uk.gov.ets.transaction.log.helper.BaseJpaTest;


public class AccountRepositoryTest extends BaseJpaTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        Account account = new Account();
        account.setAccountName("Test Account");
        account.setCommitmentPeriodCode(2);
        account.setIdentifier(10000848L);
        account.setCheckDigits(83);
        account.setFullIdentifier("UK-100-10000848-2-83");
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(2020, 6, 16);
        account.setOpeningDate(cal.getTime());
        entityManager.persist(account);
    }

    @Test
    @DisplayName("Accounts are fetched correctly.")
    public void findAll() {
        List<Account> accounts = accountRepository.findAll();
        assertEquals(1L, accounts.size());
    }

    @Test
    @DisplayName("Account by identifier is fetched correctly.")
    public void findByIdentifier() {
        Optional<Account> account = accountRepository.findByIdentifier(10000848L);
        assertTrue(account.isPresent());
    }
}
