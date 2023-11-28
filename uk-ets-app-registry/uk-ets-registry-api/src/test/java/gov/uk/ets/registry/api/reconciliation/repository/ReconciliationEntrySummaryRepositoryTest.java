package gov.uk.ets.registry.api.reconciliation.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.helper.persistence.AccountHoldingsTestHelper;
import gov.uk.ets.registry.api.helper.persistence.AccountHoldingsTestHelper.ExpectedUnitBlockData;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationEntrySummary;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = { "spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"})
class ReconciliationEntrySummaryRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    AccountHoldingsTestHelper accountHoldingsTestHelper;

    ReconciliationEntrySummaryRepository repository;

    List<ReconciliationEntrySummary> results;

    @BeforeEach
    void setUp() {
        accountHoldingsTestHelper = new AccountHoldingsTestHelper(entityManager);
        repository = new ReconciliationEntrySummaryRepository(entityManager);
    }

    @Test
    public void fetch() {
        // given
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(1000L)
            .startSerialNumber(100L)
            .endSerialNumber(119L)
            .unitType(UnitType.ALLOWANCE)
            .build());
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(1000L)
            .startSerialNumber(120L)
            .endSerialNumber(149L)
            .unitType(UnitType.ALLOWANCE)
            .build());

        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(1000L)
            .startSerialNumber(100L)
            .endSerialNumber(114L)
            .unitType(UnitType.AAU)
            .build());
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(1000L)
            .startSerialNumber(200L)
            .endSerialNumber(214L)
            .unitType(UnitType.AAU)
            .build());

        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(1001L)
            .startSerialNumber(120L)
            .endSerialNumber(149L)
            .unitType(UnitType.ALLOWANCE)
            .build());
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(1001L)
            .startSerialNumber(220L)
            .endSerialNumber(249L)
            .unitType(UnitType.ALLOWANCE)
            .build());

        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(1001L)
            .startSerialNumber(100L)
            .endSerialNumber(119L)
            .unitType(UnitType.AAU)
            .build());
        accountHoldingsTestHelper.createUnitBlock(ExpectedUnitBlockData.builder()
            .accountIdentifier(1001L)
            .startSerialNumber(200L)
            .endSerialNumber(219L)
            .unitType(UnitType.AAU)
            .build());

        // when
        results = repository.fetch(Set.of(1000L, 1001L));

        // then
        assertEquals(4, results.size());
        assertEquals(50L, getTotal(1000L, UnitType.ALLOWANCE));
        assertEquals(30L, getTotal(1000L, UnitType.AAU));
        assertEquals(60L, getTotal(1001L, UnitType.ALLOWANCE));
        assertEquals(40L, getTotal(1001L, UnitType.AAU));
    }

    private Long getTotal(Long accountIdentifier, UnitType unitType) {
        return results.stream()
            .filter(b -> b.getAccountIdentifier().equals(accountIdentifier) && b.getUnitType().equals(unitType))
            .findAny().get().getTotal();
    }
}