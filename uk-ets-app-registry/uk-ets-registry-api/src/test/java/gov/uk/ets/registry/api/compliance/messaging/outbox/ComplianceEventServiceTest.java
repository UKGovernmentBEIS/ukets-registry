package gov.uk.ets.registry.api.compliance.messaging.outbox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import gov.uk.ets.registry.api.account.messaging.CompliantEntityInitializationEvent;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.compliance.repository.StaticComplianceStatusRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ComplianceEventServiceTest {

    private static final Long TEST_COMPLIANT_ENTITY_ID = 123L;
    private static final String TEST_ACTOR_ID = "UK123456789";
    @Mock
    ComplianceOutboxService service;
    @Mock
    StaticComplianceStatusRepository staticComplianceStatusRepository;

    @InjectMocks
    ComplianceEventService cut;

    @Test
    public void shouldSendValidMessage() {
        CompliantEntityInitializationEvent event = CompliantEntityInitializationEvent.builder()
            .compliantEntityId(TEST_COMPLIANT_ENTITY_ID)
            .actorId(TEST_ACTOR_ID)
            .dateTriggered(LocalDateTime.now(ZoneId.of("UTC")))
            .dateRequested(LocalDateTime.now(ZoneId.of("UTC")))
            .firstYearOfVerifiedEmissions(2021)
            .currentYear(2020)
            .build();

        cut.processEvent(event);

        verify(service, times(1)).create(event);
    }

    @Test
    public void shouldThrowAndNotSendMessageWhenInvalid() {

        CompliantEntityInitializationEvent event = CompliantEntityInitializationEvent.builder()
            .actorId(TEST_ACTOR_ID)
            .dateTriggered(LocalDateTime.now(ZoneId.of("UTC")))
            .build();

        assertThrows(IllegalStateException.class, () -> cut.processEvent(event));

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("should not skip static compliance calculation if LYVE has not passed")
    public void shouldNotSkipIfLyveHasNotPassed() {

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2020);
        compliantEntity.setEndYear(2022);
        compliantEntity.setIdentifier(TEST_COMPLIANT_ENTITY_ID);

        boolean shouldSkip = cut.skipStaticComplianceStatusRequestForEntity(compliantEntity, 2021);


        assertThat(shouldSkip).isFalse();

        verifyNoInteractions(staticComplianceStatusRepository);

    }

    @Test
    @DisplayName("should not skip static compliance calculation for the LYVE reporting year")
    public void shouldNotSkipForLyve() {

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(2022);
        compliantEntity.setIdentifier(TEST_COMPLIANT_ENTITY_ID);

        boolean shouldSkip = cut.skipStaticComplianceStatusRequestForEntity(compliantEntity, 2022);


        assertThat(shouldSkip).isFalse();

        verifyNoInteractions(staticComplianceStatusRepository);

    }

    @Test
    @DisplayName("should skip static compliance calculation for year < FYVE +1 reporting year")
    public void shouldSkipForFyve() {

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(2022);
        compliantEntity.setIdentifier(TEST_COMPLIANT_ENTITY_ID);

        boolean shouldSkip = cut.skipStaticComplianceStatusRequestForEntity(compliantEntity, 2021);


        assertThat(shouldSkip).isTrue();

        verifyNoInteractions(staticComplianceStatusRepository);

    }    
    
    @Test
    @DisplayName("should not skip static compliance calculation If LYVE has passed and previous status was B")
    public void shouldNotSkipIfLyveHasPassedAndPreviousStatusWasB() {
        StaticComplianceStatus status = new StaticComplianceStatus();
        status.setComplianceStatus(ComplianceStatus.B);
        when(staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearGreaterThanEqual(TEST_COMPLIANT_ENTITY_ID,
            2019L)).thenReturn(List.of(status));

        Installation compliantEntity = new Installation();
        compliantEntity.setEndYear(2019);
        compliantEntity.setIdentifier(TEST_COMPLIANT_ENTITY_ID);

        boolean shouldSkip = cut.skipStaticComplianceStatusRequestForEntity(compliantEntity, 2021);


        assertThat(shouldSkip).isFalse();
        // make sure it is not skipped because of LYVE but because of previous compliance status
        verify(staticComplianceStatusRepository, times(1)).findByCompliantEntityIdentifierAndYearGreaterThanEqual(
            TEST_COMPLIANT_ENTITY_ID, 2019L);
    }

    @Test
    @DisplayName("should not skip static compliance calculation If LYVE has passed and previous status was null")
    public void shouldNotSkipIfLyveHasPassedAndPreviousStatusWasNull() {
        when(staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearGreaterThanEqual(TEST_COMPLIANT_ENTITY_ID,
            2019L)).thenReturn(List.of());

        Installation compliantEntity = new Installation();
        compliantEntity.setEndYear(2019);
        compliantEntity.setIdentifier(TEST_COMPLIANT_ENTITY_ID);

        boolean shouldSkip = cut.skipStaticComplianceStatusRequestForEntity(compliantEntity, 2021);


        assertThat(shouldSkip).isFalse();
        // make sure it is not skipped because of LYVE but because of previous compliance status
        verify(staticComplianceStatusRepository, times(1)).findByCompliantEntityIdentifierAndYearGreaterThanEqual(
            TEST_COMPLIANT_ENTITY_ID, 2019L);
    }

    @Test
    @DisplayName("should skip static compliance calculation If LYVE has passed and previous status was A")
    public void shouldSkipIfLyveHasPassedAndPreviousStatusWasA() {
        StaticComplianceStatus status = new StaticComplianceStatus();
        status.setComplianceStatus(ComplianceStatus.A);
        when(staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearGreaterThanEqual(TEST_COMPLIANT_ENTITY_ID,
            2019L)).thenReturn(List.of(status));

        Installation compliantEntity = new Installation();
        compliantEntity.setEndYear(2019);
        compliantEntity.setIdentifier(TEST_COMPLIANT_ENTITY_ID);

        boolean shouldSkip = cut.skipStaticComplianceStatusRequestForEntity(compliantEntity, 2021);


        assertThat(shouldSkip).isTrue();
        // make sure it is not skipped because of LYVE but because of previous compliance status
        verify(staticComplianceStatusRepository, times(1)).findByCompliantEntityIdentifierAndYearGreaterThanEqual(
            TEST_COMPLIANT_ENTITY_ID, 2019L);
    }

    @Test
    @DisplayName("should skip static compliance calculation If LYVE has passed, previous year status does not exist and last status was A")
    public void shouldSkipIfLyveHasPassedPreviousYearStatusWasNullAndLastStatusWasA() {
        StaticComplianceStatus status2021 = new StaticComplianceStatus();
        status2021.setYear(2021L);
        status2021.setComplianceStatus(ComplianceStatus.C);
        StaticComplianceStatus status2022 = new StaticComplianceStatus();
        status2022.setYear(2022L);
        status2022.setComplianceStatus(ComplianceStatus.A);
        when(staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearGreaterThanEqual(TEST_COMPLIANT_ENTITY_ID,
            2021L)).thenReturn(List.of(status2022, status2021));

        Installation compliantEntity = new Installation();
        compliantEntity.setEndYear(2021);
        compliantEntity.setIdentifier(TEST_COMPLIANT_ENTITY_ID);

        boolean shouldSkip = cut.skipStaticComplianceStatusRequestForEntity(compliantEntity, 2025);


        assertThat(shouldSkip).isTrue();
        // make sure it is not skipped because of LYVE but because of previous compliance status
        verify(staticComplianceStatusRepository, times(1)).findByCompliantEntityIdentifierAndYearGreaterThanEqual(
            TEST_COMPLIANT_ENTITY_ID, 2021L);
    }
}
