package gov.uk.ets.compliance.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.compliance.outbox.ComplianceOutbox;
import gov.uk.ets.compliance.outbox.ComplianceOutboxRepository;
import gov.uk.ets.compliance.outbox.ComplianceOutboxStatus;
import gov.uk.ets.compliance.outbox.ComplianceOutgoingEventType;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;
import gov.uk.ets.compliance.utils.DynamicComplianceServiceTestBase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ComplianceEventListenerTest extends DynamicComplianceServiceTestBase {

    @Autowired
    ComplianceEventListener complianceEventListener;

    @Autowired
    ComplianceOutboxRepository complianceOutboxRepository;

    @Autowired
    DynamicComplianceRepository dynamicComplianceRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    private void setup() {
        complianceOutboxRepository.deleteAll();
        dynamicComplianceRepository.deleteAll();
    }

    @Test
    @DisplayName("Calculation error - negative year")
    void testProcessComplianceOutcome_validation_error1() throws JsonProcessingException {
        complianceEventListener.processComplianceOutcome(compliantEntityInitializationEvent(-2015,
            2019, 2020));
        List<ComplianceOutbox> outboxMessages =
            complianceOutboxRepository.findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus.PENDING);
        assertEquals(1, outboxMessages.size());
        assertEquals(ComplianceOutgoingEventType.CALCULATION_ERROR, outboxMessages.get(0).getType());
        assertEquals("Incoming compliance event fields validation error",
            objectMapper.readValue(outboxMessages.get(0).getPayload(), ComplianceCalculationErrorEvent.class).getMessage());
    }

    @Test
    @DisplayName("Calculation error - null event id")
    void testProcessComplianceOutcome_validation_error2() throws JsonProcessingException {
        complianceEventListener.processComplianceOutcome(updateFirstYearOfVerifiedEmissionsEvent(null, 2019));
        List<ComplianceOutbox> outboxMessages =
            complianceOutboxRepository.findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus.PENDING);
        assertEquals(1, outboxMessages.size());
        assertEquals(ComplianceOutgoingEventType.CALCULATION_ERROR, outboxMessages.get(0).getType());
        assertEquals("Incoming compliance event fields validation error",
            objectMapper.readValue(outboxMessages.get(0).getPayload(), ComplianceCalculationErrorEvent.class).getMessage());
    }

    @Test
    @DisplayName("Calculation error - duplicate event id")
    void testProcessComplianceOutcome_duplicate_event() throws JsonProcessingException {
        UUID eventId = UUID.randomUUID();
        complianceEventListener.processComplianceOutcome(compliantEntityInitializationEvent(2015,
            2019, 2020));
        complianceEventListener.processComplianceOutcome(updateFirstYearOfVerifiedEmissionsEvent(eventId, 2019));
        complianceEventListener.processComplianceOutcome(updateFirstYearOfVerifiedEmissionsEvent(eventId, 2019));
        List<ComplianceOutbox> outboxMessages =
            complianceOutboxRepository.findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus.PENDING);
        assertEquals(3, outboxMessages.size());
        assertEquals(ComplianceOutgoingEventType.CALCULATION, outboxMessages.get(0).getType());
        assertEquals(ComplianceOutgoingEventType.CALCULATION, outboxMessages.get(1).getType());
        assertEquals(ComplianceOutgoingEventType.CALCULATION_ERROR, outboxMessages.get(2).getType());
        assertEquals("Event has already been processed",
            objectMapper.readValue(outboxMessages.get(2).getPayload(), ComplianceCalculationErrorEvent.class).getMessage());
    }

    @Test
    @DisplayName("Calculation error - no existing state for compliant entity id")
    void testProcessComplianceOutcome_no_existing_state() throws JsonProcessingException {
        complianceEventListener.processComplianceOutcome(updateFirstYearOfVerifiedEmissionsEvent(UUID.randomUUID(), 2019));
        List<ComplianceOutbox> outboxMessages =
            complianceOutboxRepository.findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus.PENDING);
        assertEquals(1, outboxMessages.size());
        assertEquals(ComplianceOutgoingEventType.CALCULATION_ERROR, outboxMessages.get(0).getType());
        assertEquals("Dynamic Compliance not found for compliant entity id: 100",
            objectMapper.readValue(outboxMessages.get(0).getPayload(), ComplianceCalculationErrorEvent.class).getMessage());
    }

    @Test
    @DisplayName("Calculation error - invalid state")
    void testProcessComplianceOutcome_invalid_state() throws JsonProcessingException {
        complianceEventListener.processComplianceOutcome(compliantEntityInitializationEvent(2021,
            2022, 2021));
        complianceEventListener.processComplianceOutcome(updateOfVerifiedEmissionsEvent(2021, 100L));
        complianceEventListener.processComplianceOutcome(exclusionEvent(2021));
        List<ComplianceOutbox> outboxMessages =
            complianceOutboxRepository.findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus.PENDING);
        assertEquals(3, outboxMessages.size());
        assertEquals(ComplianceOutgoingEventType.CALCULATION_ERROR, outboxMessages.get(2).getType());
        assertEquals("Event not used by compliance calculation engine. Ignoring event.",
            objectMapper.readValue(outboxMessages.get(2).getPayload(), ComplianceCalculationErrorEvent.class).getMessage());
    }

    @Test
    @DisplayName("getCurrentDynamicStatusEvent")
    void getCurrentDynamicStatusEvent() throws JsonProcessingException {
        complianceEventListener.processComplianceOutcome(getCurrentDynamicStatusEvent(LocalDateTime.now()));
        List<ComplianceOutbox> outboxMessages =
            complianceOutboxRepository.findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus.PENDING);
        assertEquals(1, outboxMessages.size());
        assertEquals(ComplianceOutgoingEventType.CALCULATION_ERROR, outboxMessages.get(0).getType());
        assertEquals("Dynamic Compliance not found for compliant entity id: 100",
            objectMapper.readValue(outboxMessages.get(0).getPayload(), ComplianceCalculationErrorEvent.class).getMessage());
    }
}
