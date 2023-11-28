package gov.uk.ets.registry.api.transaction.checks.acquiring;

import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CheckAcquiringAccountForCentralTransfersFromNERTest {

    @InjectMocks
    private CheckAcquiringAccountForCentralTransfersFromNER checkAcquiringAccountForCentralTransfersFromNER;

    @Mock
    TransactionAccountService transactionAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Test for allowed acquiring accounts: Total Quantity Account")
    void testForAcquiringTotalQuantityAccount() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, null, null,
                null, null, "10", null, null));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringAccountIdentifier(1013L)
            .acquiringAccountIdentifier(1010L)
            .blocks(blocks)
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1013-0-32", RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1010-0-47", RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountForCentralTransfersFromNER.execute(context);

        Assertions.assertFalse(context.hasError());

    }

    @Test
    @DisplayName("Test for allowed acquiring accounts: Allocation Account")
    void testForAcquiringAllocationAccount() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, null, null,
                null, null, "10", null, null));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringAccountIdentifier(1013L)
            .acquiringAccountIdentifier(1012L)
            .blocks(blocks)
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1013-0-32", RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1012-0-37", RegistryAccountType.UK_ALLOCATION_ACCOUNT, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountForCentralTransfersFromNER.execute(context);

        Assertions.assertFalse(context.hasError());

    }

    @Test
    @DisplayName("Test for not allowed acquiring accounts: General Holding Account")
    void testForAcquiringGeneralHoldingAccount() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, null, null,
                null, null, "10", null, null));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringAccountIdentifier(1013L)
            .acquiringAccountIdentifier(1015L)
            .blocks(blocks)
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1013-0-32", RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1015-0-22", RegistryAccountType.UK_GENERAL_HOLDING_ACCOUNT, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountForCentralTransfersFromNER.execute(context);

        Assertions.assertTrue(context.hasError());
    }

    @Test
    @DisplayName("Test for not allowed acquiring accounts: Auction Account")
    void testForAcquiringAuctionAccount() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, null, null,
                null, null, "10", null, null));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringAccountIdentifier(1013L)
            .acquiringAccountIdentifier(1011L)
            .blocks(blocks)
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1013-0-32", RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1011-0-42", RegistryAccountType.UK_AUCTION_ACCOUNT, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountForCentralTransfersFromNER.execute(context);

        Assertions.assertTrue(context.hasError());
    }

    @Test
    @DisplayName("Test for not allowed acquiring accounts: Market Stability Reserve Account")
    void testForAcquiringMarketStabilityReserveAccount() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, null, null,
                null, null, "10", null, null));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringAccountIdentifier(1013L)
            .acquiringAccountIdentifier(1014L)
            .blocks(blocks)
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1013-0-32", RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1014-0-27", RegistryAccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountForCentralTransfersFromNER.execute(context);

        Assertions.assertTrue(context.hasError());
    }
}
