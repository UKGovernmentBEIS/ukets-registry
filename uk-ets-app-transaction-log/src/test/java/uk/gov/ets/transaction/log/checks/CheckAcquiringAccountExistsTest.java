package uk.gov.ets.transaction.log.checks;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.domain.Account;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.AccountRepository;

class CheckAcquiringAccountExistsTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    CheckAcquiringAccountExists checkAcquiringAccountExists;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testAcquiringAccountMissing() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setAcquiringAccountIdentifier(1L);
        }});

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.empty());

        checkAcquiringAccountExists.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testAcquiringAccountExists() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setAcquiringAccountIdentifier(1L);
        }});

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(new Account()));

        checkAcquiringAccountExists.execute(context);
        assertFalse(context.hasError());
    }

}