package uk.gov.ets.transaction.log.service.reconciliation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import uk.gov.ets.transaction.log.domain.AccountBasicInfo;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;
import uk.gov.ets.transaction.log.helper.BaseTest;
import uk.gov.ets.transaction.log.repository.ReconciliationRepository;
import uk.gov.ets.transaction.log.repository.TransactionRepository;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

class ReconciliationServiceTest extends BaseTest {


    private static final Long ACCOUNT_IDENTIFIER_1 = 1012L;
    private static final Long ACCOUNT_IDENTIFIER_2 = 1013L;
    private static final Long ACCOUNT_IDENTIFIER_3 = 1014L;

    @Mock
    private ReconciliationRepository reconciliationRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Spy
    private UnitBlockRepository unitBlockRepository;
    @InjectMocks
    ReconciliationService cut;

    @Test
    public void shouldRetrieveDistinctAccounts() {
        AccountBasicInfo a1 = new AccountBasicInfo();
        a1.setAccountIdentifier(ACCOUNT_IDENTIFIER_1);
        AccountBasicInfo a2 = new AccountBasicInfo();
        a2.setAccountIdentifier(ACCOUNT_IDENTIFIER_2);
        AccountBasicInfo a3 = new AccountBasicInfo();
        a3.setAccountIdentifier(ACCOUNT_IDENTIFIER_3);

        LocalDateTime now = LocalDateTime.now();

        Date date = convertToDate(now.minus(1, ChronoUnit.SECONDS));
        when(reconciliationRepository.findLatestReconciliationDate()).thenReturn(date);

        Transaction t1 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100052", TransactionStatus.COMPLETED, now);
        t1.setAcquiringAccount(a1);
        t1.setTransferringAccount(a2);
        Transaction t2 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100053", TransactionStatus.COMPLETED, now);
        t2.setAcquiringAccount(a2);
        t2.setTransferringAccount(a1);
        Transaction t3 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100054", TransactionStatus.COMPLETED, now);
        t3.setAcquiringAccount(a1);
        t3.setTransferringAccount(a3);
        Transaction t4 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100055", TransactionStatus.COMPLETED, now);
        t4.setAcquiringAccount(a3);
        t4.setTransferringAccount(a1);
        when(transactionRepository.findByStartedAfterAndStatusEquals(date, TransactionStatus.COMPLETED)).thenReturn(
            Arrays.asList(t1, t2, t3, t4));

        cut.calculateReconciliationEntries();

        verify(unitBlockRepository, times(1)).retrieveEntriesForAllAccounts();
    }
}
