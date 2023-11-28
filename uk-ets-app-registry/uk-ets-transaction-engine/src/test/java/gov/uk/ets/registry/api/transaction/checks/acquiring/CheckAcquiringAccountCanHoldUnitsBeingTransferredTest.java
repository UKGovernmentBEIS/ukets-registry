package gov.uk.ets.registry.api.transaction.checks.acquiring;

import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CheckAcquiringAccountCanHoldUnitsBeingTransferredTest {

    @InjectMocks
    private CheckAcquiringAccountCanHoldUnitsBeingTransferred checkAcquiringAccountCanHoldUnits;

    @Mock
    TransactionAccountService transactionAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void execute() {
        List<TransactionBlockSummary> blocks = Arrays.asList(
            new TransactionBlockSummary(UnitType.CER, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null,"5",
                Boolean.FALSE, "JP12345"));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("JP-100-12345-0")
            .blocks(blocks)
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("JP-100-12345-0", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountCanHoldUnits.execute(context);

        Assertions.assertFalse(context.hasError());

    }

    @Test
    void execute2() {
        List<TransactionBlockSummary> blocks = Arrays.asList(
            new TransactionBlockSummary(UnitType.CER, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null,"5",
                Boolean.FALSE, "JP12345"));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.InternalTransfer)
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("JP-100-12345-0")
            .blocks(blocks)
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-10000047-2-14", RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountCanHoldUnits.execute(context);
        Assertions.assertTrue(context.hasError());


    }
}