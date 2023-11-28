package gov.uk.ets.registry.api.transaction.repository;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.helper.persistence.AccountHoldingsTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountHoldingsTestHelper.ExpectedUnitBlockData;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"})
@Import(AccountHoldingsTestHelper.class)
public class AccountHoldingRepositoryTest {
    @Autowired
    private AccountHoldingRepository repository;

    private Long accountIdentifier;
    private String projectId;

    @Autowired
    private AccountHoldingsTestHelper accountHoldingsTestHelper;

    private List<UnitBlockData> availableBlocks;
    private List<UnitBlockData> reservedBlocks;

    @BeforeEach
    public void given() {
        accountIdentifier = 10876L;
        projectId = "BR32";
        availableBlocks = List
            .of(UnitBlockData.builder()
                    .serialNumberRange(new Long[] {1L, 9L})
                    .reserved(false)
                    .unitType(UnitType.ALLOWANCE)
                    .build(),
                UnitBlockData.builder()
                    .serialNumberRange(new Long[] {21L, 27L})
                    .reserved(false)
                    .unitType(UnitType.AAU)
                    .build(),
                UnitBlockData.builder()
                    .serialNumberRange(new Long[] {21L, 27L})
                    .reserved(false)
                    .projectId(projectId)
                    .unitType(UnitType.LCER)
                    .build()
            );
        reservedBlocks = List
            .of(UnitBlockData.builder()
                    .serialNumberRange(new Long[] {51L, 59L})
                    .reserved(true)
                    .unitType(UnitType.CER)
                    .build(),
                UnitBlockData.builder()
                    .serialNumberRange(new Long[] {221L, 227L})
                    .reserved(true)
                    .unitType(UnitType.LCER)
                    .build(),
                UnitBlockData.builder()
	                .serialNumberRange(new Long[] {21L, 27L})
	                .reserved(true)
	                .projectId(projectId)
	                .unitType(UnitType.LCER)
	                .build()
            );
        availableBlocks.forEach(blockData -> saveUnitBlock(blockData));
        reservedBlocks.forEach(blockData -> saveUnitBlock(blockData));
    }

    @Test
    void getAccountUnitTypes() {
        // given
        List<UnitType> expectedUnitTypes = availableBlocks.stream().map(b -> b.unitType).distinct().collect(Collectors.toList());

        // when
        List<UnitType> unitTypes = repository.getAccountUnitTypes(accountIdentifier);

        // then
        assertEquals(expectedUnitTypes.size(), unitTypes.size());
        assertTrue(unitTypes.containsAll(expectedUnitTypes));
    }

    @Test
    void getAccountBalance() {
        // given
        Long expectedBalance = availableBlocks.stream().map(b -> b.serialNumberRange[1] - b.serialNumberRange[0] + 1).reduce(0L, Long::sum) +
                reservedBlocks.stream().map(b -> b.serialNumberRange[1] - b.serialNumberRange[0] + 1).reduce(0L, Long::sum);

        // when
        Long balance = repository.getAccountBalance(accountIdentifier);

        // then
        assertEquals(expectedBalance, balance);
    }

    
    @Test
    void getHoldingsToBeReplacedForTransactionReplacement() {
        // given
        List<UnitType> expectedUnitTypes = availableBlocks.stream().filter(t-> projectId.equals(t.projectId)).map(b -> b.unitType).distinct().collect(Collectors.toList());

        // when
        List<TransactionBlockSummary> unitTypes = repository.getHoldingsToBeReplacedForTransactionReplacement(accountIdentifier,"BR32");

        // then
        assertFalse(unitTypes.containsAll(expectedUnitTypes));
    }
    
    private void saveUnitBlock(UnitBlockData data) {
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(accountIdentifier)
            .startSerialNumber(data.serialNumberRange[0])
            .endSerialNumber(data.serialNumberRange[1])
            .unitType(data.unitType)
            .reservedForTransaction(data.reserved ? UUID.randomUUID().toString() : null)
            .subjectToSop(true)
            .originalPeriod(CommitmentPeriod.CP2)
            .applicablePeriod(CommitmentPeriod.CP2)
            .build());
    }

    @Builder
    private static class UnitBlockData {
        private Long[] serialNumberRange;
        private UnitType unitType;
        private String projectId;
        private boolean reserved;
    }
}
