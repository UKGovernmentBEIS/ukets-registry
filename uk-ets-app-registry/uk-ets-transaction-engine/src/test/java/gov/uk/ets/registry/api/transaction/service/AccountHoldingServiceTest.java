package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.AccountHoldingRepository;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountHoldingServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private AccountHoldingRepository accountHoldingRepository;

    private AccountHoldingService accountHoldingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        accountHoldingService = new AccountHoldingService(entityManager, accountHoldingRepository);
    }

    @Test
    void getQuantity() {



        TransactionBlockSummary block = TransactionBlockSummary.builder()
            .type(UnitType.AAU)
            .originalPeriod(CommitmentPeriod.CP1)
            .applicablePeriod(CommitmentPeriod.CP1)
            .subjectToSop(true)
            .environmentalActivity(EnvironmentalActivity.DEFORESTATION)
            .projectNumber("JP12345")
            .build();

        Assertions.assertThrows(NullPointerException.class, () -> {
            accountHoldingService.getQuantity(1L, block);
        });

        TransactionBlockSummary block2 = new TransactionBlockSummary();
        Assertions.assertThrows(NullPointerException.class, () -> {
            accountHoldingService.getQuantity(1L, block2);
        });


    }
}