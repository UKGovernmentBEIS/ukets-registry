package gov.uk.ets.registry.api.integration.service.emission;

import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.emissionstable.messaging.UpdateOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.integration.changelog.service.EmissionAuditService;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;

@ExtendWith(MockitoExtension.class)
class EmissionEventServiceTest {

    @Mock
    private EmissionsEntryRepository emissionsEntryRepository;
    @Mock
    private ComplianceEventService complianceEventService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private EventService eventService;
    @Mock
    private EmissionEventValidator validator;
    @Mock
    private EmissionAuditService emissionAuditService;

    private EmissionEventService service;

    @BeforeEach
    public void setup() {
        service = new EmissionEventService(emissionsEntryRepository, complianceEventService,
            accountRepository, eventService, validator, emissionAuditService);
    }

    @Test
    void testProcessEvent() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2024));

        Account account = new Account();
        account.setIdentifier(654321L);

        Mockito.when(validator.validate(event)).thenReturn(List.of());
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(123456L, 2024L))
            .thenReturn(List.of());
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123456L))
            .thenReturn(Optional.of(account));

        // when
        service.process(event, Map.of(), SourceSystem.METSIA_INSTALLATION);

        // then
        Mockito.verify(emissionsEntryRepository, Mockito.times(1)).save(any(EmissionsEntry.class));
        Mockito.verify(complianceEventService, Mockito.times(1))
            .processEvent(any(UpdateOfVerifiedEmissionsEvent.class));

        Mockito.verify(eventService, Mockito.times(1))
            .createAndPublishEvent("123456",
                null,
                "Reporting year: 2024, Old value: No emissions, Updated value: 123",
                EventType.APPROVE_COMPLIANT_ENTITY_EMISSIONS,
                "Emissions update event");
        Mockito.verify(eventService, Mockito.times(1))
            .createAndPublishEvent("654321",
                null,
                "Reporting year: 2024, Old value: No emissions, Updated value: 123",
                EventType.APPROVE_ACCOUNT_EMISSIONS,
                "Emissions update event");
    }

    @Test
    void testProcessEvent_EventWithoutNewData() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2024));

        EmissionsEntry existingEntry = new EmissionsEntry();
        existingEntry.setEmissions(123L);

        Mockito.when(validator.validate(event)).thenReturn(List.of());

        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(123456L, 2024L))
            .thenReturn(List.of(existingEntry));

        // when
        service.process(event, Map.of(), SourceSystem.METSIA_INSTALLATION);

        // then
        Mockito.verify(emissionsEntryRepository, Mockito.times(0)).save(any(EmissionsEntry.class));
        Mockito.verify(complianceEventService, Mockito.times(0))
            .processEvent(any(UpdateOfVerifiedEmissionsEvent.class));
    }
}
