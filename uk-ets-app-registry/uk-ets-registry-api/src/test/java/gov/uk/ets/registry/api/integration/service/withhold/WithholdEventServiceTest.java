package gov.uk.ets.registry.api.integration.service.withhold;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.service.AccountAllocationService;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.integration.changelog.service.WithholdAuditService;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class WithholdEventServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountAllocationService accountAllocationService;
    @Mock
    private AllocationStatusRepository allocationStatusRepository;
    @Mock
    private WithholdEventValidator validator;
    @Mock
    private WithholdAuditService auditService;

    private WithholdEventService service;

    @BeforeEach
    public void setup() {
        service = new WithholdEventService(accountRepository, accountAllocationService,
                allocationStatusRepository, validator, auditService);
    }

    @Test
    void testProcessEvent() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123456L);
        event.setWithholdFlag(true);
        event.setReportingYear(Year.of(2024));

        AircraftOperator aircraftOperator = new AircraftOperator();
        aircraftOperator.setId(123L);
        Account account = new Account();
        account.setIdentifier(654321L);
        account.setCompliantEntity(aircraftOperator);

        Mockito.when(validator.validate(event)).thenReturn(List.of());
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123456L))
                .thenReturn(Optional.of(account));

        Map<Integer, AllocationStatusType> currentStatuses =
                Map.of(2024, AllocationStatusType.ALLOWED, 2025, AllocationStatusType.ALLOWED);
        Mockito.when(accountAllocationService.getAccountAllocationStatus(654321L))
                .thenReturn(currentStatuses);

        List<AllocationStatus> allocationStatuses = List.of(new AllocationStatus());
        Mockito.when(allocationStatusRepository.findByCompliantEntityId(123L))
                .thenReturn(allocationStatuses);

        // when
        service.process(event, Map.of(), SourceSystem.METSIA_AVIATION);

        // then
        Mockito.verify(accountAllocationService, Mockito.times(1))
                .updateAllocationStatus(any(), eq(false));
        Mockito.verify(auditService, Mockito.times(1))
                .logChanges(currentStatuses, allocationStatuses, account, SourceSystem.METSIA_AVIATION);
    }

    @Test
    void testProcessEventWithAlreadyUpdated() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123456L);
        event.setWithholdFlag(true);
        event.setReportingYear(Year.of(2024));

        Account account = new Account();
        account.setIdentifier(654321L);

        Mockito.when(validator.validate(event)).thenReturn(List.of());
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123456L))
                .thenReturn(Optional.of(account));

        Map<Integer, AllocationStatusType> currentStatuses =
                Map.of(2024, AllocationStatusType.WITHHELD, 2025, AllocationStatusType.WITHHELD);
        Mockito.when(accountAllocationService.getAccountAllocationStatus(654321L))
                .thenReturn(currentStatuses);

        // when
        service.process(event, Map.of(), SourceSystem.METSIA_INSTALLATION);

        // then
        Mockito.verify(accountAllocationService, Mockito.times(0))
                .updateAllocationStatus(any());
        Mockito.verify(auditService, Mockito.times(0))
                .logChanges(any(), any(), any(), any());
    }
}
