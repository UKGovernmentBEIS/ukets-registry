package gov.uk.ets.registry.api.transaction.checks;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.allocation.service.AllocationPhaseCapService;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountAllowedTransferOutsideTrustedListRule;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountCanHoldUnits;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountCanHoldUnitsBeingTransferred;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountExists;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountForCentralTransfersFromAllocationAccount;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountForCentralTransfersFromAuctionOrMsrAccount;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountForCentralTransfersFromNER;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountHasNotAllTransactionRestrictedStatus;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountHasNotClosedStatus;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountHasNotFullySuspendedStatus;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountHasNotTransferPendingStatus;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountHasValidCheckDigits;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountHasValidType;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountInequality;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountIsNotCdmSopAdaptationFundAccount;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringAccountIsPreviousPeriodSurplusReserve;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckAcquiringTransferringAccountEquality;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckFieldAccountIdentifier;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckFieldAccountType;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckFieldAcquiringAccountProvided;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckFieldCommitmentPeriod;
import gov.uk.ets.registry.api.transaction.checks.acquiring.CheckFieldRegistryCode;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckInvalidUnitTypesForTransaction;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckInvolvementOfMoreUnitTypes;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckIssuanceLevelOfAllowances;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckIssuanceLevelOfAssignedAmountUnits;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckIssuanceLevelOfRemovalUnits;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckPendingReconciliation;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckPendingTransaction;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckQuantityAvailableInBalance;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckQuantityIsSpecified;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckQuantityOfAAUToRetire;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckQuantityValid;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckReturnQuantity;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckSameYearSameTableReturnExcessPendingTask;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckUnitBlocksInSinglePeriod;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckUnitsSubjectToSop;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckUnitsValidApplicableCommitmentPeriod;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckValidDateCarryOverPeriod;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckValidReverseSurrenderPeriod;
import gov.uk.ets.registry.api.transaction.checks.generic.CheckWhetherTransactionIsAlreadyReversed;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountExists;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountForPendingTransactionOfSameType;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountHasNotAllTransactionsRestrictedStatus;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountHasNotSomeTransactionsRestrictedRegardlessOfRole;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountHasNotSomeTransactionsRestrictedStatus;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountHasValidCheckDigits;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountHasValidType;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountIsPreviousPeriodSurplusReserve;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountNotClosed;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountNotSuspended;
import gov.uk.ets.registry.api.transaction.checks.transferring.CheckTransferringAccountNotTransferPending;
import gov.uk.ets.registry.api.transaction.common.GeneratorService;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.TransactionConnectionRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.service.AccountHoldingService;
import gov.uk.ets.registry.api.transaction.service.LevelService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = BusinessChecksConfiguration.class)
@SpringBootTest(classes = {CheckAcquiringAccountExists.class, CheckAcquiringAccountHasValidCheckDigits.class,
                           CheckAcquiringAccountCanHoldUnitsBeingTransferred.class, CheckTransferringAccountHasValidCheckDigits.class,
                           CheckTransferringAccountExists.class, CheckUnitBlocksInSinglePeriod.class, CheckAcquiringAccountCanHoldUnits.class,
                           CheckTransferringAccountNotClosed.class, CheckTransferringAccountNotSuspended.class,
                           CheckTransferringAccountHasValidType.class,
                           CheckUnitsValidApplicableCommitmentPeriod.class, CheckQuantityAvailableInBalance.class,
                           CheckInvalidUnitTypesForTransaction.class,
                           CheckAcquiringAccountHasNotClosedStatus.class, CheckAcquiringAccountHasNotFullySuspendedStatus.class,
                           CheckAcquiringAccountHasValidType.class,
                           CheckQuantityIsSpecified.class, CheckQuantityValid.class, CheckInvolvementOfMoreUnitTypes.class,
                           CheckIssuanceLevelOfRemovalUnits.class,
                           CheckIssuanceLevelOfAssignedAmountUnits.class, CheckPendingTransaction.class, CheckUnitsSubjectToSop.class,
                           CheckTransferringAccountIsPreviousPeriodSurplusReserve.class,
                           CheckAcquiringAccountIsNotCdmSopAdaptationFundAccount.class,
                           CheckAcquiringAccountIsPreviousPeriodSurplusReserve.class, CheckTransferringAccountHasNotSomeTransactionsRestrictedStatus.class,
                           CheckTransferringAccountNotTransferPending.class,
                           CheckFieldAccountIdentifier.class, CheckFieldAccountType.class, CheckFieldAcquiringAccountProvided.class,
                           CheckFieldCommitmentPeriod.class, CheckFieldRegistryCode.class, CheckValidDateCarryOverPeriod.class,
                           CheckAcquiringTransferringAccountEquality.class,
                           CheckIssuanceLevelOfAllowances.class, CheckAcquiringAccountForCentralTransfersFromNER.class,
                           CheckAcquiringAccountForCentralTransfersFromAllocationAccount.class,
                           CheckAcquiringAccountForCentralTransfersFromAuctionOrMsrAccount.class,
                           CheckPendingReconciliation.class, CheckTransferringAccountForPendingTransactionOfSameType.class,
                           CheckQuantityOfAAUToRetire.class, CheckAcquiringAccountHasNotTransferPendingStatus.class,
                           CheckAcquiringAccountAllowedTransferOutsideTrustedListRule.class, CheckAcquiringAccountInequality.class,
                           CheckReturnQuantity.class, CheckSameYearSameTableReturnExcessPendingTask.class,
                           CheckValidReverseSurrenderPeriod.class, CheckWhetherTransactionIsAlreadyReversed.class,
                           CheckAcquiringAccountHasNotAllTransactionRestrictedStatus.class, CheckTransferringAccountHasNotAllTransactionsRestrictedStatus.class,
                           CheckTransferringAccountHasNotSomeTransactionsRestrictedRegardlessOfRole.class
})
class BusinessCheckServiceTest {

    @MockBean
    protected TransactionProposalService transactionProposalService;
    @MockBean
    TransactionAccountService transactionAccountService;
    @MockBean
    GeneratorService generatorService;
    @MockBean
    LevelService levelService;
    @MockBean
    TransactionRepository transactionRepository;
    @MockBean
    TransactionConnectionRepository transactionConnectionRepository;
    @MockBean
    AccountHoldingService accountHoldingService;
    @MockBean
    AllocationConfigurationService allocationConfigurationService;
    @MockBean
    AllocationPhaseCapService allocationPhaseCapService;
    @MockBean
    ReconciliationRepository reconciliationRepository;

    @MockBean
    AllocationCalculationService allocationCalculationService;
    @MockBean
    ObjectMapper objectMapper;

    private BusinessCheckService businessCheckService;
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private BusinessChecksConfiguration businessChecksConfiguration;

    @BeforeEach
    public void setup() {
        BusinessCheckFactory businessCheckFactory = new BusinessCheckFactory(beanFactory, businessChecksConfiguration);
        businessCheckService = new BusinessCheckService(businessCheckFactory);
    }

    @Test
    void testBusinessPersonHoldingAccountInternalTransferWithNonKyotoUnitsAccountSuspendedNoAcquiringAccount() {

        TransactionSummaryMock transaction =
            getTransaction(getTransactionBlockSummary(UnitType.NON_KYOTO), TransactionType.InternalTransfer,
                KyotoAccountType.PERSON_HOLDING_ACCOUNT, "UK43215", "UK12345");

        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .kyotoAccountType(KyotoAccountType.PERSON_HOLDING_ACCOUNT)
            .registryAccountType(RegistryAccountType.NONE)
            .accountStatus(AccountStatus.SUSPENDED)
            .build();


        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(null);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(accountSummaryMock);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction));

    }

    @Test
    void testBusinessPersonHoldingAccountInternalTransferWithNonKyotoUnitsAccountSuspended() {

        TransactionSummaryMock transaction =
            getTransaction(getTransactionBlockSummary(UnitType.NON_KYOTO), TransactionType.InternalTransfer,
                KyotoAccountType.PERSON_HOLDING_ACCOUNT, "UK43215", "UK12345");

        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .kyotoAccountType(KyotoAccountType.PERSON_HOLDING_ACCOUNT)
            .registryAccountType(RegistryAccountType.NONE)
            .accountStatus(AccountStatus.SUSPENDED)
            .build();


        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(accountSummaryMock);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(accountSummaryMock);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction));
    }

    @Test
    void testBusinessPersonHoldingAccountInternalTransferWithNonKyotoUnitsAccountClosed() {

        TransactionSummaryMock transaction =
            getTransaction(getTransactionBlockSummary(UnitType.NON_KYOTO), TransactionType.InternalTransfer,
                KyotoAccountType.PERSON_HOLDING_ACCOUNT, "UK43215", "UK12345");

        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .kyotoAccountType(KyotoAccountType.PERSON_HOLDING_ACCOUNT)
            .registryAccountType(RegistryAccountType.NONE)
            .accountStatus(AccountStatus.CLOSED)
            .build();


        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(accountSummaryMock);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(accountSummaryMock);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction));
    }

    @Test
    void testBusinessCheckPartyHoldingAccountIssuanceHasTransferringAccountWithInvalidDigits_InvalidAqcuiringAccount() {

        TransactionSummaryMock transaction =
            getTransaction(getTransactionBlockSummary(UnitType.NON_KYOTO), TransactionType.IssueOfAAUsAndRMUs,
                KyotoAccountType.PARTY_HOLDING_ACCOUNT, "UK43215", "UK12345");

        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .registryAccountType(RegistryAccountType.NONE)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .build();

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(accountSummaryMock);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(accountSummaryMock);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction));
    }

    @Test
    void testBusinessCheckPartyHoldingAccountIssuanceHasTransferringAccountWithInvalidDigits_InvalidAqcuiringAccountWithRmuUnits() {

        TransactionSummaryMock transaction =
            getTransaction(getTransactionBlockSummary(UnitType.RMU), TransactionType.IssueOfAAUsAndRMUs,
                KyotoAccountType.PARTY_HOLDING_ACCOUNT, "UK43215", "UK12345");

        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .registryAccountType(RegistryAccountType.NONE)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .build();

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(accountSummaryMock);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(accountSummaryMock);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction));
    }

    @Test
    void testBusinessCheckPartyHoldingAccountIssuanceHasTransferringAccountWithInvalidDigits_InvalidAqcuiringAccountWithAauUnits() {

        TransactionSummaryMock transaction =
            getTransaction(getTransactionBlockSummary(UnitType.AAU), TransactionType.IssueOfAAUsAndRMUs,
                KyotoAccountType.PARTY_HOLDING_ACCOUNT, "UK43215", "UK12345");

        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .registryAccountType(RegistryAccountType.NONE)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .build();

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(accountSummaryMock);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(accountSummaryMock);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction));
    }

    @Test
    void testIssuanceTwoUniTypes() {

        List<TransactionBlockSummary> blocks = new ArrayList<>();
        blocks.add(TransactionBlockSummary.builder().type(UnitType.RMU).quantity("10").build());
        blocks.add(TransactionBlockSummary.builder().type(UnitType.AAU).quantity("10").build());

        TransactionSummaryMock transaction =
            getTransaction(blocks, TransactionType.IssueOfAAUsAndRMUs, KyotoAccountType.PARTY_HOLDING_ACCOUNT,
                "UK43215", "UK12345");

        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .registryAccountType(RegistryAccountType.NONE)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .build();

        validDigits(accountSummaryMock, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(accountSummaryMock);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction));
    }

    @Test
    void testDeletionSomeTransactionsRestricted() {

        List<TransactionBlockSummary> blocks = new ArrayList<>();
        blocks.add(TransactionBlockSummary.builder().type(UnitType.ALLOWANCE).quantity("1").build());
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
                                                           .type(TransactionType.DeletionOfAllowances)
                                                           .transferringAccountIdentifier(1003L)
                                                           .blocks(getBlock(UnitType.ALLOWANCE,
                                                                            CommitmentPeriod.CP0,
                                                                            CommitmentPeriod.CP0,
                                                                            null,
                                                                            "1"))
                                                           .build();

        AccountSummary transferringAccount = new AccountSummary(1003L,
                                                                RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                                                                KyotoAccountType.PARTY_HOLDING_ACCOUNT,
                                                                AccountStatus.SOME_TRANSACTIONS_RESTRICTED,
                                                                "UK",
                                                                "UK-100-1003-2-76",

                                                                0,
                                                                100L,
                                                                UnitType.ALLOWANCE);

        validDigits(transferringAccount, true);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(transferringAccount.getIdentifier(), blocks.get(0))).thenReturn(100L);
        BusinessCheckException exception = assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction, true));
        assertEquals("Transactions from accounts with status SOME TRANSACTIONS RESTRICTED are not allowed.", exception.getErrors().get(0).getMessage());
        assertEquals(1014, exception.getErrors().get(0).getCode());
    }


    @Test
    void testBusinessCheckPartyHoldingAccountIssuanceHasTransferringAccountWithInvalidDigits_InvalidAqcuiringAccountWithoutBlocks() {

        TransactionSummaryMock transactionSummaryMock = TransactionSummaryMock.builder()
            .type(TransactionType.IssueOfAAUsAndRMUs)
            .transferringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .acquiringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .acquiringAccountFullIdentifier("UK12345")
            .transferringAccountFullIdentifier("UK43215")
            .build();

        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .registryAccountType(RegistryAccountType.NONE)
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .build();

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(accountSummaryMock);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(accountSummaryMock);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transactionSummaryMock));

    }

    @Test
    void testTransferToSopForFirstExternalTransferInvalidUnits() {
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45");
        AccountSummary acquiringAccount = getAccount("CDM-100-1100-0");

        assertNotNull(transferringAccount);
        assertNotNull(acquiringAccount);
        TransactionSummaryMock transaction = getTransaction(getTransactionBlockSummary(UnitType.ALLOWANCE),
            TransactionType.TransferToSOPforFirstExtTransferAAU, KyotoAccountType.PARTY_HOLDING_ACCOUNT,
            transferringAccount.getFullIdentifier(),
            acquiringAccount.getFullIdentifier());

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(3006, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferNotEnoughUnits() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0", RegistryAccountType.NONE, AccountStatus.OPEN);
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.OPEN);

        validDigits(transferringAccount, true);

        when(accountHoldingService.getQuantity(any(), any())).thenReturn(1L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(3005, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    private void validDigits(AccountSummary transferringAccount, boolean valid) {
        Assertions.assertNotNull(transferringAccount);
        Mockito.when(generatorService.validateCheckDigits(transferringAccount.getKyotoAccountType().getCode(),
            transferringAccount.getIdentifier(),
            transferringAccount.getCommitmentPeriod(), transferringAccount.getCheckDigits())).thenReturn(valid);
    }

    @Test
    void testExternalTransferSuccessful() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0", RegistryAccountType.NONE, AccountStatus.OPEN);
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.OPEN);

        validDigits(transferringAccount, true);

        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        when(transactionAccountService.belongsToTrustedList(any(), any())).thenReturn(true);

        businessCheckService.performChecks(transaction);
    }

    @Test
    void testExternalTransferWrongCheckDigits() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0", RegistryAccountType.NONE, AccountStatus.OPEN);
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN);

        validDigits(transferringAccount, false);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1001, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountDoesNotExist() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(null);

        assertEquals(1002, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountClosed() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.CLOSED);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1003, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountSuspended() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.SUSPENDED);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1004, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountInvalidType() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-280-10000002-2-45");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1007, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferAcquiringAccountMustBePPSR() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-130-123-2-1");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(2012, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountMustBePPSR() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");


        AccountSummary acquiringAccount = getAccount("JP-130-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-123-2-1");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(1009, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    private AccountSummary getAccount(String fullIdentifier) {
        return getAccount(fullIdentifier, RegistryAccountType.NONE, AccountStatus.OPEN);
    }

    private AccountSummary getAccount(String fullIdentifier, RegistryAccountType registryAccountType, AccountStatus accountStatus) {
        return AccountSummary.parse(fullIdentifier, registryAccountType, accountStatus);
    }

    @Test
    void testExternalTransferAcquiringAccountInvalidType() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.UK_AUCTION_DELIVERY_ACCOUNT, AccountStatus.OPEN);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1007, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferAcquiringAccountCdmSop() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("CDM-100-1100-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45");

        validDigits(transferringAccount, true);

        Mockito.when(transactionAccountService.getSopAccountFullIdentifier()).thenReturn("CDM-100-1100-0");

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(2009, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountSuspendedPartially() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.SUSPENDED_PARTIALLY);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1004, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountSuspendedPartiallyAdministrator() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.SUSPENDED_PARTIALLY);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);
        when(transactionAccountService.belongsToTrustedList(any(), any())).thenReturn(true);

        businessCheckService.performChecks(transaction, true);
    }

    @Test
    void testExternalTransferTransferringAccountBlockByAdministrator() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.SOME_TRANSACTIONS_RESTRICTED);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        when(transactionAccountService.belongsToTrustedList(any(), any())).thenReturn(true);

        assertEquals(1005, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountAllTransactionsRestrictedStatus() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.ALL_TRANSACTIONS_RESTRICTED);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1012, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferTransferringAccountSomeTransactionRestrictedStatus() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.SOME_TRANSACTIONS_RESTRICTED);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(1005, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

    }


    @Test
    void testExternalTransferTransferringAccountTransferPending() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "JP-100-12345-0");

        AccountSummary acquiringAccount = getAccount("JP-100-12345-0");
        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-45", RegistryAccountType.NONE, AccountStatus.TRANSFER_PENDING);

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1011, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferNoAccountEntered() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "---");


        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-99");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(2501, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferInvalidRegistry() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "XX-100-12345-0-22");

        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-99");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(2502, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferInvalidType() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "GB-1-12345-0-22");

        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-99");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(2503, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testExternalTransferInvalidIdentifier() {
        List<TransactionBlockSummary> blocks =
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10");

        TransactionSummary transaction = getTransaction(blocks, "GB-100-xxx-0-22");

        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-99");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(2504, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    private List<TransactionBlockSummary> getBlock(
        UnitType unitType, CommitmentPeriod originalPeriod, CommitmentPeriod applicablePeriod,
        EnvironmentalActivity environmentalActivity, String quantity) {
        return List.of(TransactionBlockSummary.builder()
                .type(unitType)
                .originalPeriod(originalPeriod)
                .applicablePeriod(applicablePeriod)
                .environmentalActivity(environmentalActivity)
                .quantity(quantity)
                .build());
    }


    @Test
    void testExternalTransferInvalidPeriod() {
        TransactionSummary transaction = getTransaction(
            getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"), "GB-100-123-xxx-22");

        AccountSummary transferringAccount = getAccount("GB-100-10000002-2-99");

        validDigits(transferringAccount, true);

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(2505, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testCarryOverTransferInvalidPeriod() {
        TransactionSummaryMock transaction =
            getTransaction(getTransactionBlockSummary(UnitType.AAU), TransactionType.CarryOver_AAU,
                KyotoAccountType.PARTY_HOLDING_ACCOUNT, "UK-54321", "UK-12345");
        AccountSummaryMock accountSummaryMock = AccountSummaryMock.builder()
            .fullIdentifier("UK-12345")
            .identifier(1L)
            .commitmentPeriod(2)
            .registryCode("UK")
            .kyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .build();

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(accountSummaryMock);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(accountSummaryMock);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        Assertions.assertThrows(BusinessCheckException.class, () -> businessCheckService.performChecks(transaction));
    }

    @Test
    void testArt37CancellationTransfer_CheckTransferringAccountForPendingTransactionOfSameType() {
        String ART_37_CANCELLATION_ACCOUNT = "GB-270-1017-2-28";
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.Art37Cancellation)
                .transferringAccountIdentifier(1003L)
                .acquiringAccountFullIdentifier(ART_37_CANCELLATION_ACCOUNT)
                .blocks(getBlock(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2, null, "10"))
                .build();

        AccountSummary acquiringAccount = new AccountSummary(1017L, RegistryAccountType.NONE, KyotoAccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT,
                AccountStatus.OPEN, "GB", ART_37_CANCELLATION_ACCOUNT, 2,
                100L, UnitType.AAU);

        AccountSummary transferringAccount = new AccountSummary(1003L, RegistryAccountType.NONE, KyotoAccountType.PARTY_HOLDING_ACCOUNT,
                AccountStatus.OPEN, "GB", "GB-100-1003-2-76", 2,
                100L, UnitType.AAU);

        validDigits(transferringAccount, true);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);
        when(transactionRepository.countByTypeAndTransferringAccountAccountFullIdentifierAndStatusNotIn(any(), any(), eq(TransactionStatus.getFinalStatuses()))).thenReturn(1L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        assertEquals(1006, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }

    @Test
    void testVoluntaryCancellationTransfer_CheckTransferringAccountForPendingTransactionOfSameType() {
        String VOLUNTARY_CANCELLATION_ACCOUNT = "GB-230-1020-2-82";
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.CancellationKyotoUnits)
                .transferringAccountIdentifier(1003L)
                .acquiringAccountFullIdentifier(VOLUNTARY_CANCELLATION_ACCOUNT)
                .blocks(getBlock(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2, null, "10"))
                .build();

        AccountSummary acquiringAccount = new AccountSummary(1020L, RegistryAccountType.NONE, KyotoAccountType.VOLUNTARY_CANCELLATION_ACCOUNT,
                AccountStatus.OPEN, "GB", VOLUNTARY_CANCELLATION_ACCOUNT, 2,
                100L, UnitType.AAU);

        AccountSummary transferringAccount = new AccountSummary(1003L, RegistryAccountType.NONE, KyotoAccountType.PARTY_HOLDING_ACCOUNT,
                AccountStatus.OPEN, "GB", "GB-100-1003-2-76", 2,
                100L, UnitType.AAU);

        validDigits(transferringAccount, true);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);
        when(transactionRepository.countByTypeAndTransferringAccountAccountFullIdentifierAndStatusNotIn(any(), any(), eq(TransactionStatus.getFinalStatuses()))).thenReturn(1L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        assertEquals(1006, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());
    }


    private List<TransactionBlockSummary> getTransactionBlockSummary(UnitType nonKyoto) {
        return Collections
            .singletonList(new TransactionBlockSummary(nonKyoto, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                EnvironmentalActivity.DEFORESTATION, 100L, null,
                Boolean.FALSE, "ProjectX"));
    }

    private TransactionSummary getTransaction(List<TransactionBlockSummary> blocks,
                                              String acquiringAccountFullIdentifier) {
        return TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier(acquiringAccountFullIdentifier)
            .blocks(blocks)
            .build();
    }

    private TransactionSummaryMock getTransaction(
        List<TransactionBlockSummary> blocks,
        TransactionType transactionType,
        KyotoAccountType kyotoAccountType,
        String transferringFullIdentifier,
        String acquiringFullIdentiifer) {
        return TransactionSummaryMock.builder()
            .type(transactionType)
            .transferringAccountType(kyotoAccountType)
            .acquiringAccountType(kyotoAccountType)
            .acquiringAccountFullIdentifier(acquiringFullIdentiifer)
            .transferringAccountFullIdentifier(transferringFullIdentifier)
            .blocks(blocks)
            .build();
    }

    @Test
    void testInboundTransfer() {
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringRegistryCode("JP")
            .transferringAccountIdentifier(12345L)
            .transferringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .transferringAccountFullIdentifier("JP-100-12345-0")
            .acquiringAccountFullIdentifier("GB-100-99999999-2-45")
            .acquiringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .acquiringAccountIdentifier(99999999L)
            .acquiringAccountRegistryCode("GB")
            .blocks(getBlock(UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.DEFORESTATION,
                "10"))
            .build();

        AccountSummary acquiringAccount = getAccount("GB-100-10000002-2-45");
        AccountSummary transferringAccount = getAccount("JP-100-12345-0");

        validDigits(transferringAccount, true);

        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(null);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        // Acquiring account does not exist.
        assertEquals(5902, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        // Transaction inconsistent with Party policy.
        transaction.getBlocks().get(0).setQuantity("0");
        assertEquals(5904, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        // Account has been closed.
        transaction.getBlocks().get(0).setQuantity("10");
        assertNotNull(acquiringAccount);
        acquiringAccount.setAccountStatus(AccountStatus.CLOSED);
        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        assertEquals(5906, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        // Acquiring account is not eligible to receive units.
        acquiringAccount.setAccountStatus(AccountStatus.SUSPENDED);
        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        assertEquals(5903, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

    }

    @Test
    void testReturnExcessAllocation() {
        String identifier = "UK-100-1010-0-47";
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExcessAllocation)
            .transferringAccountIdentifier(1003L)
            .blocks(getBlock(UnitType.ALLOWANCE, CommitmentPeriod.CP0, CommitmentPeriod.CP0, null, "100"))
            .build();

        AccountSummary acquiringAccount = new AccountSummary(1017L, RegistryAccountType.UK_ALLOCATION_ACCOUNT,
            KyotoAccountType.PARTY_HOLDING_ACCOUNT, AccountStatus.OPEN, "UK", identifier,
            2, 100L, UnitType.ALLOWANCE);

        AccountSummary transferringAccount = new AccountSummary(1003L, RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
            KyotoAccountType.PARTY_HOLDING_ACCOUNT, AccountStatus.OPEN, "UK", "UK-100-1003-2-76",
            2, 100L, UnitType.ALLOWANCE);

        validDigits(transferringAccount, true);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        when(allocationCalculationService.getAllocationEntry(any(), any(), any())).thenReturn(Optional.empty());
        assertEquals(3017, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        AllocationSummary allocationSummary = new AllocationSummary(
            2021, 100L, 20L,  AllocationStatusType.ALLOWED, false);
        when(allocationCalculationService.getAllocationEntry(any(), any(), any())).thenReturn(Optional.of(allocationSummary));
        assertEquals(3017, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        allocationSummary.setRemaining(-80L);
        when(allocationCalculationService.getAllocationEntry(any(), any(), any())).thenReturn(Optional.of(allocationSummary));
        assertEquals(3017, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        allocationSummary.setRemaining(-100L);
        when(allocationCalculationService.getAllocationEntry(any(), any(), any())).thenReturn(Optional.of(allocationSummary));
        assertDoesNotThrow(() -> businessCheckService.performChecks(transaction));

        allocationSummary.setRemaining(-120L);
        when(allocationCalculationService.getAllocationEntry(any(), any(), any())).thenReturn(Optional.of(allocationSummary));
        assertDoesNotThrow(() -> businessCheckService.performChecks(transaction));
    }

    @Test
    void testConversionCP1() {
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ConversionCP1)
            .transferringAccountIdentifier(1003L)
            .blocks(getBlock(UnitType.AAU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, null, "100"))
            .build();

        AccountSummary transferringAccount = new AccountSummary(1003L, RegistryAccountType.NONE,
            KyotoAccountType.PARTY_HOLDING_ACCOUNT, AccountStatus.OPEN, "GB", "GB-100-1003-2-76",
            2, 100L, UnitType.ALLOWANCE);

        validDigits(transferringAccount, true);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(transferringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        Assertions.assertDoesNotThrow(() -> businessCheckService.performChecks(transaction));

        validDigits(transferringAccount, false);
        assertEquals(1001, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        validDigits(transferringAccount, true);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(null);
        assertEquals(1002, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);
        transferringAccount.setAccountStatus(AccountStatus.CLOSED);
        assertEquals(1003, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        transferringAccount.setAccountStatus(AccountStatus.ALL_TRANSACTIONS_RESTRICTED);
        assertEquals(1012, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        transferringAccount.setAccountStatus(AccountStatus.TRANSFER_PENDING);
        assertEquals(1011, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

    }

    @Test
    void testConversionCP1B() {
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ConversionCP1)
            .transferringAccountIdentifier(1003L)
            .blocks(getBlock(UnitType.AAU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, null, "100"))
            .build();

        AccountSummary transferringAccount = new AccountSummary(1003L, RegistryAccountType.NONE,
            KyotoAccountType.PARTY_HOLDING_ACCOUNT, AccountStatus.OPEN, "GB", "GB-100-1003-2-76",
            2, 100L, UnitType.ALLOWANCE);

        validDigits(transferringAccount, true);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(transferringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        Assertions.assertDoesNotThrow(() -> businessCheckService.performChecks(transaction));

        transferringAccount.setAccountStatus(AccountStatus.OPEN);
        transferringAccount.setType(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE);
        assertEquals(1007, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        transferringAccount.setType(AccountType.PARTY_HOLDING_ACCOUNT);
        transaction.getBlocks().get(0).setApplicablePeriod(CommitmentPeriod.CP2);
        assertEquals(3001, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        transaction.getBlocks().get(0).setApplicablePeriod(CommitmentPeriod.CP1);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(1L);
        assertEquals(3005, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);
        transaction.getBlocks().get(0).setType(UnitType.CER);
        assertEquals(3006, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        transaction.getBlocks().get(0).setType(UnitType.AAU);
        transaction.getBlocks().get(0).setQuantity("10.5");
        assertEquals(3008, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        transaction.getBlocks().get(0).setQuantity("0");
        assertEquals(3007, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

        transaction.setBlocks(List.of(TransactionBlockSummary.builder()
                .type(UnitType.AAU)
                .originalPeriod(CommitmentPeriod.CP1)
                .applicablePeriod(CommitmentPeriod.CP1)
                .quantity("50")
                .build(),
            TransactionBlockSummary.builder()
                .type(UnitType.RMU)
                .environmentalActivity(EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION)
                .originalPeriod(CommitmentPeriod.CP1)
                .applicablePeriod(CommitmentPeriod.CP1)
                .quantity("20")
                .build()));

        assertEquals(3009, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

    }

    @Test
    void testRetirement() {
        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.Retirement)
            .transferringAccountIdentifier(1009L)
            .blocks(getBlock(UnitType.RMU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION, "100"))
            .build();

        AccountSummary transferringAccount = new AccountSummary(1009L, RegistryAccountType.NONE,
            KyotoAccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT, AccountStatus.OPEN,
            "GB", "GB-100-1009-2-46", 2, 1000L, UnitType.RMU);

        AccountSummary acquiringAccount = new AccountSummary(2000L, RegistryAccountType.NONE,
            KyotoAccountType.RETIREMENT_ACCOUNT, AccountStatus.OPEN, "GB", "GB-300-2000-2-47",
            2, 100L, UnitType.MULTIPLE);

        validDigits(transferringAccount, true);
        when(accountHoldingService.getQuantity(any(), any())).thenReturn(100L);

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(acquiringAccount);
        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(transferringAccount);

        Assertions.assertDoesNotThrow(() -> businessCheckService.performChecks(transaction));

        transferringAccount.setType(AccountType.PARTY_HOLDING_ACCOUNT);
        Assertions.assertDoesNotThrow(() -> businessCheckService.performChecks(transaction));

        transferringAccount.setType(AccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT);
        assertEquals(1007, Assertions.assertThrows(
            BusinessCheckException.class, () -> businessCheckService.performChecks(transaction)).getFirstErrorCode());

    }

}
