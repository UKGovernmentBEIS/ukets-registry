package gov.uk.ets.registry.api.transaction.checks.acquiring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.*;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class CheckAcquiringAccountHasNotTransferPendingStatusTest {

    @InjectMocks
    private CheckAcquiringAccountHasNotTransferPendingStatus rule;

    @Mock
    TransactionAccountService transactionAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Rule: Transactions to acquiring accounts with status TRANSFER_PENDING are not allowed")
    void test_execute() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
                new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                        null, null, "10", null, null));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.ExternalTransfer)
                .transferringAccountIdentifier(10000062L)
                .acquiringAccountIdentifier(10000064L)
                .blocks(blocks)
                .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("UK-100-10000062-2-36", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse("UK-100-10000064-2-26", RegistryAccountType.NONE, AccountStatus.TRANSFER_PENDING)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        rule.execute(context);

        Assertions.assertTrue(context.hasError());
    }
}
