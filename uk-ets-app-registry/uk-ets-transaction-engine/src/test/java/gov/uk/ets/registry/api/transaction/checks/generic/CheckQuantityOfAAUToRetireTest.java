package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.*;
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

class CheckQuantityOfAAUToRetireTest {

    @InjectMocks
    CheckQuantityOfAAUToRetire checkQuantityOfAAUToRetire;

    @Mock
    LevelService levelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("Test Business rule 3012")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1} ")
    void testCheckQuantityOfAAUToRetire(long quantityOfAAUToRetire, boolean hasError) {
        Mockito.when(levelService.getRemainingValue(RegistryLevelType.AAU_TO_RETIRE, UnitType.AAU, CommitmentPeriod.CP2))
                .thenReturn(quantityOfAAUToRetire);

        List<TransactionBlockSummary> blocks = new ArrayList<>();
        TransactionBlockSummary block = new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, 500L, "200", true, null);
        blocks.add(block);

        TransactionSummary transaction = new TransactionSummary();
        transaction.setType(TransactionType.Retirement);
        transaction.setBlocks(blocks);
        BusinessCheckContext context = new BusinessCheckContext(transaction);
        checkQuantityOfAAUToRetire.execute(context);
        if (hasError) {
            Assert.assertNotNull(context.getErrors());
        } else {
            Assert.assertNull(context.getErrors());
        }
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(1000L, false),
                Arguments.of(200L, false),
                Arguments.of(100L, true)
        );
    }
}
