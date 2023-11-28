package gov.uk.ets.registry.api.transaction.checks.transferring;

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

class CheckTransferringAccountHasNotSomeTransactionsRestrictedStatusTest {

    @InjectMocks
    private CheckTransferringAccountHasNotSomeTransactionsRestrictedStatus rule;

    @Mock
    TransactionAccountService transactionAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test Rule: Transactions from accounts with status ALL_TRANSACTIONS_RESTRICTED are not allowed")
    void test_execute() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
                new TransactionBlockSummary(UnitType.ALLOWANCE, CommitmentPeriod.CP0, CommitmentPeriod.CP0,
                        null, null, "10", null, null));

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.SurrenderAllowances)
                .transferringAccountIdentifier(10000062L)
                .acquiringAccountIdentifier(10000064L)
                .blocks(blocks)
                .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("UK-100-10000062-2-36", RegistryAccountType.NONE, AccountStatus.ALL_TRANSACTIONS_RESTRICTED)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse("UK-100-10000025-0-33", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        rule.execute(context);

        Assertions.assertFalse(context.hasError());
    }
}
