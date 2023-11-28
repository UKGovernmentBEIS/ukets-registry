package gov.uk.ets.registry.api.transaction.checks.generic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CheckPendingReconciliationTest {

    @InjectMocks
    CheckPendingReconciliation checkPendingReconciliation;

    @Mock
    ReconciliationRepository reconciliationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testETSTransactionNoPendingReconciliation() {

        Mockito.when(reconciliationRepository.findFirstByStatusIn(any())).thenReturn(Optional.empty());

        Arrays.stream(TransactionType.values())
            .filter(type -> !type.isKyoto())
            .forEach(type -> {
                TransactionSummary transaction = new TransactionSummary();
                transaction.setType(type);
                BusinessCheckContext context = new BusinessCheckContext(transaction);

                checkPendingReconciliation.execute(context);
                assertFalse(context.hasError());
            });
    }

    @Test
    void testETSTransactionPendingReconciliation() {

        Mockito.when(reconciliationRepository.findFirstByStatusIn(any())).thenReturn(Optional.of(new Reconciliation()));

        Arrays.stream(TransactionType.values())
            .filter(type -> !type.isKyoto())
            .forEach(type -> {
                TransactionSummary transaction = new TransactionSummary();
                transaction.setType(type);
                BusinessCheckContext context = new BusinessCheckContext(transaction);

                checkPendingReconciliation.execute(context);
                assertTrue(context.hasError());
            });
    }

    @Test
    void testKyotoTransactionNoPendingReconciliation() {

        Mockito.when(reconciliationRepository.findFirstByStatusIn(any())).thenReturn(Optional.empty());

        Arrays.stream(TransactionType.values())
            .filter(type -> type.isKyoto())
            .forEach(type -> {
                TransactionSummary transaction = new TransactionSummary();
                transaction.setType(type);
                BusinessCheckContext context = new BusinessCheckContext(transaction);

                checkPendingReconciliation.execute(context);
                assertFalse(context.hasError());
            });
    }

    @Test
    void testKyotoTransactionPendingReconciliation() {

        Mockito.when(reconciliationRepository.findFirstByStatusIn(any())).thenReturn(Optional.of(new Reconciliation()));

        Arrays.stream(TransactionType.values())
            .filter(type -> type.isKyoto())
            .forEach(type -> {
                TransactionSummary transaction = new TransactionSummary();
                transaction.setType(type);
                BusinessCheckContext context = new BusinessCheckContext(transaction);

                checkPendingReconciliation.execute(context);
                assertFalse(context.hasError());
            });
    }

}