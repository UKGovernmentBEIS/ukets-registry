package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class ApprovalServiceTest {

    @Mock
    private TransactionAccountService transactionAccountService;

    private ApprovalService approvalService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        approvalService = new ApprovalService(transactionAccountService);
    }

    @Test
    void testScenario1() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.SurrenderAllowances);
        transaction.setTransferringAccountIdentifier(1L);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);
        Mockito.when(transactionAccountService.singlePersonApprovalRequired(transaction.getTransferringAccountIdentifier()))
               .thenReturn(true);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, false));

    }

    @Test
    void testScenario2() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.InternalTransfer);
        transaction.setTransferringAccountIdentifier(1L);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, false));

    }

    @Test
    void testScenario3() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.SurrenderAllowances);
        transaction.setTransferringAccountIdentifier(1L);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Mockito.when(transactionAccountService.belongsToTrustedList(transaction, account)).thenReturn(true);

        Mockito.when(transactionAccountService.approvalOfSecondAuthorisedRepresentativeIsRequired(1L))
            .thenReturn(false);

        Assertions.assertFalse(approvalService.isApprovalRequired(transaction, false));

    }

    @Test
    void testScenario4() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.SurrenderAllowances);
        transaction.setTransferringAccountIdentifier(1L);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Mockito.when(transactionAccountService.getAccountHolderIdentifier(1L))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.getAccountHolderIdentifier(2L))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.approvalOfSecondAuthorisedRepresentativeIsRequired(1L))
            .thenReturn(true);
        Mockito.when(transactionAccountService.singlePersonApprovalRequired(transaction.getTransferringAccountIdentifier()))
               .thenReturn(true);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, false));

    }

    @Test
    void testScenario5() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.SurrenderAllowances);
        transaction.setTransferringAccountIdentifier(1L);
        transaction.setAcquiringAccountIdentifier(2L);
        transaction.setAcquiringAccountType(KyotoAccountType.PERSON_HOLDING_ACCOUNT);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);
        Mockito.when(transactionAccountService.getAccountHolderIdentifier(1L))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.getAccountHolderIdentifier(2L))
            .thenReturn(1000L);
        Mockito.when(transactionAccountService.singlePersonApprovalRequired(transaction.getTransferringAccountIdentifier()))
               .thenReturn(true);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, false));

    }

    @Test
    void testScenarioAdmin1() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.SurrenderAllowances);
        transaction.setTransferringAccountIdentifier(1L);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, true));

    }

    @Test
    void testScenarioAdmin2() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.InternalTransfer);
        transaction.setTransferringAccountIdentifier(1L);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, true));

    }

    @Test
    void testScenarioAdmin3() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.SurrenderAllowances);
        transaction.setTransferringAccountIdentifier(1L);
        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Mockito.when(transactionAccountService.getAccountHolderIdentifier(1L))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.getAccountHolderIdentifier(2L))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.approvalOfSecondAuthorisedRepresentativeIsRequired(1L))
            .thenReturn(false);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, true));

    }

    @Test
    void testScenarioAdmin4() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.SurrenderAllowances);
        transaction.setTransferringAccountIdentifier(1L);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Mockito.when(transactionAccountService.getAccountHolderIdentifier(1L))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.getAccountHolderIdentifier(2L))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.approvalOfSecondAuthorisedRepresentativeIsRequired(1L))
            .thenReturn(true);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, true));

    }

    @Test
    void testScenarioAdmin5() {

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.SurrenderAllowances);
        transaction.setTransferringAccountIdentifier(1L);

        AccountSummary account =
            new AccountSummary(2L, RegistryAccountType.NONE, KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                null, "GB", "GB-121-2-0-22", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Mockito.when(transactionAccountService.getAccountHolderIdentifier(1L))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.getAccountHolderIdentifier(2L))
            .thenReturn(1000L);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, true));

        transaction.setType(TransactionType.ExternalTransfer);
        transaction.setTransferringRegistryCode("JP");
        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, true));
        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, false));

    }

    @DisplayName("Transaction Issuance of allowances requires approval")
    @Test
    void testScenarioAllowances() {
        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.IssueAllowances);

        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, false));
        Assertions.assertTrue(approvalService.isApprovalRequired(transaction, true));

    }

    @DisplayName("ARs on accounts with singlePersonApprovalRequired false should not require 2 AR.")
    @Test
    void testScenarioAuthRepReturnExcellAllocation() {

        Long transferringAccountIdentifier = 10000050L;
        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.ExcessAllocation);
        transaction.setTransferringAccountIdentifier(transferringAccountIdentifier);

        Long acquiringAccountIdentifier = 10000003L;
        AccountSummary account =
            new AccountSummary(acquiringAccountIdentifier, RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, KyotoAccountType.PARTY_HOLDING_ACCOUNT,
                null, "UK", "UK-100-10000003-0-46", 0);

        Mockito.when(transactionAccountService.populateAcquiringAccount(transaction)).thenReturn(account);

        Mockito.when(transactionAccountService.getAccountHolderIdentifier(transferringAccountIdentifier))
            .thenReturn(10L);
        Mockito.when(transactionAccountService.getAccountHolderIdentifier(acquiringAccountIdentifier))
            .thenReturn(11L);
        Mockito.when(transactionAccountService.approvalOfSecondAuthorisedRepresentativeIsRequired(transferringAccountIdentifier))
            .thenReturn(true);
        Mockito.when(transactionAccountService.singlePersonApprovalRequired(transferringAccountIdentifier))
            .thenReturn(false);

        Assertions.assertFalse(approvalService.isApprovalRequired(transaction, false));

    }

}
