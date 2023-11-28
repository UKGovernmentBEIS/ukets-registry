package gov.uk.ets.registry.api.allocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.uk.ets.registry.api.allocation.AccountSampleEntity;
import gov.uk.ets.registry.api.allocation.AllocationsTestApplication;
import gov.uk.ets.registry.api.allocation.CompliantEntity;
import gov.uk.ets.registry.api.allocation.ParentAllocationBase;
import gov.uk.ets.registry.api.allocation.data.AllocationClassificationSummary;
import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.domain.AllocationEntry;
import gov.uk.ets.registry.api.allocation.domain.AllocationPeriod;
import gov.uk.ets.registry.api.allocation.domain.AllocationPhase;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationPeriodRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationPhaseRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.allocation.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.allocation.repository.ExcludeEmissionsEntryRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ContextConfiguration(classes = AllocationsTestApplication.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
//    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"
})
class AllocationCalculationServiceTest extends ParentAllocationBase {

    @Autowired
    private AllocationEntryRepository allocationEntryRepository;

    @Autowired
    private AllocationPhaseRepository allocationPhaseRepository;

    @Autowired
    private AllocationPeriodRepository allocationPeriodRepository;

    @Autowired
    private AllocationStatusRepository allocationStatusRepository;

    @Autowired
    private AllocationYearRepository allocationYearRepository;
    
    @Autowired
    private ExcludeEmissionsEntryRepository excludeEmissionsEntryRepository;

    @Autowired
    private CompliantEntityRepository compliantEntityRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private AllocationCalculationService allocationCalculationService;

    @BeforeEach
    @Transactional
    void setUp() {
        allocationCalculationService = new AllocationCalculationService(allocationEntryRepository, allocationYearRepository);

        AllocationPhase phase = new AllocationPhase();
        phase.setCode(1);
        phase.setInitialPhaseCap(1000000L);
        phase.setConsumedPhaseCap(0L);
        phase.setPendingPhaseCap(0L);
        allocationPhaseRepository.save(phase);

        AllocationPeriod firstPeriod = new AllocationPeriod();
        firstPeriod.setCode(1);
        firstPeriod.setPhase(phase);
        allocationPeriodRepository.save(firstPeriod);

        for (int index = 2021; index <= 2025 ; index++) {
            allocationYearRepository.save(createYear(firstPeriod, index));
        }

        AllocationPeriod secondPeriod = new AllocationPeriod();
        secondPeriod.setCode(2);
        secondPeriod.setPhase(phase);
        allocationPeriodRepository.save(secondPeriod);

        for (int index = 2026; index <= 2030 ; index++) {
            allocationYearRepository.save(createYear(secondPeriod, index));
        }
    }

    @Test
    @Transactional
    @Disabled
    void testCalculateAllocationOverview() {
        allocationEntryRepository.save(createAllocationEntry(100001L, 2021, 100L, 50L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100002L, 2021, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100003L, 2021, 100L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100004L, 2021, 500L, 200L, AllocationType.NAT));

        allocationEntryRepository.save(createAllocationEntry(100005L, 2021, 100L, 50L, AllocationType.NAVAT));
        allocationEntryRepository.save(createAllocationEntry(100006L, 2021, 200L, 0L, AllocationType.NAVAT));
        allocationEntryRepository.save(createAllocationEntry(100007L, 2021, 300L, 200L, AllocationType.NAVAT));
        allocationEntryRepository.save(createAllocationEntry(100008L, 2021, 400L, 1200L, AllocationType.NAVAT));

        allocationEntryRepository.save(createAllocationEntry(100001L, 2021, 10L, 5L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100002L, 2021, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100003L, 2021, 500L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(1000010L, 2021, 400L, 100L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100009L, 2021, 10000L, 100L, AllocationType.NER));

        allocationStatusRepository.save(createAllocationStatus(100001L, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(100002L, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(100003L, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(100004L, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(100005L, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(100006L, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(100007L, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(100008L, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(100009L, 2021, AllocationStatusType.WITHHELD));
        allocationStatusRepository.save(createAllocationStatus(1000010L, 2021, AllocationStatusType.ALLOWED));

        AllocationOverview overview = allocationCalculationService.calculateAllocationsOverview(2021, AllocationCategory.INSTALLATION);
        assertNotNull(overview);
        assertNotNull(overview.getTotal());
        assertNotNull(overview.getInstallations());
        assertNotNull(overview.getAircraftOperators());
        assertNotNull(overview.getInstallationsNewEntrants());

        assertEquals(2021, overview.getYear());
        assertEquals(1605, overview.getTotalQuantity());
        assertEquals(10, overview.getTotal().getAccounts());
        assertNotNull(overview.getBeneficiaryRecipients());

        overview.getBeneficiaryRecipients().stream().forEach(entry -> {
            assertNotNull(entry.getEntitlement());
            assertNotNull(entry.getAllocated());
            assertNotNull(entry.getRemaining());
            assertNotNull(entry.getType());
            assertNotNull(entry.getStatus());
            assertNotNull(entry.getYear());
        });

    }

    /**
     * NAT no records & NER no records => Fully
     */
    @Test
    void testCalculateAllocationClassification_NerAndNatNoRecords_result_Fully() {
        int currentAllocationYear = 2026;

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(0, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertNull(summary.getAllocationClassification());
    }

    /**
     * NAT Under & NER Under => Under
     */
    @Test
    void testCalculateAllocationClassification_NerUnderNatUnder_result_under() {
        int currentAllocationYear = 2026;
        /* NAT under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 80L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 180L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1160, summary.getAllocated());
        assertEquals(140, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Under & NER Over => Over (because NAT Under < NER Over)
     */
    @Test
    void testCalculateAllocationClassification_NerOverNatUnder_result_over() {
        int currentAllocationYear = 2026;
        /* NAT under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 10L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 480L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1390, summary.getAllocated());
        assertEquals(-90, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.OVER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Under & NER Over => Under (because NAT Under > NER Over)
     */
    @Test
    void testCalculateAllocationClassification_NerOverNatUnder_result_under() {
        int currentAllocationYear = 2026;
        /* NAT under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 60L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 320L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1280, summary.getAllocated());
        assertEquals(20, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Under & NER Over => Fully (because NAT Under = NER Over)
     */
    @Test
    void testCalculateAllocationClassification_NerOverNatUnder_result_fully() {
        int currentAllocationYear = 2026;
        /* NAT under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 60L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 340L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1300, summary.getAllocated());
        assertEquals(0, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.FULLY_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Under & NER Not => Under
     */
    @Test
    void testCalculateAllocationClassification_NatUnderAndNotNer_result_under() {
        int currentAllocationYear = 2026;
        /* NAT under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 60L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(4, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(500, summary.getEntitlement());
        assertEquals(460, summary.getAllocated());
        assertEquals(40, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Under & NER Fully => Under
     */
    @Test
    void testCalculateAllocationClassification_NatUnderAndNerFully_result_under() {
        int currentAllocationYear = 2026;
        /* NAT under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 60L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER fully allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 300L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1260, summary.getAllocated());
        assertEquals(40, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Over & NER Under => Over (because NAT over > NER under)
     */
    @Test
    void testCalculateAllocationClassification_NerUnderNatOver_result_over() {
        int currentAllocationYear = 2026;
        /* NAT over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 250L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 180L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1330, summary.getAllocated());
        assertEquals(-30, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.OVER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Over & NER Under => Under (because NAT over < NER under)
     */
    @Test
    void testCalculateAllocationClassification_NerUnderNatOver_result_under() {
        int currentAllocationYear = 2026;
        /* NAT over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 120L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 180L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1200, summary.getAllocated());
        assertEquals(100, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Over & NER Under => Fully (because NAT over = NER under)
     */
    @Test
    void testCalculateAllocationClassification_NerUnderNatOver_result_fully() {
        int currentAllocationYear = 2026;
        /* NAT over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 120L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 280L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1300, summary.getAllocated());
        assertEquals(0, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.FULLY_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Over & NER Over => Over
     */
    @Test
    void testCalculateAllocationClassification_NerOverNatOver_result_over() {
        int currentAllocationYear = 2026;
        /* NAT over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 120L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 340L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1360, summary.getAllocated());
        assertEquals(-60, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.OVER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Over & NER Not => Over (because NAT over > NER entitlement)
     */
    @Test
    void testCalculateAllocationClassification_NerNotNatOver_result_over() {
        int currentAllocationYear = 2026;
        /* NAT over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 920L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1320, summary.getAllocated());
        assertEquals(-20, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.OVER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Over & NER Not => Under (because NAT over < NER entitlement)
     */
    @Test
    void testCalculateAllocationClassification_NerNotNatOver_result_under() {
        int currentAllocationYear = 2026;
        /* NAT over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 120L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(520, summary.getAllocated());
        assertEquals(780, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Over & NER Not => Fully (because NAT over = NER entitlement)
     */
    @Test
    void testCalculateAllocationClassification_NerNotNatOver_result_fully() {
        int currentAllocationYear = 2026;
        /* NAT over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 900L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1300, summary.getAllocated());
        assertEquals(0, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.FULLY_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Over & NER Fully => Over
     */
    @Test
    void testCalculateAllocationClassification_NerFullyNatOver_result_over() {
        int currentAllocationYear = 2026;
        /* NAT over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 120L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER fully allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 300L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1320, summary.getAllocated());
        assertEquals(-20, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.OVER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Not & NER Under => Under
     */
    @Test
    void testCalculateAllocationClassification_NerUnderNatNot_result_Under() {
        int currentAllocationYear = 2026;
        /* NAT not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, null, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, null, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, null, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, null, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 280L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(780, summary.getAllocated());
        assertEquals(520, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Not & NER Over => Over (because NER over > NAT entitlement)
     */
    @Test
    void testCalculateAllocationClassification_NerOverNatNot_result_Over() {
        int currentAllocationYear = 2026;
        /* NAT not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 1340L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1840, summary.getAllocated());
        assertEquals(-540, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.OVER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Not & NER Over => Under (because NER over < NAT entitlement)
     */
    @Test
    void testCalculateAllocationClassification_NerOverNatNot_result_Under() {
        int currentAllocationYear = 2026;
        /* NAT not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 340L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(840, summary.getAllocated());
        assertEquals(460, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Not & NER Over => Fully (because NER over = NAT entitlement)
     */
    @Test
    void testCalculateAllocationClassification_NerOverNatNot_result_Fully() {
        int currentAllocationYear = 2026;
        /* NAT not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 800L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1300, summary.getAllocated());
        assertEquals(0, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.FULLY_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Not & NER Not => Not
     */
    @Test
    void testCalculateAllocationClassification_NerNotNatNot_result_Not() {
        int currentAllocationYear = 2026;
        /* NAT not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(0, summary.getAllocated());
        assertEquals(1300, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.NOT_YET_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Not & NER Fully => Under
     */
    @Test
    void testCalculateAllocationClassification_NerFullyNatNot_result_Under() {
        int currentAllocationYear = 2026;
        /* NAT not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 0L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER fully allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 300L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(800, summary.getAllocated());
        assertEquals(500, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Fully & NER Under => Under
     */
    @Test
    void testCalculateAllocationClassification_NerUnderNatFully_result_Under() {
        int currentAllocationYear = 2026;
        /* NAT fully allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 100L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER under allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 280L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1280, summary.getAllocated());
        assertEquals(20, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Fully & NER Over => Over
     */
    @Test
    void testCalculateAllocationClassification_NerOverNatFully_result_Over() {
        int currentAllocationYear = 2026;
        /* NAT fully allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 100L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER over allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 340L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1340, summary.getAllocated());
        assertEquals(-40, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.OVER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Fully & NER Not => Under
     */
    @Test
    void testCalculateAllocationClassification_NerNotNatFully_result_Under() {
        int currentAllocationYear = 2026;
        /* NAT fully allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 100L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER not allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 0L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(500, summary.getAllocated());
        assertEquals(800, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.UNDER_ALLOCATED.name(), summary.getAllocationClassification());
    }

    /**
     * NAT Fully & NER Fully => Fully
     */
    @Test
    void testCalculateAllocationClassification_NerFullyNatFully_result_Fully() {
        int currentAllocationYear = 2026;
        /* NAT fully allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 100L, 120L, AllocationType.NAT, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NAT, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 100L, 100L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 100L, null, AllocationType.NAT));

        /* NER fully allocated */
        allocationEntryRepository.save(createAllocationEntry(100001L, 2023, 200L, 220L, AllocationType.NER, 20L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2024, 100L, 110L, AllocationType.NER, 10L, 0L));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2025, 200L, 200L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2026, 300L, 300L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(100001L, 2027, 300L, null, AllocationType.NER));

        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(currentAllocationYear, 100001L);
        assertEquals(8, allocationEntryRepository.calculateAllocationClassification(currentAllocationYear, 100001L).size());
        assertEquals(1300, summary.getEntitlement());
        assertEquals(1300, summary.getAllocated());
        assertEquals(0, summary.getRemainingQuantity());
        assertEquals(AllocationClassification.FULLY_ALLOCATED.name(), summary.getAllocationClassification());
    }
    
    /*
     * Test excluded without allocation
     */
    @Test
    void testCalculateAllocationClassificationFullyAllocated_UKETS_6971() {
        Integer allocationYear = 2022;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        excludeEmissionsEntryRepository.save(createExcludeEmissionsEntry(complaintEntityIdentifier, 2021L , true));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 10L, null, AllocationType.NAVAT));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 100L,100L, AllocationType.NAVAT));
        
        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(allocationYear,ce.getId());
        assertEquals(2, allocationEntryRepository.calculateAllocationClassification(allocationYear, ce.getId()).size());
        assertEquals(100, summary.getEntitlement());
        assertEquals(100, summary.getAllocated());
        assertEquals(0, summary.getRemainingQuantity());
        assertNotNull(summary.getAllocationClassification(), "Should result in non-nullable Allocation Classification");
        assertEquals(AllocationClassification.FULLY_ALLOCATED.name(), summary.getAllocationClassification());
    }
    
    /*
     * Test excluded with allocation , reversed and returned
     */
    @Test
    void testCalculateAllocationClassificationFullyAllocatedReversedReturned_UKETS_6971() {
        Integer allocationYear = 2022;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        excludeEmissionsEntryRepository.save(createExcludeEmissionsEntry(complaintEntityIdentifier, 2021L , true));
        //Returned = 4 , Reversed = 6
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 10L, 10L, AllocationType.NAVAT , 4L ,6L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 100L,100L, AllocationType.NAVAT));
        
        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(allocationYear,ce.getId());
        assertEquals(2, allocationEntryRepository.calculateAllocationClassification(allocationYear, ce.getId()).size());
        assertEquals(100, summary.getEntitlement());
        assertEquals(100, summary.getAllocated());
        assertEquals(0, summary.getRemainingQuantity());
        assertNotNull(summary.getAllocationClassification(), "Should result in non-nullable Allocation Classification");
        assertEquals(AllocationClassification.FULLY_ALLOCATED.name(), summary.getAllocationClassification());
    }
    
    /*
     * Test excluded with allocation (exclusion after allocation).
     */
    @Test
    void testCalculateAllocationClassificationOverAllocated_UKETS_6971() {
        Integer allocationYear = 2022;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        excludeEmissionsEntryRepository.save(createExcludeEmissionsEntry(complaintEntityIdentifier, 2021L , true));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 10L, 10L, AllocationType.NAVAT));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 100L,100L, AllocationType.NAVAT));
        
        AllocationClassificationSummary summary = allocationCalculationService.calculateAllocationClassification(allocationYear,ce.getId());
        assertEquals(2, allocationEntryRepository.calculateAllocationClassification(allocationYear, ce.getId()).size());
        assertEquals(100, summary.getEntitlement());
        assertEquals(110, summary.getAllocated());
        assertEquals(-10, summary.getRemainingQuantity());
        assertNotNull(summary.getAllocationClassification(), "Should result in non-nullable Allocation Classification");
        assertEquals(AllocationClassification.OVER_ALLOCATED.name(), summary.getAllocationClassification());
    }
    
    @Test
    @Disabled
    void testUpdateAllocationStatusAllocated_UKETS_6971() {
        Integer allocationYear = 2022;
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        excludeEmissionsEntryRepository.save(createExcludeEmissionsEntry(complaintEntityIdentifier, 2021L , true));
        //Returned = 4 , Reversed = 6
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 10L, 10L, AllocationType.NAVAT , 4L ,6L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 200L,100L, AllocationType.NAVAT, 12L , 9L));
        
        allocationCalculationService.updateAllocationStatus(accountIdentifier,15L,AllocationType.NAVAT,allocationYear,TransactionType.AllocateAllowances);
        assertEquals(2, allocationEntryRepository.findAll().size());
        
        AllocationEntry resullt = allocationEntryRepository.findByCompliantEntityIdAndTypeAndAllocationYear_Year(ce.getId(), AllocationType.NAVAT, allocationYear);
        assertEquals(115, resullt.getAllocated());
        assertEquals(200, resullt.getEntitlement());
        assertEquals(12, resullt.getReturned());
        assertEquals(9, resullt.getReversed());
    }
    
    @Test
    @Disabled
    void testUpdateAllocationStatusReversed_UKETS_6971() {
        Integer allocationYear = 2022;
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        excludeEmissionsEntryRepository.save(createExcludeEmissionsEntry(complaintEntityIdentifier, 2021L , true));
        //Returned = 4 , Reversed = 6
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 10L, 10L, AllocationType.NAVAT , 4L ,6L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 200L,100L, AllocationType.NAVAT, 12L , 9L));
        
        allocationCalculationService.updateAllocationStatus(accountIdentifier,15L,AllocationType.NAVAT,allocationYear,TransactionType.ReverseAllocateAllowances);
        assertEquals(2, allocationEntryRepository.findAll().size());
        
        AllocationEntry resullt = allocationEntryRepository.findByCompliantEntityIdAndTypeAndAllocationYear_Year(ce.getId(), AllocationType.NAVAT, allocationYear);
        assertEquals(100, resullt.getAllocated());
        assertEquals(200, resullt.getEntitlement());
        assertEquals(12, resullt.getReturned());
        assertEquals(24, resullt.getReversed());
    }
    
    @Test
    @Disabled
    void testUpdateAllocationStatusReturned_UKETS_6971() {
        Integer allocationYear = 2022;
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        excludeEmissionsEntryRepository.save(createExcludeEmissionsEntry(complaintEntityIdentifier, 2021L , true));
        //Returned = 4 , Reversed = 6
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 10L, 10L, AllocationType.NAVAT , 4L ,6L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 200L,100L, AllocationType.NAVAT, 12L , 9L));
        
        allocationCalculationService.updateAllocationStatus(accountIdentifier,15L,AllocationType.NAVAT,allocationYear,TransactionType.ExcessAllocation);
        assertEquals(2, allocationEntryRepository.findAll().size());
        
        AllocationEntry resullt = allocationEntryRepository.findByCompliantEntityIdAndTypeAndAllocationYear_Year(ce.getId(), AllocationType.NAVAT, allocationYear);
        assertEquals(100, resullt.getAllocated());
        assertEquals(200, resullt.getEntitlement());
        assertEquals(27, resullt.getReturned());
        assertEquals(9, resullt.getReversed());
    }
    
    @DisplayName("Zero Entitlements case")
    @Test
    void testUpdateAllocationClassification_ZeroEntitlements_UKETS_6108() {
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 0L, 0L, AllocationType.NAVAT , 0L ,0L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 0L,0L, AllocationType.NAVAT, 0L , 0L));
        
        AllocationClassificationSummary summary_2021 =  allocationCalculationService.calculateAllocationClassification(2021, ce.getId());
        
        assertEquals(2, allocationEntryRepository.findAll().size());
       assertNotNull(summary_2021);
       assertNull(summary_2021.getAllocationClassification());
    }
    
    @Test
    void testUpdateAllocationClassification_NoAllocationEntry_UKETS_6108() {
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        
        AllocationClassificationSummary summary_2021 =  allocationCalculationService.calculateAllocationClassification(2021, ce.getId());
        
       assertNotNull(summary_2021);
       assertNull(summary_2021.getAllocationClassification());
    }
    
    
    //Not yet allocated - for at least one of the years up to and including the current calendar 
    //year the operator has a non-zero entitlement, 
    //is not excluded for that year and has received 0 allowances in allocation
    @DisplayName("Not Yet Allocated case")
    @Test
    void testUpdateAllocationClassification_NotYetAllocated_UKETS_6108() {
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 450L, 0L, AllocationType.NAVAT , 0L ,0L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 0L,0L, AllocationType.NAVAT, 0L , 0L));
        
        AllocationClassificationSummary summary_2022 =  allocationCalculationService.calculateAllocationClassification(2022, ce.getId());
        
        assertEquals(2, allocationEntryRepository.findAll().size());
       assertNotNull(summary_2022);
       assertEquals(AllocationClassification.NOT_YET_ALLOCATED , AllocationClassification.valueOf(summary_2022.getAllocationClassification()));
    }
    
    //Under allocated - for at least one of the years up to and including the current calendar 
    //year the operator has a non-zero entitlement, is not excluded for that year and has received less than their entitlement
    @DisplayName("Under Allocated case")
    @Test
    void testUpdateAllocationClassification_UnderAllocated_UKETS_6108() {
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 450L, 55L, AllocationType.NAVAT , 0L ,0L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 0L,0L, AllocationType.NAVAT, 0L , 0L));
        
        AllocationClassificationSummary summary_2022 =  allocationCalculationService.calculateAllocationClassification(2022, ce.getId());
        
        assertEquals(2, allocationEntryRepository.findAll().size());
       assertNotNull(summary_2022);
       assertEquals(AllocationClassification.UNDER_ALLOCATED , AllocationClassification.valueOf(summary_2022.getAllocationClassification()));
    }
    
    //Over allocated - for at least one of the years up to and including the current calendar 
    //year the operator has a non-zero entitlement, is not excluded for that year and has received more than their entitlement 
    //OR the operator has a non-zero entitlement, is excluded and has received 1 or more allowances
    @DisplayName("Over Allocated case")
    @Test
    void testUpdateAllocationClassification_OverAllocated_UKETS_6108() {
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 450L, 600L, AllocationType.NAVAT , 0L ,0L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 0L,0L, AllocationType.NAVAT, 0L , 0L));
        
        AllocationClassificationSummary summary_2022 =  allocationCalculationService.calculateAllocationClassification(2022, ce.getId());
        
        assertEquals(2, allocationEntryRepository.findAll().size());
       assertNotNull(summary_2022);
       assertEquals(AllocationClassification.OVER_ALLOCATED , AllocationClassification.valueOf(summary_2022.getAllocationClassification()));
    }
    
    //Fully allocated - for all years up to and including the current calendar year 
    //the operator has a non-zero entitlement, is not excluded and has received exactly their entitlement
    @DisplayName("Fully Allocated case")
    @Test
    void testUpdateAllocationClassification_FullyAllocated_UKETS_6108() {
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 450L, 450L, AllocationType.NAVAT , 0L ,0L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 0L,0L, AllocationType.NAVAT, 0L , 0L));
        
        AllocationClassificationSummary summary_2022 =  allocationCalculationService.calculateAllocationClassification(2022, ce.getId());
        
        assertEquals(2, allocationEntryRepository.findAll().size());
       assertNotNull(summary_2022);
       assertEquals(AllocationClassification.FULLY_ALLOCATED , AllocationClassification.valueOf(summary_2022.getAllocationClassification()));
    }
    
    //Null - for at least one of the years up to and including the current calendar 
    //year the operator has a non-zero entitlement, 
    //but is excluded for that year and has received 0 allowances in allocation
    @DisplayName("Entitled but Exluded case")
    @Test
    void testUpdateAllocationClassification_EntitledButExluded_UKETS_6108() {
        Long accountIdentifier = 100089L;
        Long complaintEntityIdentifier = 1000726L;
        CompliantEntity ce = compliantEntityRepository.save(createCompliantEntity(complaintEntityIdentifier, 2021, 2025));
        excludeEmissionsEntryRepository.save(createExcludeEmissionsEntry(complaintEntityIdentifier, 2022L , true));
        excludeEmissionsEntryRepository.save(createExcludeEmissionsEntry(complaintEntityIdentifier, 2023L , true));
        allocationStatusRepository.save(createAllocationStatus(ce.getId(), 2021, AllocationStatusType.WITHHELD));
        allocationStatusRepository.save(createAllocationStatus(ce.getId(), 2022, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(ce.getId(), 2023, AllocationStatusType.ALLOWED));
        AccountSampleEntity account1 = new AccountSampleEntity();
        account1.setIdentifier(accountIdentifier);
        account1.setCompliantEntityId(ce.getId());
        account1.setAccountStatus(AccountStatus.OPEN);
        entityManager.persist(account1);
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2021, 0L, 0L, AllocationType.NAT , 0L ,0L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2022, 100L,0L, AllocationType.NAT, 0L , 0L));
        allocationEntryRepository.save(createAllocationEntry(ce.getId(), 2023, 100L,0L, AllocationType.NAT, 0L , 0L));
        
        AllocationClassificationSummary summary_2023 =  allocationCalculationService.calculateAllocationClassification(2023, ce.getId());
        
        assertEquals(3, allocationEntryRepository.findAll().size());
       assertNotNull(summary_2023);
       assertNull(summary_2023.getAllocationClassification());
    }
}