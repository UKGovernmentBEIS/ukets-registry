package gov.uk.ets.registry.api.transaction.checks.transferring;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.common.GeneratorService;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.*;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.stream.Stream;

class CheckTransferringAccountHasValidCheckDigitsTest {

    @InjectMocks
    CheckTransferringAccountHasValidCheckDigits checkTransferringAccountHasValidCheckDigits;

    @Mock
    TransactionAccountService transactionAccountService;

    @Mock
    GeneratorService generatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Test Business rule 1001")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} ")
    void testBusinessRule(TransactionType transactionType) {
        AccountSummary transferringAccount =
                AccountSummary.parse("GB-100-12345-0-22", RegistryAccountType.NONE, AccountStatus.OPEN);
        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(transactionType);

        Mockito.when(generatorService.validateCheckDigits(transferringAccount.getKyotoAccountType().getCode(),
                transferringAccount.getIdentifier(),
                transferringAccount.getCommitmentPeriod(),
                transferringAccount.getCheckDigits()))
                .thenReturn(new GeneratorService().validateCheckDigits(transferringAccount.getKyotoAccountType().getCode(),
                        transferringAccount.getIdentifier(),
                        transferringAccount.getCommitmentPeriod(),
                        transferringAccount.getCheckDigits()));

        Mockito.when(transactionAccountService.populateTransferringAccount(transactionSummary))
                .thenReturn(transferringAccount);

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkTransferringAccountHasValidCheckDigits.execute(context);
        Assert.assertNotNull(context.getErrors());
        Assert.assertEquals("The transferring account number is invalid with respect to its check digits", context.getErrors().get(0).getMessage());
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(TransactionType.values()).map(Arguments::of);
    }
}
