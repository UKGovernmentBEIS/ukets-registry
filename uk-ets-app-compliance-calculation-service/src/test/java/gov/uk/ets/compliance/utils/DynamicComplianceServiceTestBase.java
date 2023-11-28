package gov.uk.ets.compliance.utils;

import gov.uk.ets.compliance.domain.events.ChangeYearEvent;
import gov.uk.ets.compliance.domain.events.CompliantEntityInitializationEvent;
import gov.uk.ets.compliance.domain.events.ExclusionEvent;
import gov.uk.ets.compliance.domain.events.ExclusionReversalEvent;
import gov.uk.ets.compliance.domain.events.GetCurrentDynamicStatusEvent;
import gov.uk.ets.compliance.domain.events.SurrenderEvent;
import gov.uk.ets.compliance.domain.events.SurrenderReversalEvent;
import gov.uk.ets.compliance.domain.events.UpdateFirstYearOfVerifiedEmissionsEvent;
import gov.uk.ets.compliance.domain.events.UpdateLastYearOfVerifiedEmissionsEvent;
import gov.uk.ets.compliance.domain.events.UpdateOfVerifiedEmissionsEvent;
import gov.uk.ets.compliance.domain.events.base.StaticComplianceRequestEvent;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * An integration test using in memory h2 database. the creation of the tables performed by hibernate
 * as configured in application-test.properties
 */
@ActiveProfiles("test")
@SpringBootTest
public abstract class DynamicComplianceServiceTestBase {

    private final Long testCompliantEntityId = 100L;

    protected CompliantEntityInitializationEvent compliantEntityInitializationEvent(int firstYearOfVerifiedEmissions,
                                                        Integer lastYearOfVerifiedEmissions,
                                                        int currentYear) {

        return compliantEntityInitializationEvent(firstYearOfVerifiedEmissions, lastYearOfVerifiedEmissions, currentYear, LocalDateTime.now());
    }

    protected CompliantEntityInitializationEvent compliantEntityInitializationEvent(int firstYearOfVerifiedEmissions,
                                                                                    Integer lastYearOfVerifiedEmissions,
                                                                                    int currentYear,
                                                                                    LocalDateTime dateRequested) {

        return CompliantEntityInitializationEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateTriggered(LocalDateTime.now())
            .dateRequested(dateRequested)
            .firstYearOfVerifiedEmissions(firstYearOfVerifiedEmissions)
            .lastYearOfVerifiedEmissions(lastYearOfVerifiedEmissions)
            .currentYear(currentYear)
            .build();
    }

    protected ChangeYearEvent changeYear() {
        return changeYear(LocalDateTime.now());
    }

    protected ChangeYearEvent changeYear(LocalDateTime dateRequested) {
        return ChangeYearEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateRequested(dateRequested)
            .dateTriggered(LocalDateTime.now())
            .build();
    }

    protected UpdateFirstYearOfVerifiedEmissionsEvent updateFirstYearOfVerifiedEmissionsEvent(
        UUID eventId,
        int firstYearOfVerifiedEmissions) {
        return updateFirstYearOfVerifiedEmissionsEvent(eventId, firstYearOfVerifiedEmissions, LocalDateTime.now());
    }

    protected UpdateFirstYearOfVerifiedEmissionsEvent updateFirstYearOfVerifiedEmissionsEvent(
        int firstYearOfVerifiedEmissions,
        LocalDateTime dateRequested) {
        return updateFirstYearOfVerifiedEmissionsEvent(UUID.randomUUID(), firstYearOfVerifiedEmissions, dateRequested);
    }

    protected UpdateFirstYearOfVerifiedEmissionsEvent updateFirstYearOfVerifiedEmissionsEvent(
        UUID eventId,
        int firstYearOfVerifiedEmissions,
        LocalDateTime dateRequested) {
        return
            UpdateFirstYearOfVerifiedEmissionsEvent.builder()
                .eventId(eventId)
                .compliantEntityId(testCompliantEntityId)
                .dateTriggered(LocalDateTime.now())
                .dateRequested(dateRequested)
                .firstYearOfVerifiedEmissions(firstYearOfVerifiedEmissions)
                .build();
    }

    protected UpdateLastYearOfVerifiedEmissionsEvent updateLastYearOfVerifiedEmissionsEvent(
        Integer lastYearOfVerifiedEmissions) {
        return updateLastYearOfVerifiedEmissionsEvent(lastYearOfVerifiedEmissions, LocalDateTime.now());
    }

    protected UpdateLastYearOfVerifiedEmissionsEvent updateLastYearOfVerifiedEmissionsEvent(
        Integer lastYearOfVerifiedEmissions,
        LocalDateTime dateRequested) {
        return UpdateLastYearOfVerifiedEmissionsEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateTriggered(LocalDateTime.now())
            .dateRequested(dateRequested)
            .lastYearOfVerifiedEmissions(lastYearOfVerifiedEmissions)
            .build();
    }

    protected UpdateOfVerifiedEmissionsEvent updateOfVerifiedEmissionsEvent(int year, Long verifiedEmissions) {
        return updateOfVerifiedEmissionsEvent(year, verifiedEmissions, LocalDateTime.now());
    }

    protected UpdateOfVerifiedEmissionsEvent updateOfVerifiedEmissionsEvent(int year, Long verifiedEmissions, LocalDateTime dateRequested) {
        return UpdateOfVerifiedEmissionsEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateTriggered(LocalDateTime.now())
            .dateRequested(dateRequested)
            .year(year)
            .verifiedEmissions(verifiedEmissions)
            .build();
    }

    protected ExclusionReversalEvent exclusionReversalEvent(int year) {
        return ExclusionReversalEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateTriggered(LocalDateTime.now())
            .dateRequested(LocalDateTime.now())
            .year(year)
            .build();
    }

    protected ExclusionEvent exclusionEvent(int year) {
        return exclusionEvent(year, LocalDateTime.now());
    }

    protected ExclusionEvent exclusionEvent(int year, LocalDateTime dateRequested) {
        return ExclusionEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateTriggered(LocalDateTime.now())
            .dateRequested(dateRequested)
            .year(year)
            .build();
    }

    protected SurrenderEvent surrenderEvent(long amount) {
        return surrenderEvent(amount, LocalDateTime.now());
    }

    protected SurrenderEvent surrenderEvent(long amount, LocalDateTime dateRequested) {
        return SurrenderEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateTriggered(LocalDateTime.now())
            .dateRequested(dateRequested)
            .amount(amount)
            .build();
    }

    protected SurrenderReversalEvent surrenderReversalEvent(long amount) {
        return surrenderReversalEvent(amount, LocalDateTime.now());
    }

    protected SurrenderReversalEvent surrenderReversalEvent(long amount, LocalDateTime dateRequested) {
        return SurrenderReversalEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateTriggered(LocalDateTime.now())
            .dateRequested(dateRequested)
            .amount(amount)
            .build();
    }
    
    protected StaticComplianceRequestEvent staticComplianceRequestEvent(LocalDateTime dateRequested) {
        return StaticComplianceRequestEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateRequested(dateRequested)
            .dateTriggered(LocalDateTime.now())
            .build();
    }
    
    protected GetCurrentDynamicStatusEvent getCurrentDynamicStatusEvent(LocalDateTime dateRequested) {
        return GetCurrentDynamicStatusEvent.builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(testCompliantEntityId)
            .dateRequested(dateRequested)
            .dateTriggered(LocalDateTime.now())
            .build();
    }
}
