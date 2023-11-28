package gov.uk.ets.registry.api.reconciliation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.domain.ReconciliationHistory;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationEntrySummaryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationFailedEntryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationHistoryRepository;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationEntrySummary;
import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {

    @Mock
    ReconciliationRepository reconciliationRepository;
    @Mock
    ReconciliationHistoryRepository reconciliationHistoryRepository;
    @Mock
    ReconciliationEntrySummaryRepository reconciliationEntrySummaryRepository;
    @Mock
    ReconciliationFailedEntryRepository reconciliationFailedEntryRepository;

    @Captor
    ArgumentCaptor<Reconciliation> storeReconciliationCaptor;

    @Captor
    ArgumentCaptor<ReconciliationHistory> storeReconciliationHistoryCaptor;

    ReconciliationService reconciliationService;

    @BeforeEach
    void setUp() {
        reconciliationService = new ReconciliationService(reconciliationRepository,
            reconciliationHistoryRepository,
            reconciliationEntrySummaryRepository,
            reconciliationFailedEntryRepository);
    }

    @DisplayName("The createReconciliation method should create a Reconciliation with created date, "
        + "update date and with INITIATED status and persist it")
    @Test
    void createReconciliation() {
        // given
        Date startDate = new Date();
        Long identifier = 1234567L;
        given(reconciliationRepository.getNextIdentifier()).willReturn(identifier);

        // when
        Reconciliation reconciliation = reconciliationService.createReconciliation(startDate);

        // then
        then(reconciliationRepository).should(times(1)).save(storeReconciliationCaptor.capture());
        Reconciliation toBeStoredReconciliation = storeReconciliationCaptor.getValue();
        assertEquals(identifier, toBeStoredReconciliation.getIdentifier());
        assertNotNull(toBeStoredReconciliation.getCreated());
        assertNotNull(toBeStoredReconciliation.getUpdated());
        assertEquals(ReconciliationStatus.INITIATED, toBeStoredReconciliation.getStatus());
    }

    @DisplayName(
        "The audit method should create a Reconciliation history entry for the passed reconciliation with date "
            + "the reconciliation update date and status the current reconciliation status.")
    @Test
    void audit() throws ParseException {
        // given
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setIdentifier(1234567L);
        reconciliation.setStatus(ReconciliationStatus.INITIATED);
        reconciliation.setUpdated(new SimpleDateFormat("dd/MM/yyyy").parse("12/03/2020 13:33:03"));
        // when
        reconciliationService.keepHistory(reconciliation);
        // then
        then(reconciliationHistoryRepository).should(times(1))
            .save(storeReconciliationHistoryCaptor.capture());

        ReconciliationHistory storedHistoryEntry = storeReconciliationHistoryCaptor.getValue();
        assertEquals(reconciliation.getUpdated(), storedHistoryEntry.getDate());
        assertEquals(reconciliation.getStatus(), storedHistoryEntry.getStatus());
        assertEquals(reconciliation, storedHistoryEntry.getReconciliation());
    }


    @DisplayName("The getLastReconciliationCompletedDate should return the created date of the last completed reconciliation")
    @Test
    void getLastReconciliationCompletedDate() {
        // given
        Date expectedResult = new Date();
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setCreated(expectedResult);
        given(reconciliationRepository.fetchLastCompletedReconciliation()).willReturn(reconciliation);
        // when
        Date result = reconciliationService.getLastReconciliationCompletedDate();
        // then
        then(reconciliationRepository).should().fetchLastCompletedReconciliation();
        assertEquals(expectedResult, result);

        // given
        given(reconciliationRepository.fetchLastCompletedReconciliation()).willReturn(null);
        // when
        result = reconciliationService.getLastReconciliationCompletedDate();
        then(reconciliationRepository).should(times(2)).fetchLastCompletedReconciliation();
        assertNull(result);
    }

    @DisplayName("The getReconciliationEntrySummaries should return the entry summaries by calling the reconciliationEntrySummaryRepository")
    @Test
    void getReconciliationEntrySummaries() {
        // given
        Long identifier = 21321L;
        Set<Long> accountIdentifiers = Set.of(identifier);
        ReconciliationEntrySummary testEntry = new ReconciliationEntrySummary();
        testEntry.setAccountIdentifier(identifier);
        testEntry.setTotal(100L);
        testEntry.setUnitType(UnitType.ALLOWANCE);
        List<ReconciliationEntrySummary> expectedResult = List.of(testEntry);
        given(reconciliationEntrySummaryRepository.fetch(accountIdentifiers)).willReturn(expectedResult);

        // when
        List<ReconciliationEntrySummary> result = reconciliationService
            .getReconciliationEntrySummaries(accountIdentifiers);

        // then
        then(reconciliationEntrySummaryRepository).should(times(1)).fetch(accountIdentifiers);
        assertEquals(expectedResult, result);
    }

    @DisplayName("To delete the reconciliation the reconciliation repository should be called")
    @Test
    void deleteReconciliation() {
        // given
        Long identifier = 123L;
        // when
        reconciliationService.deleteReconciliation(identifier);
        // then
        then(reconciliationRepository).should(times(1)).removeByIdentifier(identifier);
    }

    @DisplayName("To delete the reconciliation history entries, the reconciliation history should be called")
    @Test
    void deleteReconciliationHistory() {
        // given
        Long identifier = 123L;
        // when
        reconciliationService.deleteReconciliationHistory(identifier);
        // then
        then(reconciliationHistoryRepository).should(times(1)).deleteAllByReconciliationIdentifier(identifier);
    }

    @DisplayName("The updateReconciliation method should update the reconciliation and its failed entries.")
    @Test
    void updateReconciliation() throws JsonProcessingException {
        // given
        ReconciliationSummary reconciliationSummary = ReconciliationSummary.builder()
            .identifier(12323L)
            .status(ReconciliationStatus.COMPLETED)
            .build();
        Reconciliation reconciliation = new Reconciliation();
        reconciliation.setIdentifier(reconciliationSummary.getIdentifier());
        given(reconciliationRepository.findByIdentifier(reconciliationSummary.getIdentifier())).willReturn(reconciliation);

        //when
        Reconciliation result = reconciliationService.updateReconciliation(reconciliationSummary);

        assertEquals(new ObjectMapper().writeValueAsString(reconciliationSummary), reconciliation.getData());
        assertNotNull(reconciliation.getUpdated());
        assertEquals(reconciliationSummary.getStatus(), reconciliation.getStatus());
    }
}