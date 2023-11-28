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

class CheckTransferringAccountExistsTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    CheckTransferringAccountExists checkTransferringAccountExists;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testTransferringAccountMissing() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setTransferringAccountIdentifier(1L);
        }});

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.empty());

        checkTransferringAccountExists.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testTransferringAccountExists() {
        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(new TransactionNotification(){{
            setTransferringAccountIdentifier(1L);
        }});

        when(accountRepository.findByIdentifier(1L)).thenReturn(Optional.of(new Account()));

        checkTransferringAccountExists.execute(context);
        assertFalse(context.hasError());
    }

}
