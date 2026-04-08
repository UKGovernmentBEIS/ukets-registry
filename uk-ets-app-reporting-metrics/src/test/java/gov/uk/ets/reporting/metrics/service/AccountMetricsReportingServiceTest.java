package gov.uk.ets.reporting.metrics.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import gov.uk.ets.reporting.metrics.domain.AccountMetrics;
import gov.uk.ets.reporting.metrics.domain.ProcessedAccountMetricsEvent;
import gov.uk.ets.reporting.metrics.messaging.events.DynamicComplianceStatusCalculatedEvent;
import gov.uk.ets.reporting.metrics.messaging.events.EmissionsUpdatedEvent;
import gov.uk.ets.reporting.metrics.messaging.events.InstallationTransferEvent;
import gov.uk.ets.reporting.metrics.messaging.events.TransactionFinalisationEvent;
import gov.uk.ets.reporting.metrics.outbox.repository.AccountMetricsRepository;
import gov.uk.ets.reporting.metrics.outbox.repository.ProcessedAccountMetricsEventRepository;
import gov.uk.ets.reporting.metrics.types.ComplianceStatus;
import gov.uk.ets.reporting.metrics.types.TransactionStatus;
import gov.uk.ets.reporting.metrics.types.TransactionType;


public class AccountMetricsReportingServiceTest {
	
    @Mock
    private AccountMetricsRepository accountMetricsRepository;
    @Mock
    private ProcessedAccountMetricsEventRepository processedAccountMetricsEventRepository;

    
    private AccountMetricsReportingService accountMetricsReportingService;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        accountMetricsReportingService = new AccountMetricsReportingService(accountMetricsRepository,processedAccountMetricsEventRepository);
    }
    
    
    @Test
    @DisplayName("Verify that upon surrender transaction completion metrics are updated correctly for an existing in metrics account.")
    void testSurrenderTransactionCompletedEvent() {
    
    	final ComplianceStatus initialDynamicComplianceStatus = ComplianceStatus.C;
    	final Long initialQuantitySurrendered = 120L;
    	final Long initialQuantityReversedSurrendered = 0L;
    	final Long initialTotalVerifiedEmissions = 500L;
    	final Long transactionQuantitySurrendered = 450L;
    	final UUID eventId = UUID.randomUUID();
    	
    	TransactionFinalisationEvent transactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventId)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(TransactionStatus.COMPLETED)
    	.transactionType(TransactionType.SurrenderAllowances)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantitySurrendered)
    	.build();

    	AccountMetrics accountMetrics = new AccountMetrics();
    	accountMetrics.setDynamicComplianceStatus(initialDynamicComplianceStatus);
    	accountMetrics.setIdentifier(transactionCompletedEvent.getTransferringAccountIdentifier());
    	accountMetrics.setQuantityReversedSurrendered(initialQuantityReversedSurrendered);
    	accountMetrics.setQuantitySurrendered(initialQuantitySurrendered);
    	accountMetrics.setTotalVerifiedEmissions(initialTotalVerifiedEmissions);
    	accountMetrics.setSurrenderBalance(initialQuantitySurrendered - initialQuantityReversedSurrendered - initialTotalVerifiedEmissions);
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(transactionCompletedEvent.getEventId())).thenReturn(
                Optional.empty());
        Mockito.when(accountMetricsRepository.findByIdentifier(transactionCompletedEvent.getTransferringAccountIdentifier())).thenReturn(
            Optional.of(accountMetrics));
        
        accountMetricsReportingService.processAccountMetricEvent(transactionCompletedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(10000081L);
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue();        
        
        assertEquals(initialDynamicComplianceStatus , actual.getDynamicComplianceStatus());
        assertEquals(initialTotalVerifiedEmissions , actual.getTotalVerifiedEmissions());
        assertEquals(initialQuantitySurrendered + transactionQuantitySurrendered , actual.getQuantitySurrendered());
        assertEquals(initialQuantityReversedSurrendered , actual.getQuantityReversedSurrendered());
        assertEquals(initialTotalVerifiedEmissions , actual.getTotalVerifiedEmissions());
        assertEquals(initialQuantitySurrendered + transactionQuantitySurrendered - initialQuantityReversedSurrendered - initialTotalVerifiedEmissions , actual.getSurrenderBalance());
        
        ArgumentCaptor<ProcessedAccountMetricsEvent> processedAccountMetricsEventCaptor = ArgumentCaptor.forClass(ProcessedAccountMetricsEvent.class);
        verify(processedAccountMetricsEventRepository).save(processedAccountMetricsEventCaptor.capture());
        ProcessedAccountMetricsEvent processedAccountMetricsEvent = processedAccountMetricsEventCaptor.getValue();
        assertNotNull(processedAccountMetricsEvent.getPayload());
        assertNotNull(processedAccountMetricsEvent.getProcessedOn());
    }
    
    @Test
    @DisplayName("Verify that the TransactionFinalisationEvent Kafka consumer is Idempotent.")
    void testSurrenderTransactionCompletedEventIdempotentConsumer() {
    
    	final ComplianceStatus initialDynamicComplianceStatus = ComplianceStatus.C;
    	final Long initialQuantitySurrendered = 120L;
    	final Long initialQuantityReversedSurrendered = 0L;
    	final Long initialTotalVerifiedEmissions = 500L;
    	final Long transactionQuantitySurrendered = 450L;
    	final UUID eventID = UUID.randomUUID();
    	
    	TransactionFinalisationEvent transactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventID)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(TransactionStatus.COMPLETED)
    	.transactionType(TransactionType.SurrenderAllowances)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantitySurrendered)
    	.build();

    	TransactionFinalisationEvent duplicatedTransactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventID)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(TransactionStatus.COMPLETED)
    	.transactionType(TransactionType.SurrenderAllowances)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantitySurrendered)
    	.build();    	
    	
    	AccountMetrics accountMetrics = new AccountMetrics();
    	accountMetrics.setDynamicComplianceStatus(initialDynamicComplianceStatus);
    	accountMetrics.setIdentifier(transactionCompletedEvent.getTransferringAccountIdentifier());
    	accountMetrics.setQuantityReversedSurrendered(initialQuantityReversedSurrendered);
    	accountMetrics.setQuantitySurrendered(initialQuantitySurrendered);
    	accountMetrics.setTotalVerifiedEmissions(initialTotalVerifiedEmissions);
    	accountMetrics.setSurrenderBalance(initialQuantitySurrendered - initialQuantityReversedSurrendered - initialTotalVerifiedEmissions);
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(eventID)).thenReturn(
                Optional.of(new ProcessedAccountMetricsEvent()));
        Mockito.when(accountMetricsRepository.findByIdentifier(transactionCompletedEvent.getTransferringAccountIdentifier())).thenReturn(
            Optional.of(accountMetrics));
        
        accountMetricsReportingService.processAccountMetricEvent(transactionCompletedEvent);
        accountMetricsReportingService.processAccountMetricEvent(duplicatedTransactionCompletedEvent);

        verify(processedAccountMetricsEventRepository, times(2)).findByEventId(eventID);
        
        verify(accountMetricsRepository, times(0)).findByIdentifier(10000081L);
        verify(accountMetricsRepository, times(0)).save(any());
    }    
    
    @Test
    @DisplayName("Verify that upon surrender transaction completion metrics are updated correctly for a new account.")
    void testSurrenderTransactionCompletedEventForNewAccount() {
    
    	final Long transactionQuantitySurrendered = 450L;
    	final UUID eventId = UUID.randomUUID();
    	
    	TransactionFinalisationEvent transactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventId)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(TransactionStatus.COMPLETED)
    	.transactionType(TransactionType.SurrenderAllowances)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantitySurrendered)
    	.build();
    	
    	AccountMetrics accountMetrics = new AccountMetrics();
    	accountMetrics.setDynamicComplianceStatus(null);
    	accountMetrics.setIdentifier(transactionCompletedEvent.getTransferringAccountIdentifier());
    	accountMetrics.setQuantityReversedSurrendered(null);
    	accountMetrics.setQuantitySurrendered(transactionQuantitySurrendered);
    	accountMetrics.setTotalVerifiedEmissions(null);
    	accountMetrics.setSurrenderBalance(transactionQuantitySurrendered);    	
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(transactionCompletedEvent.getEventId())).thenReturn(
                Optional.empty());
        Mockito.when(accountMetricsRepository.findByIdentifier(transactionCompletedEvent.getTransferringAccountIdentifier())).thenReturn(
            Optional.empty());

        accountMetricsReportingService.processAccountMetricEvent(transactionCompletedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(10000081L);
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue();  
        
        assertNull(actual.getDynamicComplianceStatus());
        assertEquals(transactionCompletedEvent.getTransferringAccountIdentifier(), actual.getIdentifier());
        assertEquals(transactionQuantitySurrendered , actual.getQuantitySurrendered());
        assertNull(actual.getQuantityReversedSurrendered());
        assertNull(actual.getTotalVerifiedEmissions());
        assertEquals(transactionQuantitySurrendered , actual.getSurrenderBalance());
        

    }
    
    @Test
    @DisplayName("Verify that upon reverse surrender transaction completion metrics are updated correctly for an existing in metrics account.")
    void testReverseSurrenderTransactionCompletedEvent() {
    
    	final ComplianceStatus initialDynamicComplianceStatus = ComplianceStatus.C;
    	final Long initialQuantitySurrendered = 120L;
    	final Long initialQuantityReversedSurrendered = 0L;
    	final Long initialTotalVerifiedEmissions = 500L;
    	final Long transactionQuantityReversedSurrendered = 450L;
    	final UUID eventId = UUID.randomUUID();
    	
    	TransactionFinalisationEvent transactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventId)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(TransactionStatus.COMPLETED)
    	.transactionType(TransactionType.ReverseSurrenderAllowances)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantityReversedSurrendered)
    	.build();

    	AccountMetrics accountMetrics = new AccountMetrics();
    	accountMetrics.setDynamicComplianceStatus(initialDynamicComplianceStatus);
    	accountMetrics.setIdentifier(transactionCompletedEvent.getAcquiringAccountIdentifier());
    	accountMetrics.setQuantityReversedSurrendered(initialQuantityReversedSurrendered);
    	accountMetrics.setQuantitySurrendered(initialQuantitySurrendered);
    	accountMetrics.setTotalVerifiedEmissions(initialTotalVerifiedEmissions);
    	accountMetrics.setSurrenderBalance(initialQuantitySurrendered - initialQuantityReversedSurrendered - initialTotalVerifiedEmissions);
    	
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(transactionCompletedEvent.getEventId())).thenReturn(
                Optional.empty());
        Mockito.when(accountMetricsRepository.findByIdentifier(transactionCompletedEvent.getAcquiringAccountIdentifier())).thenReturn(
            Optional.of(accountMetrics));

        accountMetricsReportingService.processAccountMetricEvent(transactionCompletedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(10000025L);
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue();          
        
        assertEquals(initialDynamicComplianceStatus , actual.getDynamicComplianceStatus());
        assertEquals(initialTotalVerifiedEmissions , actual.getTotalVerifiedEmissions());
        assertEquals(initialQuantitySurrendered , actual.getQuantitySurrendered());
        assertEquals(transactionQuantityReversedSurrendered , actual.getQuantityReversedSurrendered());
        assertEquals(initialTotalVerifiedEmissions , actual.getTotalVerifiedEmissions());
        assertEquals(initialQuantitySurrendered - transactionQuantityReversedSurrendered - initialTotalVerifiedEmissions , actual.getSurrenderBalance());
    }
    
    @Test
    @DisplayName("Verify that upon reverse surrender transaction completion metrics are updated correctly for a new account.")
    void testReverseSurrenderTransactionCompletedEventForNewAccount() {
    
    	final Long transactionQuantityReversedSurrendered = 450L;
    	final UUID eventId = UUID.randomUUID();
    	
    	TransactionFinalisationEvent transactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventId)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(TransactionStatus.COMPLETED)
    	.transactionType(TransactionType.ReverseSurrenderAllowances)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantityReversedSurrendered)
    	.build();
    	
    	AccountMetrics accountMetrics = new AccountMetrics();
    	accountMetrics.setDynamicComplianceStatus(null);
    	accountMetrics.setIdentifier(transactionCompletedEvent.getAcquiringAccountIdentifier());
    	accountMetrics.setQuantityReversedSurrendered(transactionQuantityReversedSurrendered);
    	accountMetrics.setQuantitySurrendered(null);
    	accountMetrics.setTotalVerifiedEmissions(null);
    	accountMetrics.setSurrenderBalance(-transactionQuantityReversedSurrendered);    	
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(transactionCompletedEvent.getEventId())).thenReturn(
                Optional.empty());
        Mockito.when(accountMetricsRepository.findByIdentifier(transactionCompletedEvent.getAcquiringAccountIdentifier())).thenReturn(
            Optional.empty());

        accountMetricsReportingService.processAccountMetricEvent(transactionCompletedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(10000025L);
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue(); 
        
        assertNull(actual.getDynamicComplianceStatus());
        assertEquals(transactionCompletedEvent.getAcquiringAccountIdentifier(), actual.getIdentifier());
        assertEquals(transactionQuantityReversedSurrendered , actual.getQuantityReversedSurrendered());
        assertNull(actual.getQuantitySurrendered());
        assertNull(actual.getTotalVerifiedEmissions());
        assertEquals(-transactionQuantityReversedSurrendered , actual.getSurrenderBalance());
        

    }  
    
    @ParameterizedTest
    @EnumSource(
      value = TransactionType.class,
      names = {"SurrenderAllowances", "ReverseSurrenderAllowances"},
      mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Verify that upon any other transaction except (reverse) surrender ones nothing happens.")
    void testNonSurrenderedTransactionCompletedEvent(TransactionType transactionType) {
    
    	final Long transactionQuantity = 450L;
    	final UUID eventId = UUID.randomUUID();
    	
    	TransactionFinalisationEvent transactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventId)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(TransactionStatus.COMPLETED)
    	.transactionType(transactionType)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantity)
    	.build();
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(transactionCompletedEvent.getEventId())).thenReturn(
                Optional.empty());
        
        accountMetricsReportingService.processAccountMetricEvent(transactionCompletedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verifyNoInteractions(accountMetricsRepository);     

    }
    
    @ParameterizedTest
    @EnumSource(
    	      value = TransactionStatus.class,
    	      names = {"COMPLETED"},
    	      mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Verify that upon any surrender with status other than COMPLETED nothing happens.")
    void testSurrenderedTransactionNonCompletedEvent(TransactionStatus transactionStatus) {
    
    	final Long transactionQuantity = 450L;
    	final UUID eventId = UUID.randomUUID();
    	
    	TransactionFinalisationEvent transactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventId)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(transactionStatus)
    	.transactionType(TransactionType.SurrenderAllowances)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantity)
    	.build();

        Mockito.when(processedAccountMetricsEventRepository.findByEventId(transactionCompletedEvent.getEventId())).thenReturn(
                Optional.empty());
        
        accountMetricsReportingService.processAccountMetricEvent(transactionCompletedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verifyNoInteractions(accountMetricsRepository);     

    }    
    
    @ParameterizedTest
    @EnumSource(
    	      value = TransactionStatus.class,
    	      names = {"COMPLETED"},
    	      mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Verify that upon any reverse surrender with status other than COMPLETED nothing happens.")
    void testReversedSurrenderedTransactionNonCompletedEvent(TransactionStatus transactionStatus) {
    
    	final Long transactionQuantity = 450L;
    	final UUID eventId = UUID.randomUUID();
    	
    	TransactionFinalisationEvent transactionCompletedEvent = TransactionFinalisationEvent
    	.builder()
    	.eventId(eventId)
    	.transactionIdentifier("UK887778")
    	.transactionStatus(transactionStatus)
    	.transactionType(TransactionType.ReverseSurrenderAllowances)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(10000025L)
    	.amount(transactionQuantity)
    	.build();

        accountMetricsReportingService.processAccountMetricEvent(transactionCompletedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verifyNoInteractions(accountMetricsRepository);     

    }    
    
    
    @Test
    @DisplayName("Verify that upon an Emissions updated event metrics are updated correctly for an existing in metrics account.")
    void testEmissionsUpdatedEventEvent() {
    
    	final ComplianceStatus initialDynamicComplianceStatus = ComplianceStatus.NOT_APPLICABLE;
    	final Long oldEmissionsValue = 400L;
    	final Long newEmissionsValue = 1200L;
    	final Long initialQuantitySurrendered = 120L;
    	final Long initialQuantityReversedSurrendered = null;
    	final Long initialTotalVerifiedEmissions = 3400L;
    	final Long expectedTotalEmissions = initialTotalVerifiedEmissions + newEmissionsValue - oldEmissionsValue;
    	final UUID eventId = UUID.randomUUID();
    	
    	EmissionsUpdatedEvent emissionsUpdatedEvent = EmissionsUpdatedEvent
    	.builder()
    	.eventId(eventId)
    	.accountIdentifier(10000081L)
    	.year(Year.of(2025))
    	.oldEmissionsValue(oldEmissionsValue)
    	.newEmissionsValue(newEmissionsValue)
    	.build();

    	AccountMetrics oldAccountMetrics = new AccountMetrics();
    	oldAccountMetrics.setDynamicComplianceStatus(initialDynamicComplianceStatus);
    	oldAccountMetrics.setIdentifier(emissionsUpdatedEvent.getAccountIdentifier());
    	oldAccountMetrics.setQuantityReversedSurrendered(initialQuantityReversedSurrendered);
    	oldAccountMetrics.setQuantitySurrendered(initialQuantitySurrendered);
    	oldAccountMetrics.setTotalVerifiedEmissions(initialTotalVerifiedEmissions);
    	oldAccountMetrics.setSurrenderBalance(initialQuantitySurrendered - initialTotalVerifiedEmissions);
    	
    	AccountMetrics expectedAccountMetrics = new AccountMetrics();
    	expectedAccountMetrics.setDynamicComplianceStatus(initialDynamicComplianceStatus);
    	expectedAccountMetrics.setIdentifier(emissionsUpdatedEvent.getAccountIdentifier());
    	expectedAccountMetrics.setQuantityReversedSurrendered(initialQuantityReversedSurrendered);
    	expectedAccountMetrics.setQuantitySurrendered(initialQuantitySurrendered);
    	expectedAccountMetrics.setTotalVerifiedEmissions(expectedTotalEmissions);
    	expectedAccountMetrics.setSurrenderBalance(initialQuantitySurrendered - expectedTotalEmissions);
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(emissionsUpdatedEvent.getEventId())).thenReturn(
                Optional.empty());
        Mockito.when(accountMetricsRepository.findByIdentifier(emissionsUpdatedEvent.getAccountIdentifier())).thenReturn(
            Optional.of(oldAccountMetrics));
        
        accountMetricsReportingService.processAccountMetricEvent(emissionsUpdatedEvent);
        
        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(emissionsUpdatedEvent.getAccountIdentifier());
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue();
        
        assertEquals(initialDynamicComplianceStatus , actual.getDynamicComplianceStatus());
        assertEquals(initialQuantitySurrendered,actual.getQuantitySurrendered());
        assertNull(actual.getQuantityReversedSurrendered());
        assertEquals(initialTotalVerifiedEmissions + newEmissionsValue - oldEmissionsValue , actual.getTotalVerifiedEmissions());
        assertEquals(initialQuantitySurrendered - expectedTotalEmissions , actual.getSurrenderBalance());
    }
    
    @Test
    @DisplayName("Verify that upon an Emissions updated event metrics are updated correctly for a new account.")
    void testEmissionsUpdatedEventForNewAccount() {
    
    	final Long newEmissionsValue = 1200L;
    	final UUID eventId = UUID.randomUUID();
    	
    	EmissionsUpdatedEvent emissionsUpdatedEvent = EmissionsUpdatedEvent
    	.builder()
    	.eventId(eventId)
    	.accountIdentifier(10000081L)
    	.year(Year.of(2025))
    	.oldEmissionsValue(null)
    	.newEmissionsValue(newEmissionsValue)
    	.build();  	
    	
        Mockito.when(accountMetricsRepository.findByIdentifier(emissionsUpdatedEvent.getAccountIdentifier())).thenReturn(
            Optional.empty());
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(emissionsUpdatedEvent.getEventId())).thenReturn(
                Optional.empty());
        
        accountMetricsReportingService.processAccountMetricEvent(emissionsUpdatedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(emissionsUpdatedEvent.getAccountIdentifier());
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue();
        
        assertNull(actual.getDynamicComplianceStatus());
        assertEquals(emissionsUpdatedEvent.getAccountIdentifier(), actual.getIdentifier());
        assertNull(actual.getQuantityReversedSurrendered());
        assertNull(actual.getQuantitySurrendered());
        assertEquals(newEmissionsValue, actual.getTotalVerifiedEmissions());
        assertEquals(-newEmissionsValue , actual.getSurrenderBalance());
        

    } 
    
    @Test
    @DisplayName("Verify that upon an dynamic compliance status calculated event metrics are updated correctly for a new account.")
    void testDynamicComplianceStatusCalculatedEventForNewAccount() {
    
    	final ComplianceStatus newComplianceStatus = ComplianceStatus.C;
    	final UUID eventId = UUID.randomUUID();
    	
    	DynamicComplianceStatusCalculatedEvent dynamicComplianceStatusCalculatedEvent = DynamicComplianceStatusCalculatedEvent
    	.builder()
    	.eventId(eventId)
    	.accountIdentifier(10000081L)
    	.dynamicComplianceStatus(newComplianceStatus)
    	.build();  	
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(dynamicComplianceStatusCalculatedEvent.getEventId())).thenReturn(
                Optional.empty());
        Mockito.when(accountMetricsRepository.findByIdentifier(dynamicComplianceStatusCalculatedEvent.getAccountIdentifier())).thenReturn(
            Optional.empty());

        accountMetricsReportingService.processAccountMetricEvent(dynamicComplianceStatusCalculatedEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(dynamicComplianceStatusCalculatedEvent.getAccountIdentifier());
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue();
        
        assertEquals(newComplianceStatus,actual.getDynamicComplianceStatus());
        assertEquals(dynamicComplianceStatusCalculatedEvent.getAccountIdentifier(), actual.getIdentifier());
        assertNull(actual.getQuantityReversedSurrendered());
        assertNull(actual.getQuantitySurrendered());
        assertNull(actual.getTotalVerifiedEmissions());
        assertNull(actual.getSurrenderBalance());
    }  
    
    
    @Test
    @DisplayName("Verify that upon an installation transfer event metrics are updated correctly.")
    void testInstallationTransferEvent() {
    
    	final ComplianceStatus initialDynamicComplianceStatus = ComplianceStatus.A;
    	final Long initialQuantitySurrendered = 120L;
    	final Long initialQuantityReversedSurrendered = null;
    	final Long initialTotalVerifiedEmissions = 3400L;
    	final UUID eventId = UUID.randomUUID();
    	
    	InstallationTransferEvent installationTransferEvent = InstallationTransferEvent
    	.builder()
    	.eventId(eventId)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(299400081L)
    	.build();

    	AccountMetrics oldAccountMetrics = new AccountMetrics();
    	oldAccountMetrics.setDynamicComplianceStatus(initialDynamicComplianceStatus);
    	oldAccountMetrics.setIdentifier(installationTransferEvent.getTransferringAccountIdentifier());
    	oldAccountMetrics.setQuantityReversedSurrendered(initialQuantityReversedSurrendered);
    	oldAccountMetrics.setQuantitySurrendered(initialQuantitySurrendered);
    	oldAccountMetrics.setTotalVerifiedEmissions(initialTotalVerifiedEmissions);
    	oldAccountMetrics.setSurrenderBalance(initialQuantitySurrendered - initialTotalVerifiedEmissions);
    	
    	AccountMetrics expectedAccountMetrics = new AccountMetrics();
    	expectedAccountMetrics.setDynamicComplianceStatus(initialDynamicComplianceStatus);
    	expectedAccountMetrics.setIdentifier(installationTransferEvent.getAcquiringAccountIdentifier());
    	expectedAccountMetrics.setQuantityReversedSurrendered(initialQuantityReversedSurrendered);
    	expectedAccountMetrics.setQuantitySurrendered(initialQuantitySurrendered);
    	expectedAccountMetrics.setTotalVerifiedEmissions(initialTotalVerifiedEmissions);
    	expectedAccountMetrics.setSurrenderBalance(initialQuantitySurrendered - initialTotalVerifiedEmissions);
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(installationTransferEvent.getEventId())).thenReturn(
                Optional.empty());
        Mockito.when(accountMetricsRepository.findByIdentifier(installationTransferEvent.getTransferringAccountIdentifier())).thenReturn(
            Optional.of(oldAccountMetrics));
        
        accountMetricsReportingService.processAccountMetricEvent(installationTransferEvent);
        
        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(installationTransferEvent.getTransferringAccountIdentifier());
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue();
        
        assertEquals(installationTransferEvent.getAcquiringAccountIdentifier() , actual.getIdentifier());
        assertEquals(initialDynamicComplianceStatus , actual.getDynamicComplianceStatus());
        assertEquals(initialQuantitySurrendered,actual.getQuantitySurrendered());
        assertNull(actual.getQuantityReversedSurrendered());
        assertEquals(initialTotalVerifiedEmissions, actual.getTotalVerifiedEmissions());
        assertEquals(initialQuantitySurrendered - initialTotalVerifiedEmissions, actual.getSurrenderBalance());
    }
    
    @Test
    @DisplayName("Verify that upon an installation transfer event metrics are updated correctly for a new account.")
    void testInstallationTransferEventForNewAccount() {
    	
    	final UUID eventId = UUID.randomUUID();
    	
    	InstallationTransferEvent installationTransferEvent = InstallationTransferEvent
    	.builder()
    	.eventId(eventId)
    	.transferringAccountIdentifier(10000081L)
    	.acquiringAccountIdentifier(299400081L)
    	.build();  	
    	
        Mockito.when(processedAccountMetricsEventRepository.findByEventId(installationTransferEvent.getEventId())).thenReturn(
                Optional.empty());
        Mockito.when(accountMetricsRepository.findByIdentifier(installationTransferEvent.getTransferringAccountIdentifier())).thenReturn(
            Optional.empty());

        accountMetricsReportingService.processAccountMetricEvent(installationTransferEvent);

        verify(processedAccountMetricsEventRepository).findByEventId(eventId);
        verify(accountMetricsRepository).findByIdentifier(installationTransferEvent.getTransferringAccountIdentifier());
        ArgumentCaptor<AccountMetrics> captor = ArgumentCaptor.forClass(AccountMetrics.class);
        verify(accountMetricsRepository).save(captor.capture());
        AccountMetrics actual = captor.getValue();
        
        assertNull(actual.getDynamicComplianceStatus());
        assertEquals(installationTransferEvent.getAcquiringAccountIdentifier(), actual.getIdentifier());
        assertNull(actual.getQuantityReversedSurrendered());
        assertNull(actual.getQuantitySurrendered());
        assertNull(actual.getTotalVerifiedEmissions());
        assertNull(actual.getSurrenderBalance());
    }
}
