package gov.uk.ets.registry.api.compliance.service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.sis.internal.util.StandardDateFormat.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationOwnershipRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.allocation.data.AllocationClassificationSummary;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ExclusionEvent;
import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import gov.uk.ets.registry.api.compliance.repository.StaticComplianceStatusRepository;
import gov.uk.ets.registry.api.compliance.web.model.ComplianceOverviewDTO;
import gov.uk.ets.registry.api.compliance.web.model.ComplianceStatusHistoryResultDTO;
import gov.uk.ets.registry.api.compliance.web.model.OperatorEmissionsExclusionStatusChangeDTO;
import gov.uk.ets.registry.api.compliance.web.model.VerifiedEmissionsDTO;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableService;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestMethodOrder(OrderAnnotation.class)
class ComplianceServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private AccountService accountService;
    @Mock
    private ComplianceEventService complianceEventService;
    @Mock
    private EventService eventService;
    @Mock
    private ExcludeEmissionsRepository excludeEmissionsRepository;
    @Mock
    private EmissionsEntryRepository emissionsEntryRepository;
    @Mock
    private StaticComplianceStatusRepository staticComplianceStatusRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CompliantEntityRepository compliantEntityRepository;
    @Mock
    private InstallationOwnershipRepository installationOwnershipRepository;
    @Mock
    private AllocationCalculationService allocationCalculationService;
    @Mock
    private AllocationEntryRepository allocationEntryRepository;
    @Mock
    private EmissionsTableService emissionsTableService;

    private ComplianceService complianceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        complianceService = new ComplianceService(accountService, eventService, userService,
            complianceEventService, excludeEmissionsRepository, emissionsEntryRepository,
            staticComplianceStatusRepository,transactionRepository,compliantEntityRepository,installationOwnershipRepository,
                allocationCalculationService, allocationEntryRepository, emissionsTableService);
    }

    @Test
    @Order(1)
    @DisplayName("Update exclusion status, account does not exist, expected to fail")
    void testUpdateExclusionStatus_accountNotExists() {
        OperatorEmissionsExclusionStatusChangeDTO request = new OperatorEmissionsExclusionStatusChangeDTO();
        request.setExcluded(true);
        request.setYear(2021L);

        Mockito.when(accountService.getAccount(1000L)).thenReturn(null);

        UkEtsException exception = assertThrows(
            UkEtsException.class,
            () -> complianceService.updateExclusionStatus(1000L, request));

        assertTrue(exception.getMessage().contains("which does not exist"));
    }

    @Test
    @Order(2)
    @DisplayName("Update exclusion status, year is not included in the reporting period, expected to fail")
    void testUpdateExclusionStatus_yearNotInReportingPeriod() {
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(2021);
        installation.setEndYear(2022);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        OperatorEmissionsExclusionStatusChangeDTO request = new OperatorEmissionsExclusionStatusChangeDTO();
        request.setExcluded(true);
        request.setYear(2030L);

        Mockito.when(accountService.getAccount(1234L)).thenReturn(account);
        Mockito.when(compliantEntityRepository.findByIdentifier(1234L)).thenReturn(
            Optional.of((CompliantEntity) installation));

        BusinessRuleErrorException exception = assertThrows(
            BusinessRuleErrorException.class,
            () -> complianceService.updateExclusionStatus(1234L, request));

        assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString()
            .contains("The selected year should be within the reporting period"));
    }

    @Test
    @Order(4)
    @DisplayName("Update exclusion status for future year within the reporting period, expected to fail")
    void testUpdateExclusionStatus_futureYear() {
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(2021);
        installation.setEndYear(2030);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        OperatorEmissionsExclusionStatusChangeDTO request = new OperatorEmissionsExclusionStatusChangeDTO();
        request.setExcluded(true);
        request.setYear(2029L);

        Mockito.when(accountService.getAccount(1234L)).thenReturn(account);
        Mockito.when(compliantEntityRepository.findByIdentifier(1234L)).thenReturn(
            Optional.of((CompliantEntity) installation));

        BusinessRuleErrorException exception = assertThrows(
            BusinessRuleErrorException.class,
            () -> complianceService.updateExclusionStatus(1234L, request));

        assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString()
            .contains("The exclusion status cannot be updated for future years"));
    }

    @Test
    @Order(5)
    @DisplayName("Update exclusion status, emissions exist for this year, expected to pass")
    void testUpdateExclusionStatus_emissionsExist() {
        int currentYear = LocalDate.now().getYear();
        Installation installation = new Installation();
        installation.setId(1L);
        installation.setIdentifier(1234L);
        installation.setStartYear(2020);
        installation.setEndYear(currentYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        OperatorEmissionsExclusionStatusChangeDTO request = new OperatorEmissionsExclusionStatusChangeDTO();
        request.setExcluded(true);
        request.setYear((long) currentYear);

        User user = new User();
        user.setUrid("123456");

        EmissionsEntry entry = new EmissionsEntry();
        entry.setEmissions(100L);
        Mockito.when(accountService.getAccount(1234L)).thenReturn(account);
        Mockito.when(compliantEntityRepository.findByIdentifier(1234L)).thenReturn(
            Optional.of((CompliantEntity) installation));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(1234L, (long) currentYear))
            .thenReturn(Collections.singletonList(entry));
        Mockito.when(userService.getCurrentUser()).thenReturn(user);
        Mockito.when(allocationCalculationService.calculateAllocationClassification(currentYear, 1L))
            .thenReturn(new AllocationClassificationSummary(1L, AllocationClassification.NOT_YET_ALLOCATED.name()));

        complianceService.updateExclusionStatus(1234L, request);

        verify(emissionsEntryRepository, times(1))
            .save(any(EmissionsEntry.class));
        verify(emissionsTableService, times(1))
            .publishUpdateOfVerifiedEmissionsEvent(any(EmissionsEntry.class), any(Date.class));

        verify(allocationEntryRepository, Mockito.times(1)).updateAllocationClassification(1L,
            AllocationClassification.NOT_YET_ALLOCATED.name());
        verify(userService, Mockito.times(1)).getCurrentUser();

        ArgumentCaptor<ExcludeEmissionsEntry> argument = ArgumentCaptor.forClass(ExcludeEmissionsEntry.class);
        verify(excludeEmissionsRepository, Mockito.times(1)).save(argument.capture());
        assertEquals(1234L, argument.getValue().getCompliantEntityId());
        assertEquals(currentYear, argument.getValue().getYear());

        ArgumentCaptor<ExclusionEvent> argument2 = ArgumentCaptor.forClass(ExclusionEvent.class);
        verify(complianceEventService, Mockito.times(1)).processEvent(argument2.capture());
        assertEquals(1234L, argument2.getValue().getCompliantEntityId());
        assertEquals(currentYear, argument2.getValue().getYear());
    }

    @Test
    @Order(6)
    @DisplayName("Update exclusion status, set the same status, expected to fail")
    void testUpdateExclusionStatus_statusIsUnchanged() {
        int currentYear = LocalDate.now().getYear();
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(currentYear);
        installation.setEndYear(currentYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        OperatorEmissionsExclusionStatusChangeDTO request = new OperatorEmissionsExclusionStatusChangeDTO();
        request.setExcluded(true);
        request.setYear((long) currentYear);

        ExcludeEmissionsEntry entry = new ExcludeEmissionsEntry();
        entry.setExcluded(true);
        Mockito.when(accountService.getAccount(1234L)).thenReturn(account);
        Mockito.when(compliantEntityRepository.findByIdentifier(1234L)).thenReturn(
            Optional.of((CompliantEntity) installation));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(1234L, (long) currentYear))
            .thenReturn(Collections.singletonList(new EmissionsEntry()));
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityIdAndYear(1234L, (long) currentYear))
            .thenReturn(entry);

        BusinessRuleErrorException exception = assertThrows(
            BusinessRuleErrorException.class,
            () -> complianceService.updateExclusionStatus(1234L, request));

        assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString().
            contains("You must select the other option"));
    }

    @Test
    @Order(7)
    @DisplayName("Update exclusion status, expected to pass")
    void testUpdateExclusionStatus() {
        final int currentYear = LocalDate.now().getYear();
        Installation installation = new Installation();
        installation.setId(1L);
        installation.setIdentifier(1234L);
        installation.setStartYear(currentYear);
        installation.setEndYear(currentYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        OperatorEmissionsExclusionStatusChangeDTO request = new OperatorEmissionsExclusionStatusChangeDTO();
        request.setExcluded(true);
        request.setYear((long) currentYear);
        User user = new User();
        user.setUrid("123456");

        Mockito.when(accountService.getAccount(1234L)).thenReturn(account);
        Mockito.when(compliantEntityRepository.findByIdentifier(1234L)).thenReturn(
            Optional.of((CompliantEntity) installation));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(1234L, (long) currentYear))
            .thenReturn(Collections.singletonList(new EmissionsEntry()));
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityIdAndYear(1234L, (long) currentYear))
            .thenReturn(null);
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        Mockito.when(allocationCalculationService.calculateAllocationClassification(currentYear, 1L))
                .thenReturn(new AllocationClassificationSummary(1L, AllocationClassification.NOT_YET_ALLOCATED.name()));
        complianceService.updateExclusionStatus(1234L, request);

        verify(allocationEntryRepository, Mockito.times(1)).updateAllocationClassification(1L,
            AllocationClassification.NOT_YET_ALLOCATED.name());
        verify(userService, Mockito.times(1)).getCurrentUser();

        ArgumentCaptor<ExcludeEmissionsEntry> argument = ArgumentCaptor.forClass(ExcludeEmissionsEntry.class);
        verify(excludeEmissionsRepository, Mockito.times(1)).save(argument.capture());
        assertEquals(1234L, argument.getValue().getCompliantEntityId());
        assertEquals(currentYear, argument.getValue().getYear());

        ArgumentCaptor<ExclusionEvent> argument2 = ArgumentCaptor.forClass(ExclusionEvent.class);
        verify(complianceEventService, Mockito.times(1)).processEvent(argument2.capture());
        assertEquals(1234L, argument2.getValue().getCompliantEntityId());
        assertEquals(currentYear, argument2.getValue().getYear());
    }

    @Test
    @Order(8)
    @DisplayName("Update exclusion status, expected to pass")
    void testUpdateExclusionStatusWithoutAllocationClassification() {
        final int currentYear = LocalDate.now().getYear();
        Installation installation = new Installation();
        installation.setId(1L);
        installation.setIdentifier(1234L);
        installation.setStartYear(currentYear);
        installation.setEndYear(currentYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        OperatorEmissionsExclusionStatusChangeDTO request = new OperatorEmissionsExclusionStatusChangeDTO();
        request.setExcluded(true);
        request.setYear((long) currentYear);
        User user = new User();
        user.setUrid("123456");

        Mockito.when(accountService.getAccount(1234L)).thenReturn(account);
        Mockito.when(compliantEntityRepository.findByIdentifier(1234L)).thenReturn(
            Optional.of((CompliantEntity) installation));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(1234L, (long) currentYear))
            .thenReturn(Collections.singletonList(new EmissionsEntry()));
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityIdAndYear(1234L, (long) currentYear))
            .thenReturn(null);
        Mockito.when(userService.getCurrentUser()).thenReturn(user);

        Mockito.when(allocationCalculationService.calculateAllocationClassification(currentYear, 1L))
            .thenReturn(new AllocationClassificationSummary(1L, null));
        complianceService.updateExclusionStatus(1234L, request);

        verify(allocationEntryRepository, Mockito.times(0)).updateAllocationClassification(anyLong(), anyString());
        verify(userService, Mockito.times(1)).getCurrentUser();

        ArgumentCaptor<ExcludeEmissionsEntry> argument = ArgumentCaptor.forClass(ExcludeEmissionsEntry.class);
        verify(excludeEmissionsRepository, Mockito.times(1)).save(argument.capture());
        assertEquals(1234L, argument.getValue().getCompliantEntityId());
        assertEquals(currentYear, argument.getValue().getYear());

        ArgumentCaptor<ExclusionEvent> argument2 = ArgumentCaptor.forClass(ExclusionEvent.class);
        verify(complianceEventService, Mockito.times(1)).processEvent(argument2.capture());
        assertEquals(1234L, argument2.getValue().getCompliantEntityId());
        assertEquals(currentYear, argument2.getValue().getYear());
    }

    @Test
    @Order(9)
    @DisplayName("ReportableVerifiedEmissions current Year Greater than FYVE, FYVE = LYVE")
    void getReportableVerifiedEmissions_CurrentYear_Greater_than_FirstYear_equals_LastYear() {
        int currentYear = 2025;
        long compliantEntityId = 1234L;
        int startYear = 2023;
        int endYear = 2023;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
        installation.setEndYear(endYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        List<VerifiedEmissionsDTO> emissions =
            IntStream.rangeClosed(startYear, startYear).boxed().map(t-> new VerifiedEmissionsDTO(compliantEntityId, t.longValue(), "300", LocalDateTime.now())).collect(toUnmodifiableList());

        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(emissions);
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(new ArrayList<>());

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        List<VerifiedEmissionsDTO> expected = new ArrayList<>(emissions);
        assertIterableEquals(expected, result);
    }

    @Test
    @Order(10)
    @DisplayName("ReportableVerifiedEmissions current Year Less than First Year of Verified Emissions")
    void getReportableVerifiedEmissions_CurrentYear_Less_than_FirstYearVerifiedEmissions() {
        int currentYear = 2021;
        long compliantEntityId = 1234L;
        int startYear = 2023;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(IntStream.rangeClosed(startYear, 2027).boxed().map(t-> new VerifiedEmissionsDTO(compliantEntityId, t.longValue(), "300", LocalDateTime.now())).collect(toList()));
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(new ArrayList<ExcludeEmissionsEntry>());

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        assertTrue(result.isEmpty());
    }

    @Test
    @Order(11)
    @DisplayName("ReportableVerifiedEmissions Current Year between First and Last Year of Verified Emissions")
    void getReportableVerifiedEmissions_CurrentYear_Between_First_and_Last_Year() {
        int currentYear = 2025;
        long compliantEntityId = 1234L;
        int startYear = 2021;
        int endYear = 2028;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
        installation.setEndYear(endYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        List<VerifiedEmissionsDTO> emissions = IntStream.rangeClosed(startYear, currentYear-1).boxed()
            .map(t-> new VerifiedEmissionsDTO(compliantEntityId, t.longValue(), "300", LocalDateTime.now()))
            .collect(toUnmodifiableList());

        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(emissions);

        ExcludeEmissionsEntry excludeEntry = new ExcludeEmissionsEntry();
        excludeEntry.setCompliantEntityId(compliantEntityId);
        excludeEntry.setExcluded(true);
        excludeEntry.setYear((long) currentYear);
        excludeEntry.setLastUpdated(new Date());

        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(List.of(excludeEntry));

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        List<VerifiedEmissionsDTO> expected = new ArrayList<VerifiedEmissionsDTO>(emissions);
        expected.add(new VerifiedEmissionsDTO(compliantEntityId, excludeEntry.getYear(), "Excluded", LocalDateTime.ofInstant(excludeEntry.getLastUpdated().toInstant(),ZoneId.of(UTC))));

        assertIterableEquals(expected, result);
    }

    @Test
    @Order(12)
    @DisplayName("ReportableVerifiedEmissions Current Year greater than First and Last Year of Verified Emissions is null")
    void getReportableVerifiedEmissions_CurrentYear_Greater_than_First_and_Last_Year_Null() {
        int currentYear = 2025;
        long compliantEntityId = 1234L;
        int startYear = 2021;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
//        installation.setEndYear(endYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        List<VerifiedEmissionsDTO> emissions = IntStream.rangeClosed(startYear, currentYear-1).boxed()
            .map(t-> new VerifiedEmissionsDTO(compliantEntityId, t.longValue(), "300", LocalDateTime.now()))
            .collect(toUnmodifiableList());

        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(emissions);

        ExcludeEmissionsEntry excludeEntry = new ExcludeEmissionsEntry();
        excludeEntry.setCompliantEntityId(compliantEntityId);
        excludeEntry.setExcluded(true);
        excludeEntry.setYear((long) currentYear);
        excludeEntry.setLastUpdated(new Date());

        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(List.of(excludeEntry));

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        List<VerifiedEmissionsDTO> expected = new ArrayList<VerifiedEmissionsDTO>(emissions);
        expected.add(new VerifiedEmissionsDTO(compliantEntityId, excludeEntry.getYear(), "Excluded", LocalDateTime.ofInstant(excludeEntry.getLastUpdated().toInstant(),ZoneId.of(UTC))));

        assertIterableEquals(expected, result);
    }

    @Test
    @Order(13)
    @DisplayName("ReportableVerifiedEmissions Current Year greater than Last Year of Verified Emissions")
    void getReportableVerifiedEmissions_CurrentYear_Greater_than_Last_Year() {
        int currentYear = 2027;
        long compliantEntityId = 1234L;
        int startYear = 2021;
        int endYear = 2025;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
        installation.setEndYear(endYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        List<VerifiedEmissionsDTO> emissions =
            IntStream.rangeClosed(startYear, endYear-1).boxed().map(t-> new VerifiedEmissionsDTO(compliantEntityId, t.longValue(), "300", LocalDateTime.now())).collect(toUnmodifiableList());

        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(emissions);

        ExcludeEmissionsEntry excludeEntry = new ExcludeEmissionsEntry();
        excludeEntry.setCompliantEntityId(compliantEntityId);
        excludeEntry.setExcluded(true);
        excludeEntry.setYear((long) endYear);
        excludeEntry.setLastUpdated(new Date());

        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(List.of(excludeEntry));

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        List<VerifiedEmissionsDTO> expected = new ArrayList<VerifiedEmissionsDTO>(emissions);
        expected.add(new VerifiedEmissionsDTO(compliantEntityId, excludeEntry.getYear(), "Excluded", LocalDateTime.ofInstant(excludeEntry.getLastUpdated().toInstant(),ZoneId.of(UTC))));

        assertIterableEquals(expected, result);
    }

    @Test
    @Order(14)
    @DisplayName("ReportableVerifiedEmissions Current Year greater than Last Year of Verified Emissions with Non Excluded entry")
    void getReportableVerifiedEmissions_CurrentYear_Greater_than_Last_Year_NonExcludedEntry() {
        int currentYear = 2027;
        long compliantEntityId = 1234L;
        int startYear = 2021;
        int endYear = 2025;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
        installation.setEndYear(endYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        List<VerifiedEmissionsDTO> emissions = IntStream.rangeClosed(startYear, endYear-1)
            .boxed()
            .map(t-> new VerifiedEmissionsDTO(compliantEntityId, t.longValue(), "300", LocalDateTime.now()))
            .collect(toUnmodifiableList());

        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(emissions);

        ExcludeEmissionsEntry excludeEntry = new ExcludeEmissionsEntry();
        excludeEntry.setCompliantEntityId(compliantEntityId);
        excludeEntry.setExcluded(false);
        excludeEntry.setYear((long) endYear);
        excludeEntry.setLastUpdated(new Date());

        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(List.of(excludeEntry));

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        List<VerifiedEmissionsDTO> expected = new ArrayList<>(emissions);
        expected.add(new VerifiedEmissionsDTO(compliantEntityId, excludeEntry.getYear(), null, null));

        assertIterableEquals(expected, result);
    }

    @Test
    @Order(15)
    @DisplayName("ReportableVerifiedEmissions current Year equals to FYVE and LYVE is null")
    void getReportableVerifiedEmissions_CurrentYear_EqualsTo_FYVE_And_LYVE_is_Null() {
        int currentYear = 2021;
        long compliantEntityId = 1234L;
        int startYear = 2021;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        List<VerifiedEmissionsDTO> emissions = List.of(new VerifiedEmissionsDTO(compliantEntityId, 2021L, "300",
            LocalDateTime.now()));
        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(emissions);
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(new ArrayList<ExcludeEmissionsEntry>());

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        assertIterableEquals(emissions, result);
    }

    @Test
    @Order(16)
    @DisplayName("ReportableVerifiedEmissions current Year equals to FYVE and less than LYVE")
    void getReportableVerifiedEmissions_CurrentYear_EqualsTo_FYVE_And_LessThan_LYVE() {
        int currentYear = 2021;
        long compliantEntityId = 1234L;
        int startYear = 2021;
        int endYear = 2025;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
        installation.setEndYear(endYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        List<VerifiedEmissionsDTO> emissions = List.of(new VerifiedEmissionsDTO(compliantEntityId, 2021L, "300",
            LocalDateTime.now()));
        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(emissions);
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(new ArrayList<ExcludeEmissionsEntry>());

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        assertIterableEquals(emissions, result);
    }

    @Test
    @Order(17)
    @DisplayName("ReportableVerifiedEmissions Current Year equals to Last Year of Verified Emissions")
    void getReportableVerifiedEmissions_CurrentYear_Equals_To_Last_Year() {
        int currentYear = 2025;
        long compliantEntityId = 1234L;
        int startYear = 2021;
        int endYear = 2025;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(startYear);
        installation.setEndYear(endYear);
        Account account = new Account();
        account.setIdentifier(1234L);
        account.setCompliantEntity(installation);

        List<VerifiedEmissionsDTO> emissions = IntStream.rangeClosed(startYear, endYear-2).boxed().map(t-> new VerifiedEmissionsDTO(compliantEntityId, t.longValue(), "300", LocalDateTime.now())).collect(toUnmodifiableList());

        Mockito.when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        Mockito.when(emissionsEntryRepository.findLatestByCompliantEntityIdentifier(compliantEntityId))
            .thenReturn(emissions);

        ExcludeEmissionsEntry excludeEntry = new ExcludeEmissionsEntry();
        excludeEntry.setCompliantEntityId(compliantEntityId);
        excludeEntry.setExcluded(true);
        excludeEntry.setYear(endYear-1L);
        excludeEntry.setLastUpdated(new Date());

        Mockito.when(excludeEmissionsRepository.findByCompliantEntityId(compliantEntityId))
            .thenReturn(List.of(excludeEntry));

        List<VerifiedEmissionsDTO> result = complianceService.getReportableVerifiedEmissions(1234L, currentYear)
            .getVerifiedEmissions();

        List<VerifiedEmissionsDTO> expected = new ArrayList<VerifiedEmissionsDTO>(emissions);
        expected.add(new VerifiedEmissionsDTO(compliantEntityId, excludeEntry.getYear(), "Excluded", LocalDateTime.ofInstant(excludeEntry.getLastUpdated().toInstant(),ZoneId.of(UTC))));
        expected.add(new VerifiedEmissionsDTO(compliantEntityId, (long) currentYear, null, null));

        assertIterableEquals(expected, result);
    }

    @Test
    @Order(18)
    @DisplayName("Get Compliance Overview, current Year Less than First Year of Verified Emissions")
    void getComplianceOverview_CurrentYear_Less_than_FirstYearVerifiedEmissions() {
        Long accountIdentifier = 44444L;
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(2025);
        installation.setEndYear(2030);
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setCompliantEntity(installation);

        List<Long> emissions = List.of(0L);

        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        Mockito.when(emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(
            accountIdentifier)).thenReturn(emissions);

        ComplianceOverviewDTO overview = complianceService.getComplianceOverview(accountIdentifier);

        assertNotNull(overview);
        assertEquals(0, overview.getTotalVerifiedEmissions());
    }

    @Test
    @Order(19)
    @DisplayName("Get Compliance Overview,Current Year Greater Than First Year of Verified Emissions\n")
    void getComplianceOverview_CurrentYear_Greater_Than_FirstYear_1() {
        Long accountIdentifier = 44444L;
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(2020);
        installation.setEndYear(2050);
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setCompliantEntity(installation);

        List<Long> emissions = List.of(400L);

        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        Mockito.when(emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(
            accountIdentifier)).thenReturn(emissions);

        ComplianceOverviewDTO overview = complianceService.getComplianceOverview(accountIdentifier);

        assertNotNull(overview);
        assertEquals(400, overview.getTotalVerifiedEmissions());
    }

    @Test
    @Order(20)
    @DisplayName("Get Compliance Overview,Current Year Greater Than First Year of Verified Emissions\n")
    void getComplianceOverview_CurrentYear_Greater_Than_FirstYear_2() {
        Long accountIdentifier = 44444L;
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(2019);
        installation.setEndYear(2050);
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setCompliantEntity(installation);

        List<Long> emissions = List.of(400L, 200L);

        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        Mockito.when(emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(
            accountIdentifier)).thenReturn(emissions);

        ComplianceOverviewDTO overview = complianceService.getComplianceOverview(accountIdentifier);

        assertNotNull(overview);
        assertEquals(600, overview.getTotalVerifiedEmissions());
    }

    @Test
    @Order(21)
    @DisplayName("Get Compliance Overview,Current Year Greater Than First Year of Verified Emissions\n")
    void getComplianceOverview_zero_emissions() {
        Long accountIdentifier = 44444L;
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        List<Long> emissions = List.of(0L);

        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        Mockito.when(emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(
                accountIdentifier)).thenReturn(emissions);

        ComplianceOverviewDTO overview = complianceService.getComplianceOverview(accountIdentifier);

        assertNotNull(overview);
        assertEquals(0, overview.getTotalVerifiedEmissions());
    }

    @Test
    @Order(22)
    @DisplayName("Get Compliance Overview,Current Year Greater Than First Year of Verified Emissions\n")
    void getComplianceOverview_null_emissions() {
        Long accountIdentifier = 44444L;
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        List<Long> emissions = List.of();

        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        Mockito.when(emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(
                accountIdentifier)).thenReturn(emissions);

        ComplianceOverviewDTO overview = complianceService.getComplianceOverview(accountIdentifier);

        assertNotNull(overview);
        assertNull(overview.getTotalVerifiedEmissions());
    }

    @Test
    @Order(23)
    @DisplayName("Get Compliance Overview,Current Year greater than Last Year of Verified Emissions\n")
    void getComplianceOverview_CurrentYear_Greater_Than_LastYear() {
        Long accountIdentifier = 44444L;
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(2015);
        installation.setEndYear(2018);
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setCompliantEntity(installation);

        List<Long> emissions = List.of(400L, 200L, 100L, 50L);

        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        Mockito.when(emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(
            accountIdentifier)).thenReturn(emissions);

        ComplianceOverviewDTO overview = complianceService.getComplianceOverview(accountIdentifier);

        assertNotNull(overview);
        assertEquals(750, overview.getTotalVerifiedEmissions());
    }

    @Test
    @Order(24)
    @DisplayName("Get Compliance Overview,CurrentYear_Greater_than_First_and_Last_Year_Null\n")
    void getComplianceOverview_CurrentYear_Greater_than_First_and_Last_Year_Null() {
        Long accountIdentifier = 44444L;
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(2015);
        installation.setEndYear(null);
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setCompliantEntity(installation);

        List<Long> emissions = List.of(400L, 200L, 100L, 50L, 10L, 100L);

        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        Mockito.when(emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(
            accountIdentifier))
            .thenReturn(emissions);

        ComplianceOverviewDTO overview = complianceService.getComplianceOverview(accountIdentifier);

        assertNotNull(overview);
        assertEquals(860, overview.getTotalVerifiedEmissions());

    }

    @Test
    @Order(25)
    @DisplayName("Get Compliance Overview,CurrentYear_Greater_than_First_and_Last_Year_Null\n")
    void getComplianceOverview_CurrentYear_Greater_than_First_and_First_Year_Equal_To_Last_Year() {
        Long accountIdentifier = 44444L;
        Installation installation = new Installation();
        installation.setIdentifier(1234L);
        installation.setStartYear(2015);
        installation.setEndYear(2015);
        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setCompliantEntity(installation);

        List<Long> emissions = List.of(400L, 200L, 100L, 50L, 10L, 100L);

        Mockito.when(accountService.getAccount(accountIdentifier)).thenReturn(account);
        Mockito.when(emissionsEntryRepository.findTotalVerifiedEmissionsByAccountIdentifier(
            accountIdentifier))
            .thenReturn(emissions);

        ComplianceOverviewDTO overview = complianceService.getComplianceOverview(accountIdentifier);

        assertNotNull(overview);
        assertEquals(860, overview.getTotalVerifiedEmissions());
    }

    @Test
    @Order(26)
    @DisplayName("Get Compliance Status History, current year <= startYear")
    void getComplianceStatusHistory_CurrentYear_LessOrEqual_Than_StartYear() {
        Long compliantEntityId = 1L;
        Installation installation = new Installation();
        installation.setStartYear(2050);
        installation.setEndYear(null);
        when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));

        ComplianceStatusHistoryResultDTO complianceStatusHistory =
            complianceService.getComplianceStatusHistory(compliantEntityId);

        verifyNoInteractions(staticComplianceStatusRepository);
        assertNull(complianceStatusHistory.getComplianceStatusHistory());
        assertNull(complianceStatusHistory.getLastYearOfVerifiedEmissions());
    }

    @Test
    @Order(27)
    @DisplayName("Get Compliance Status History, current year > startYear, endYear null")
    void getComplianceStatusHistory_CurrentYear_Greater_Than_StartYear_End_Year_Null() {
        Long compliantEntityId = 1L;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(2019);
        installation.setEndYear(null);

        StaticComplianceStatus complianceStatus = new StaticComplianceStatus();
        complianceStatus.setCompliantEntity(installation);
        complianceStatus.setYear(2020L);
        complianceStatus.setComplianceStatus(ComplianceStatus.C);
        when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        when(staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearBetween(compliantEntityId
            ,installation.getStartYear().longValue(), (long)(LocalDate.now().getYear() -1) ))
            .thenReturn(List.of(complianceStatus));

        ComplianceStatusHistoryResultDTO complianceStatusHistory =
            complianceService.getComplianceStatusHistory(compliantEntityId);

        verify(staticComplianceStatusRepository, never()).findByCompliantEntityIdentifierAndYearGreaterThanEqual(anyLong(), anyLong());
        assertEquals(1, complianceStatusHistory.getComplianceStatusHistory().size());
        assertEquals(2020, complianceStatusHistory.getComplianceStatusHistory().get(0).getYear());
        assertEquals(ComplianceStatus.C, complianceStatusHistory.getComplianceStatusHistory().get(0).getStatus());
        assertNull(complianceStatusHistory.getLastYearOfVerifiedEmissions());
    }

    @Test
    @Order(28)
    @DisplayName("Get Compliance Status History, startYear <  current year <= endYear + 1")
    void getComplianceStatusHistory_CurrentYear_Greater_Than_StartYear_Less_Than_NextOfEndYear() {
        Long compliantEntityId = 1L;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(2019);
        installation.setEndYear(2050);

        StaticComplianceStatus complianceStatus = new StaticComplianceStatus();
        complianceStatus.setCompliantEntity(installation);
        complianceStatus.setYear(2020L);
        complianceStatus.setComplianceStatus(ComplianceStatus.C);
        when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        when(staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearBetween(compliantEntityId
            ,installation.getStartYear().longValue(), (long)(LocalDate.now().getYear() -1) ))
            .thenReturn(List.of(complianceStatus));

        ComplianceStatusHistoryResultDTO complianceStatusHistory =
            complianceService.getComplianceStatusHistory(compliantEntityId);

        verify(staticComplianceStatusRepository, never()).findByCompliantEntityIdentifierAndYearGreaterThanEqual(anyLong(), anyLong());
        assertEquals(1, complianceStatusHistory.getComplianceStatusHistory().size());
        assertEquals(2020, complianceStatusHistory.getComplianceStatusHistory().get(0).getYear());
        assertEquals(ComplianceStatus.C, complianceStatusHistory.getComplianceStatusHistory().get(0).getStatus());
        assertEquals(installation.getEndYear(), complianceStatusHistory.getLastYearOfVerifiedEmissions());
    }

    @Test
    @Order(29)
    @DisplayName("Get Compliance Status History, current year > endYear + 1")
    void getComplianceStatusHistory_CurrentYear_Greater_Than_NextOfEndYear() {
        Long compliantEntityId = 1L;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(2015);
        installation.setEndYear(2018);

        StaticComplianceStatus complianceStatus = new StaticComplianceStatus();
        complianceStatus.setCompliantEntity(installation);
        complianceStatus.setYear(2017L);
        complianceStatus.setComplianceStatus(ComplianceStatus.C);
        when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        when(staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearGreaterThanEqual(compliantEntityId
            ,installation.getStartYear().longValue()))
            .thenReturn(List.of(complianceStatus));

        ComplianceStatusHistoryResultDTO complianceStatusHistory =
            complianceService.getComplianceStatusHistory(compliantEntityId);

        verify(staticComplianceStatusRepository, never()).findByCompliantEntityIdentifierAndYearBetween(anyLong(),
            anyLong(), anyLong());
        assertEquals(1, complianceStatusHistory.getComplianceStatusHistory().size());
        assertEquals(2017L, complianceStatusHistory.getComplianceStatusHistory().get(0).getYear());
        assertEquals(ComplianceStatus.C, complianceStatusHistory.getComplianceStatusHistory().get(0).getStatus());
        assertEquals(installation.getEndYear(), complianceStatusHistory.getLastYearOfVerifiedEmissions());
    }

    @Test
    @Order(30)
    @DisplayName("Get Compliance Status History, startYear <  current, no compliance status found")
    void getComplianceStatusHistory_CurrentYear_Greater_Than_StartYear_No_Compliance_Found() {
        Long compliantEntityId = 1L;
        Installation installation = new Installation();
        installation.setIdentifier(compliantEntityId);
        installation.setStartYear(2019);
        installation.setEndYear(2050);

        when(compliantEntityRepository.findByIdentifier(compliantEntityId)).thenReturn(Optional.of(installation));
        when(staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearBetween(compliantEntityId
            ,installation.getStartYear().longValue(), (long)(LocalDate.now().getYear() -1) )).thenReturn(List.of());

        ComplianceStatusHistoryResultDTO complianceStatusHistory =
            complianceService.getComplianceStatusHistory(compliantEntityId);

        verify(staticComplianceStatusRepository, never()).findByCompliantEntityIdentifierAndYearGreaterThanEqual(anyLong(), anyLong());
        assertNotNull(complianceStatusHistory.getComplianceStatusHistory());
        assertEquals(0, complianceStatusHistory.getComplianceStatusHistory().size());
        assertEquals(installation.getEndYear(), complianceStatusHistory.getLastYearOfVerifiedEmissions());
    }
}
