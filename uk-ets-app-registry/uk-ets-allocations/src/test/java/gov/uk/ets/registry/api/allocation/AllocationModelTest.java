package gov.uk.ets.registry.api.allocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.domain.AllocationEntry;
import gov.uk.ets.registry.api.allocation.domain.AllocationPeriod;
import gov.uk.ets.registry.api.allocation.domain.AllocationPhase;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationPeriodRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationPhaseRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.allocation.service.AllocationPhaseCapService;
import gov.uk.ets.registry.api.allocation.service.AllocationStatusService;
import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"
})
class AllocationModelTest extends ParentAllocationBase {

    @Autowired
    private AllocationEntryRepository allocationEntryRepository;

    @Autowired
    private AllocationYearRepository allocationYearRepository;

    @Autowired
    private AllocationPhaseRepository allocationPhaseRepository;

    @Autowired
    private AllocationPeriodRepository allocationPeriodRepository;

    @Autowired
    private AllocationStatusRepository allocationStatusRepository;

    private AllocationPhaseCapService allocationPhaseCapService;

    private AllocationYearCapService allocationYearCapService;

    private AllocationStatusService allocationStatusService;

    @Mock
    private AllocationConfigurationService allocationConfigurationService;

    @BeforeEach
    @Transactional
    void setUp() {
        MockitoAnnotations.initMocks(this);

        allocationPhaseCapService = new AllocationPhaseCapService(allocationPhaseRepository);
        allocationYearCapService = new AllocationYearCapService(allocationYearRepository,allocationPhaseRepository, allocationConfigurationService);
        allocationStatusService = new AllocationStatusService(allocationEntryRepository, allocationStatusRepository);

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
    void testInitialData() {
        assertEquals(1, allocationPhaseRepository.findAll().size());
        assertEquals(2, allocationPeriodRepository.findAll().size());
        assertEquals(10, allocationYearRepository.findAll().size());
        assertNotNull(allocationYearRepository.findByYear(2021));


        AllocationPhase phase = allocationPhaseRepository.getAllocationPhaseOfYear(2021);
        AllocationPhase secondPhase = new AllocationPhase();
        secondPhase.setCode(1);
        phase.getPeriods();
        assertEquals(phase, secondPhase);

        AllocationPhase thirdPhase = new AllocationPhase();
        thirdPhase.setCode(2);
        assertNotEquals(secondPhase, thirdPhase);

        assertEquals(3, new HashSet<>(){{
            add(phase);
            add(secondPhase);
            add(thirdPhase);
            add(new AllocationPhase());
        }}.size());

        List<AllocationPeriod> periods = allocationPeriodRepository.findAll();
        assertEquals(2, periods.size());

        AllocationPeriod firstPeriod = periods.get(0);
        firstPeriod.getYears();

        AllocationPeriod secondPeriod = periods.get(1);
        secondPeriod.getYears();

    }

    @Test
    @Transactional
    void testPhaseCaps() {
        assertEquals(1000000L, allocationPhaseCapService.getRemainingCap(2021));

        allocationPhaseCapService.reserveCap(1000L, 2021);
        assertEquals(999000L, allocationPhaseCapService.getRemainingCap(2021));

        allocationPhaseCapService.releaseCap(1000L, 2021);
        assertEquals(1000000L, allocationPhaseCapService.getRemainingCap(2021));

        allocationPhaseCapService.reserveCap(1000L, 2021);
        allocationPhaseCapService.consumeCap(1000L, 2021);
        assertEquals(999000L, allocationPhaseCapService.getRemainingCap(2021));
    }

    @Test
    @Transactional
    void testYearlyCaps() {
        assertEquals(100000L, allocationYearCapService.getRemainingCap(2021));

        allocationYearCapService.reserveCap(500L, 2021);
        assertEquals(99500L, allocationYearCapService.getRemainingCap(2021));

        allocationYearCapService.releaseCap(500L, 2021);
        assertEquals(100000L, allocationYearCapService.getRemainingCap(2021));

        allocationYearCapService.reserveCap(500L, 2021);
        allocationYearCapService.consumeCap(500L, 2021);
        assertEquals(99500L, allocationYearCapService.getRemainingCap(2021));

        Mockito.when(allocationConfigurationService.getAllocationYear()).thenReturn(2015);
        assertEquals(0, allocationYearCapService.getCapsForCurrentPhase().size());

        Mockito.when(allocationConfigurationService.getAllocationYear()).thenReturn(2022);
        assertEquals(10, allocationYearCapService.getCapsForCurrentPhase().size());

        Mockito.when(allocationConfigurationService.getAllocationYear()).thenReturn(2029);
        assertEquals(10, allocationYearCapService.getCapsForCurrentPhase().size());

    }

    @Test
    @Transactional
    void testAllocationEntries() {
        final long compliantEntityId = 1L;
        allocationEntryRepository.save(createAllocationEntry(compliantEntityId, 2021, 10L, 50L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(compliantEntityId, 2022, 200L, 110L, AllocationType.NAT));
        allocationEntryRepository.save(createAllocationEntry(compliantEntityId, 2023, 200L, 400L, AllocationType.NAT));
        for (int year = 2024; year <= 2030 ; year++) {
            allocationEntryRepository.save(createAllocationEntry(compliantEntityId, year, 200L, 0L, AllocationType.NAT));
        }

        allocationEntryRepository.save(createAllocationEntry(compliantEntityId, 2021, 100L, 50L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(compliantEntityId, 2022, 100L, 150L, AllocationType.NER));
        allocationEntryRepository.save(createAllocationEntry(compliantEntityId, 2023, 100L, 100L, AllocationType.NER));
        for (int year = 2024; year <= 2030 ; year++) {
            allocationEntryRepository.save(createAllocationEntry(compliantEntityId, year, 100L, 0L, AllocationType.NER));
        }

        allocationStatusRepository.save(createAllocationStatus(compliantEntityId, 2021, AllocationStatusType.ALLOWED));
        allocationStatusRepository.save(createAllocationStatus(compliantEntityId, 2022, AllocationStatusType.WITHHELD));
        allocationStatusRepository.save(createAllocationStatus(compliantEntityId, 2023, AllocationStatusType.WITHHELD));

        for (int year = 2024; year <= 2030 ; year++) {
            allocationStatusRepository.save(createAllocationStatus(compliantEntityId, year, AllocationStatusType.ALLOWED));
        }

        assertEquals(0, allocationStatusService.getAllocationEntries(compliantEntityId, AllocationType.NAVAT).size());
        assertEquals(10, allocationStatusService.getAllocationEntries(compliantEntityId, AllocationType.NAT).size());
        assertEquals(10, allocationStatusService.getAllocationEntries(compliantEntityId, AllocationType.NER).size());

        for (AllocationEntry entry : allocationEntryRepository.findAll()) {
            assertNotNull(entry.getAllocated());
            assertNotNull(entry.getAllocationYear());
            assertNotNull(entry.getCompliantEntityId());
            assertNotNull(entry.getEntitlement());
            assertNotNull(entry.getId());
            assertNotNull(entry.getReturned());
            assertNotNull(entry.getReversed());
            assertNotNull(entry.getType());
        }

        Set<AllocationEntry> entrySet = new HashSet<>();
        entrySet.add(createAllocationEntry(1L, 2021, 100L, 0L, AllocationType.NER));
        entrySet.add(createAllocationEntry(1L, 2021, 200L, 20L, AllocationType.NER));
        entrySet.add(createAllocationEntry(1L, 2021, 300L, 30L, AllocationType.NER));
        assertEquals(1, entrySet.size());

        Set<AllocationStatus> statusSet = new HashSet<>();
        statusSet.add(createAllocationStatus(1L, 2025, AllocationStatusType.ALLOWED));
        statusSet.add(createAllocationStatus(1L, 2025, AllocationStatusType.ALLOWED));
        statusSet.add(createAllocationStatus(1L, 2025, AllocationStatusType.WITHHELD));
        statusSet.add(createAllocationStatus(1L, 2026, AllocationStatusType.WITHHELD));
        statusSet.add(createAllocationStatus(1L, 2026, AllocationStatusType.ALLOWED));
        statusSet.add(createAllocationStatus(2L, 2030, AllocationStatusType.ALLOWED));
        AllocationStatus status1 = new AllocationStatus();
        statusSet.add(status1);
        status1 = new AllocationStatus();
        status1.setCompliantEntityId(10L);
        statusSet.add(status1);
        status1 = new AllocationStatus();
        status1.setAllocationYear(allocationYearRepository.findByYear(2024));
        assertEquals(5, statusSet.size());

        for (AllocationStatus status : allocationStatusRepository.findAll()) {
            assertNotNull(status.getAllocationYear());
            assertNotNull(status.getCompliantEntityId());
            assertNotNull(status.getStatus());
        }

        AllocationYear year1 = allocationYearRepository.findByYear(2024);
        AllocationYear year2 = allocationYearRepository.findByYear(2022);
        assertNotEquals(year1, year2);
        assertEquals(year1.getPeriod(), year2.getPeriod());
        AllocationYear year3 = new AllocationYear();
        year3.setYear(2022);
        assertEquals(year2, year3);
        AllocationYear year4 = new AllocationYear();
        assertNotEquals(year1, year4);

        Set<AllocationYear> yearSet = new HashSet<>() {{
           add(year1);
           add(year2);
           add(year3);
           add(year4);
        }};
        assertEquals(3, yearSet.size());

        allocationEntryRepository.flush();
        allocationStatusRepository.flush();

        for (AllocationYear year: allocationYearRepository.findAll()) {
            assertNotNull(year.getAllocatedYearly());
            assertNotNull(year.getAuctionedYearly());
            assertNotNull(year.getConsumedYearlyCap());
            assertNotNull(year.getEntitlementYearly());
            assertNotNull(year.getReversedYearly());
            assertNotNull(year.getReturnedYearly());
            year.getAllocationEntries();
            year.getAllocationStatuses();
        }

        Map<Integer, AllocationStatusType> map = new HashMap<>();
        for (int year = 2021; year <= 2030 ; year++) {
            map.put(year, AllocationStatusType.WITHHELD);
        }
        allocationStatusService.updateAllocationStatus(1L, map);

        for (AllocationSummary summary  : allocationStatusService.getAllocationStatus(1L)) {
            assertNotNull(summary.getYear());
            assertNotNull(summary.getStatus());
            assertEquals(AllocationStatusType.WITHHELD, summary.getStatus());
        }
    }

    @Test
    void testStructure() {
        AllocationPeriod firstPeriod = new AllocationPeriod();
        firstPeriod.setCode(1);

        AllocationPeriod secondPeriod = new AllocationPeriod();
        secondPeriod.setCode(2);

        EqualsVerifier.forClass(AllocationPhase.class)
            .withOnlyTheseFields("code")
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .withPrefabValues(AllocationPeriod.class, firstPeriod, secondPeriod).verify();

        AllocationPhase phase1 = new AllocationPhase();
        phase1.setCode(1);

        AllocationPhase phase2 = new AllocationPhase();
        phase2.setCode(2);

        AllocationYear year1 = allocationYearRepository.findByYear(2021);
        AllocationYear year2 = allocationYearRepository.findByYear(2022);

        EqualsVerifier.forClass(AllocationPeriod.class)
            .withOnlyTheseFields("code")
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)            
            .withPrefabValues(AllocationPhase.class, phase1, phase2)
            .withPrefabValues(AllocationYear.class, year1, year2)
            .verify();

        AllocationEntry entry1 = createAllocationEntry(1L, 2021, 100L, 0L, AllocationType.NER);
        AllocationEntry entry2 = createAllocationEntry(2L, 2022, 100L, 0L, AllocationType.NER);

        AllocationStatus status1 = createAllocationStatus(1L, 2025, AllocationStatusType.WITHHELD);
        AllocationStatus status2 = createAllocationStatus(2L, 2025, AllocationStatusType.WITHHELD);

        EqualsVerifier.forClass(AllocationYear.class)
            .withOnlyTheseFields("year")
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)            
            .withPrefabValues(AllocationEntry.class, entry1, entry2)
            .withPrefabValues(AllocationStatus.class, status1, status2)
            .withPrefabValues(AllocationPeriod.class, firstPeriod, secondPeriod)
            .verify();

        EqualsVerifier.forClass(AllocationStatus.class)
            .withOnlyTheseFields("compliantEntityId", "allocationYear");

        EqualsVerifier.forClass(AllocationEntry.class)
            .withOnlyTheseFields("allocationYear", "compliantEntityId", "type");

    }

}