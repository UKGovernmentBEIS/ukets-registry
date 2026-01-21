package gov.uk.ets.registry.api.integration.service.exemption;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.integration.changelog.service.ExemptionAuditService;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

@ExtendWith(MockitoExtension.class)
public class ExemptionEventServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ExcludeEmissionsRepository excludeEmissionsRepository;
    @Mock
    private ComplianceService complianceService;
    @Mock
    private ExemptionEventValidator validator;
    @Mock
    private ExemptionAuditService auditService;

    private ExemptionEventService service;

    @BeforeEach
    public void setup() {
        service = new ExemptionEventService(accountRepository, excludeEmissionsRepository,
                complianceService, validator, auditService);
    }

    @Test
    void testProcessEvent() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123456L);
        event.setExemptionFlag(true);
        event.setReportingYear(Year.of(2024));

        Account account = new Account();
        account.setIdentifier(654321L);

        Mockito.when(validator.validate(event)).thenReturn(List.of());
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityIdAndYear(123456L, 2024L))
                .thenReturn(null);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123456L))
                .thenReturn(Optional.of(account));

        // when
        service.process(event, Map.of(), SourceSystem.METSIA_AVIATION);

        // then
        Mockito.verify(complianceService, Mockito.times(1))
                .updateExclusionStatus(654321L, 2024L, true);
    }

    @Test
    void testProcessEventWithAlreadyExcludedYear() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123456L);
        event.setExemptionFlag(true);
        event.setReportingYear(Year.of(2024));

        ExcludeEmissionsEntry existingEntry = new ExcludeEmissionsEntry();
        existingEntry.setExcluded(true);

        Mockito.when(validator.validate(event)).thenReturn(List.of());
        Mockito.when(excludeEmissionsRepository.findByCompliantEntityIdAndYear(123456L, 2024L))
                .thenReturn(existingEntry);

        // when
        service.process(event, Map.of(), SourceSystem.METSIA_INSTALLATION);

        // then
        Mockito.verify(accountRepository, Mockito.times(0)).findByCompliantEntityIdentifier(any());
        Mockito.verify(complianceService, Mockito.times(0)).updateExclusionStatus(any(), any(), anyBoolean());
    }
}
