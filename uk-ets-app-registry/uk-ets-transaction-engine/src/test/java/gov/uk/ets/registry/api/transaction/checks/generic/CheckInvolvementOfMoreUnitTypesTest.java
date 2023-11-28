package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.service.LevelService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class CheckInvolvementOfMoreUnitTypesTest {

    @InjectMocks
    CheckInvolvementOfMoreUnitTypes checkInvolvementOfMoreUnitTypes;

    @Mock
    LevelService levelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Test Business rule 3009")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1} ")
    void testCheckQuantityOfAAUToRetire(TransactionType transactionType, String errorMessage) {
        Mockito.when(levelService.getRemainingValue(RegistryLevelType.AAU_TO_RETIRE, UnitType.AAU, CommitmentPeriod.CP2))
                .thenReturn(1000L);

        List<TransactionBlockSummary> blocks = new ArrayList<>();
        TransactionBlockSummary block1 = new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, 500L, "200", true, null);
        blocks.add(block1);
        TransactionBlockSummary block2 = new TransactionBlockSummary(UnitType.RMU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, 500L, "200", true, null);
        blocks.add(block2);

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(transactionType);
        transaction.setBlocks(blocks);
        BusinessCheckContext context = new BusinessCheckContext(transaction);
        checkInvolvementOfMoreUnitTypes.execute(context);
        Assert.assertNotNull(context.getErrors());
        Assert.assertEquals(errorMessage, context.getErrors().get(0).getMessage());
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(TransactionType.IssueAllowances, "A transaction must not issue more than one unit type"),
                Arguments.of(TransactionType.TransferToSOPForConversionOfERU, "A transaction must not involve more than one unit type"),
                Arguments.of(TransactionType.IssueOfAAUsAndRMUs, "A transaction must not issue more than one unit type"),
                Arguments.of(TransactionType.ConversionCP1, "A transaction must not involve more than one unit type"),
                Arguments.of(TransactionType.ConversionA, "A transaction must not involve more than one unit type"),
                Arguments.of(TransactionType.ConversionB, "A transaction must not involve more than one unit type")
        );
    }
}
