package gov.uk.ets.registry.api.allocation.service;

import static gov.uk.ets.registry.api.allocation.service.AccountAllocationService.ALLOCATION_RESTRICTION_LIFTED;
import static gov.uk.ets.registry.api.allocation.service.AccountAllocationService.ALLOCATION_WITHHELD;
import static gov.uk.ets.registry.api.allocation.service.AccountAllocationService.FOR_YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.service.dto.UpdateAllocationCommand;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class UpdateAllocationStatusBDDTest extends GetAccountAllocationBDDTest {
    @Test
    void getAccountAllocationStatus() {
        int currentAllocationYear = 2022;
        // given an unexisted account, when service call getAccountAllocationStatus then should throw exception.
        assertThrows(IllegalArgumentException.class, () -> serviceUnderTest.getAccountAllocationStatus(1L));

        given(allocationConfigurationService.getAllocationYear()).willReturn(currentAllocationYear);

        // given an account without compliant entity
        Long accountId = 1234L;
        given(accountRepository.findByIdentifier(accountId)).willReturn(Optional.of(account));

        // when
        Map<Integer, AllocationStatusType> result = serviceUnderTest.getAccountAllocationStatus(accountId);

        // then
        then(accountRepository).should(times(1)).findByIdentifier(accountId);
        assertTrue(result.isEmpty());

        // given account with compliantEntity
        Long compliantEntityId = 10000L;
        given(compliantEntity.getId()).willReturn(compliantEntityId);
        given(account.getCompliantEntity()).willReturn(compliantEntity);
        Map<Integer, AllocationStatusType> results = Stream.of(
            new AbstractMap.SimpleEntry<Integer, AllocationStatusType>(2022, AllocationStatusType.ALLOWED),
            new AbstractMap.SimpleEntry<Integer, AllocationStatusType>(2023, AllocationStatusType.WITHHELD),
            new AbstractMap.SimpleEntry<Integer, AllocationStatusType>(2024, AllocationStatusType.WITHHELD))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<AllocationSummary> allocationStatusServiceResult = results.entrySet().stream()
            .filter(allocationStatus -> allocationStatus.getKey() <= currentAllocationYear)
            .map(e -> new AllocationSummary(e.getKey(), e.getValue())).collect(Collectors.toList());
        given(allocationStatusService.getAllocationStatus(compliantEntityId)).willReturn(allocationStatusServiceResult);

        // when
        result = serviceUnderTest.getAccountAllocationStatus(accountId);

        then(allocationStatusService).should(times(1)).getAllocationStatus(compliantEntityId);
        Map<Integer, AllocationStatusType> expectedResult = new HashMap<>();
        expectedResult.put(2022, AllocationStatusType.ALLOWED);
        assertEquals(expectedResult, result);
    }

    @Test
    void updateAllocationStatus() {
        // given non existed account
        UpdateAllocationCommand command = UpdateAllocationCommand.builder()
            .accountId(1000L)
            .justification("test")
            .changedStatus(Stream.of(
                new AbstractMap.SimpleEntry<Integer, AllocationStatusType>(2022, AllocationStatusType.ALLOWED),
                new AbstractMap.SimpleEntry<Integer, AllocationStatusType>(2023, AllocationStatusType.WITHHELD),
                new AbstractMap.SimpleEntry<Integer, AllocationStatusType>(2024, AllocationStatusType.WITHHELD))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
            .build();

        // when the account does not exist then
        assertThrows(IllegalArgumentException.class,
            () -> serviceUnderTest.updateAllocationStatus(command));

        // given existed account without compliant entity
        given(accountRepository.findByIdentifier(command.getAccountId())).willReturn(Optional.of(account));

        // when then
        assertThrows(IllegalArgumentException.class, () -> serviceUnderTest.updateAllocationStatus(command));

        // given existed account with compliant entity
        String urid = "UK123213";
        User currentUser = mock(User.class);
        given(currentUser.getUrid()).willReturn(urid);
        given(userService.getCurrentUser()).willReturn(currentUser);
        Long compliantEntityId = 20000L;
        given(compliantEntity.getId()).willReturn(compliantEntityId);
        given(account.getCompliantEntity()).willReturn(compliantEntity);

        // when
        serviceUnderTest.updateAllocationStatus(command);

        // then
        then(allocationStatusService).should(times(1)).updateAllocationStatus(compliantEntityId, command.getChangedStatus());
        // test the events generating
        command.getChangedStatus().entrySet().stream().forEach(e -> {
            String actionPrefix = e.getValue().equals(AllocationStatusType.ALLOWED) ? ALLOCATION_RESTRICTION_LIFTED : ALLOCATION_WITHHELD;
            String expectedAction = actionPrefix + FOR_YEAR + e.getKey();
            EventType expectedType = EventType.ACCOUNT_ALLOCATION_STATUS_UPDATED;
            String expectedDescription = command.getJustification();
            then(eventService).should(times(1))
                .createAndPublishEvent(
                    command.getAccountId().toString(), // event identifier
                    currentUser.getUrid(), // The urid
                    expectedDescription, // The expected event description
                    expectedType, // The expected event type
                    expectedAction // The expected action
                );
        });
    }
}