package gov.uk.ets.registry.api.transaction.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import gov.uk.ets.registry.api.helper.persistence.AccountHoldingsTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountHoldingsTestHelper.ExpectedUnitBlockData;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"
})
class UnitBlockRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UnitBlockRepository unitBlockRepository;

    private Long accountIdentifier;

    @BeforeEach
    public void setUp() {
        accountIdentifier = 55L;
        AccountHoldingsTestHelper accountHoldingsTestHelper = new AccountHoldingsTestHelper(entityManager);
        accountHoldingsTestHelper.setUp(accountIdentifier);
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
                .accountIdentifier(accountIdentifier)
                .applicablePeriod(CommitmentPeriod.CP2)
                .originalPeriod(CommitmentPeriod.CP2)
                .originatingRegistryCode("BR")
                .unitType(UnitType.TCER)
                .subjectToSop(true)
                .startSerialNumber(10000072447L)
                .endSerialNumber(10000072644L)
                .build());
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
                .accountIdentifier(accountIdentifier)
                .applicablePeriod(CommitmentPeriod.CP2)
                .originalPeriod(CommitmentPeriod.CP1)
                .unitType(UnitType.AAU)
                .subjectToSop(true)
                .startSerialNumber(120000L)
                .endSerialNumber(128900L)
                .build());
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
                .accountIdentifier(accountIdentifier)
                .applicablePeriod(CommitmentPeriod.CP2)
                .originalPeriod(CommitmentPeriod.CP1)
                .unitType(UnitType.AAU)
                .subjectToSop(true)
                .startSerialNumber(1099L)
                .endSerialNumber(2123L)
                .build());

        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
               .accountIdentifier(accountIdentifier)
               .applicablePeriod(CommitmentPeriod.CP0)
               .originalPeriod(CommitmentPeriod.CP0)
               .originatingRegistryCode("GB")
               .unitType(UnitType.ALLOWANCE)
               .subjectToSop(false)
               .startSerialNumber(1000991L)
               .endSerialNumber(1001000L)
               .build());
    }

    @DisplayName("Account Holdings Query executes findByAccountIdentifier with success.")
    @Test
    void test_findByAccountIdentifier() {
        List<UnitBlock> result = unitBlockRepository.findByAccountIdentifier(accountIdentifier);
        assertFalse(result.isEmpty());
        assertEquals(4, result.size());
    }

    @DisplayName("Account Holdings Query executes findBySerialBlockRange with success.")
    @Test
    void test_findSerialBlockRange() {
        List<UnitBlock> result = unitBlockRepository.findBySerialBlockRange(10000072447L, 10000072644L,"BR", List.of(UnitType.TCER, UnitType.LCER));
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @DisplayName("Balance transfer query executes calculateAvailableQuantity with success.")
    @Test
    void test_calculateTheAvailableQuantity() {
        Long quantity = unitBlockRepository.calculateAvailableQuantity(accountIdentifier, UnitType.ALLOWANCE);
        assertEquals(10L, quantity);
    }
}
