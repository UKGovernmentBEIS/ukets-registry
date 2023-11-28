package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.itl.notice.ITLNoticeService;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.AccountTotalRepository;
import gov.uk.ets.registry.api.transaction.repository.AcquiringAccountRepository;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestMethodOrder(OrderAnnotation.class)
class TransactionAccountServiceTest {

    @Mock
    private TransactionPersistenceService mockTransactionPersistenceService;

    @Mock
    private AccountTotalRepository mockAccountTotalRepository;

    @Mock
    private AcquiringAccountRepository acquiringAccountRepository;


    private TransactionAccountService transactionAccountService;

    @Mock
    private ITLNoticeService itlNoticeService;
    
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        transactionAccountService =
            new TransactionAccountService(mockTransactionPersistenceService, mockAccountTotalRepository,
                new PredefinedAcquiringAccountsProperties(), acquiringAccountRepository, itlNoticeService);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }
    
    @Test
    @Order(1)
    @DisplayName("Acquiring account registry code is not internal and the account is not trusted")
    void belongsToTrustedList1() {
        long transferringAccountIdentifier = 100L;
        long acquiringAccountIdentifier = 200L;
        String acquiringAccountFullIdentifier = "JP-100-10000068";
        
        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.ExternalTransfer);
        transactionSummary.setTransferringAccountIdentifier(transferringAccountIdentifier);
        transactionSummary.setAcquiringAccountFullIdentifier(acquiringAccountFullIdentifier);
        
        AccountSummary acquiringAccountSummary = new AccountSummary();
        acquiringAccountSummary.setFullIdentifier(acquiringAccountFullIdentifier);
        acquiringAccountSummary.setRegistryCode("JP");
        Mockito.when(mockAccountTotalRepository.getAccountSummary(acquiringAccountIdentifier))
               .thenReturn(acquiringAccountSummary);
        boolean belongs = transactionAccountService
            .belongsToTrustedList(transactionSummary, acquiringAccountSummary);
        Assertions.assertFalse(belongs);
    }

    @Test
    @Order(2)
    @DisplayName("Acquiring account registry code is internal but transferring and acquiring account are not under the same account holder")
    void belongsToTrustedList2() {
        long transferringAccountIdentifier = 1L;
        long acquiringAccountIdentifier = 2L;
        String acquiringAccountRegistryCode = "GB";
        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setIdentifier(acquiringAccountIdentifier);
        accountSummary.setFullIdentifier("UK-100-10000068-2-6");
        Mockito.when(mockAccountTotalRepository.getAccountHolderIdentifier(transferringAccountIdentifier))
            .thenReturn(10L);
        Mockito.when(mockAccountTotalRepository.getAccountHolderIdentifier(acquiringAccountIdentifier))
            .thenReturn(11L);
        Mockito.when(mockAccountTotalRepository.getAccountSummary(acquiringAccountIdentifier))
               .thenReturn(accountSummary);
        boolean belongs = transactionAccountService
            .isTrustedAccount(transferringAccountIdentifier, accountSummary,
                acquiringAccountRegistryCode);
        Assertions.assertFalse(belongs);
    }

    @Test
    @Order(3)
    @DisplayName("Acquiring account registry code is internal and both acquiring accounts are under the same account holder")
    void belongsToTrustedList3() {
        long transferringAccountIdentifier = 1L;
        long acquiringAccountIdentifier = 2L;
        String acquiringAccountRegistryCode = "GB";
        AccountSummary accountSummary = new AccountSummary();
        accountSummary.setIdentifier(acquiringAccountIdentifier);
        Mockito.when(mockAccountTotalRepository.getAccountHolderIdentifier(transferringAccountIdentifier))
            .thenReturn(10L);
        Mockito.when(mockAccountTotalRepository.getAccountHolderIdentifier(acquiringAccountIdentifier))
            .thenReturn(10L);

        boolean belongs = transactionAccountService
            .isTrustedAccount(transferringAccountIdentifier, accountSummary,
                acquiringAccountRegistryCode);
        Assertions.assertTrue(belongs);
    }

    @Test
    @Order(4)
    @DisplayName("Acquiring account registry code is internal and both acquiring accounts are under the same account holder via different method")
    void belongsToTrustedList4() {
        long transferringAccountIdentifier = 1L;
        long acquiringAccountIdentifier = 2L;

        Mockito.when(mockAccountTotalRepository.getAccountHolderIdentifier(transferringAccountIdentifier))
            .thenReturn(10L);
        Mockito.when(mockAccountTotalRepository.getAccountHolderIdentifier(acquiringAccountIdentifier))
            .thenReturn(10L);

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.SurrenderAllowances);
        transactionSummary.setTransferringAccountIdentifier(transferringAccountIdentifier);

        AccountSummary acquiringAccountSummary =
            new AccountSummary(acquiringAccountIdentifier, RegistryAccountType.NONE,
                KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);


        boolean belongs = transactionAccountService
            .belongsToTrustedList(transactionSummary, acquiringAccountSummary);
        Assertions.assertTrue(belongs);
    }
    
    //See also JIRA-6732
    @Test
    @Order(5)
    @DisplayName("Acquiring account registry code is external")
    void belongsToTrustedList5() {
        long transferringAccountIdentifier = 1L;
        long acquiringAccountIdentifier = 2L;

        Mockito.when(mockAccountTotalRepository.getAccountHolderIdentifier(transferringAccountIdentifier))
            .thenReturn(10L);
        Mockito.when(mockAccountTotalRepository.getAccountHolderIdentifier(acquiringAccountIdentifier))
            .thenReturn(null);

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.ExternalTransfer);
        transactionSummary.setTransferringAccountIdentifier(transferringAccountIdentifier);

        AccountSummary acquiringAccountSummary =
            new AccountSummary(acquiringAccountIdentifier, RegistryAccountType.NONE,
                KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                null, "SI", "SI-121-5034828", 0);
        AcquiringAccountInfo acquiringAccountInfo =  new AcquiringAccountInfo(acquiringAccountSummary.getFullIdentifier(),"SI Trusted Account",null,true);
        Mockito.when(acquiringAccountRepository.retrieveOtherTrustedAccountsAsAcquiringAccounts(transferringAccountIdentifier))
        .thenReturn(List.of(acquiringAccountInfo));

        boolean belongs = transactionAccountService
            .belongsToTrustedList(transactionSummary, acquiringAccountSummary);
        Assertions.assertTrue(belongs);
    }
}
