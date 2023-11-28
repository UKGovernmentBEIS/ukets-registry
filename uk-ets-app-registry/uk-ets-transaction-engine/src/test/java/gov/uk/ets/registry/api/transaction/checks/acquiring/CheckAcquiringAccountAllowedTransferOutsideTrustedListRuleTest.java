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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

class CheckAcquiringAccountAllowedTransferOutsideTrustedListRuleTest {

    @InjectMocks
    private CheckAcquiringAccountAllowedTransferOutsideTrustedListRule rule;

    @Mock
    TransactionAccountService transactionAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1} - {2}")
    @DisplayName("Test Rule: Transfers to accounts not on the trusted account list are not allowed")
    void testRuleForTransfersOutsideTAL(boolean transferOutsideTALAllowed, boolean belongsToTAL, boolean showError) {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
                new TransactionBlockSummary(UnitType.ALLOWANCE, CommitmentPeriod.CP0, CommitmentPeriod.CP0,
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
                AccountSummary.parse("UK-100-10000064-2-26", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.transfersToAccountsNotOnTheTrustedListAreAllowed(any()))
                .thenReturn(transferOutsideTALAllowed);
        Mockito.when(transactionAccountService.belongsToTrustedList(any(), any())).thenReturn(belongsToTAL);

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        rule.execute(context);

        if (showError) {
            Assertions.assertTrue(context.hasError());
        } else {
            Assertions.assertFalse(context.hasError());
        }
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(false, false, true),
                Arguments.of(false, true, false),
                Arguments.of(true, false, false),
                Arguments.of(true, true, false)
        );
    }
}
