package gov.uk.ets.registry.api.transaction.checks.acquiring;

import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class CheckAcquiringAccountInequalityTest {

    @InjectMocks
    private CheckAcquiringAccountInequality checkAcquiringAccountInequality;

    @Mock
    TransactionAccountService transactionAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void transfer_to_different_account_is_allowed() {

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.InternalTransfer)
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("UK-100-10000002-2-99")
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-10000002-5-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountInequality.execute(context);

        Assertions.assertFalse(context.hasError());
    }

    @Test
    void transfer_to_same_account_is_not_allowed() {

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.InternalTransfer)
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("UK-100-10000002-2-99")
            .build();

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        BusinessCheckContext context = new BusinessCheckContext(transactionSummary);
        checkAcquiringAccountInequality.execute(context);

        Assertions.assertTrue(context.hasError());
    }
}
