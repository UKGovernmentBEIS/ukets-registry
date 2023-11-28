package gov.uk.ets.registry.api.allocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.data.AllocationOverviewRow;
import gov.uk.ets.registry.api.allocation.repository.AllocationJobRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.allocationtable.AllocationTableUploadDetails;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AllocationUtilsTest {

    @InjectMocks
    AllocationUtils allocationUtils;

    @Mock
    TaskRepository taskRepository;

    @Mock
    AllocationJobRepository allocationJobRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    Mapper mapper;

    @ParameterizedTest(name = "#{index} - {0} - {1}")
    @MethodSource("getArgumentsPendingAllocationRequest")
    void testGetPendingAllocationRequest(Integer year, AllocationCategory category, boolean hasResult) {
        // given
        AllocationOverview allocationOverview = new AllocationOverview();
        allocationOverview.setYear(2022);
        allocationOverview.setRows(Map.of(AllocationType.NAT, new AllocationOverviewRow()));
        when(mapper.convertToPojo("difference", AllocationOverview.class)).thenReturn(allocationOverview);

        Task task = new Task();
        task.setDifference("difference");
        when(taskRepository.findPendingTasksByType(RequestType.ALLOCATION_REQUEST)).thenReturn(List.of(task));

        // when
        Task result = allocationUtils.getPendingAllocationRequest(year, category);

        // then
        assertEquals(hasResult, Objects.nonNull(result));
    }

    static Stream<Arguments> getArgumentsPendingAllocationRequest() {
        return Stream.of(
            Arguments.of(2022, AllocationCategory.INSTALLATION, true),
            Arguments.of(2021, AllocationCategory.INSTALLATION, false),
            Arguments.of(2022, AllocationCategory.AIRCRAFT_OPERATOR, false),
            Arguments.of(2021, AllocationCategory.AIRCRAFT_OPERATOR, false)
        );
    }

    @ParameterizedTest(name = "#{index} - {0}")
    @MethodSource("getArgumentsPendingAllocationTableTaskExists")
    void testPendingAllocationTableTaskExists(AllocationCategory category, boolean result) {
        // given
        AllocationTableUploadDetails allocationTableUploadDetails = new AllocationTableUploadDetails();
        allocationTableUploadDetails.setAllocationCategory(AllocationCategory.INSTALLATION);
        when(mapper.convertToPojo("difference", AllocationTableUploadDetails.class)).thenReturn(allocationTableUploadDetails);

        Task task = new Task();
        task.setDifference("difference");
        when(taskRepository.findPendingTasksByType(RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST)).thenReturn(List.of(task));

        // when
        boolean taskExists = allocationUtils.pendingAllocationTableTaskExists(category);

        // then
        assertEquals(result, taskExists);
    }

    static Stream<Arguments> getArgumentsPendingAllocationTableTaskExists() {
        return Stream.of(
            Arguments.of(AllocationCategory.INSTALLATION, true),
            Arguments.of(AllocationCategory.AIRCRAFT_OPERATOR, false)
        );
    }

    @Test
    void testHasPendingAllocationJobOrTransactions() {
        // given
        AllocationCategory category = AllocationCategory.INSTALLATION;
        Integer year = 2022;
        when(allocationJobRepository
            .findByCategoryAndYearAndStatusIn(category, year, List.of(AllocationJobStatus.SCHEDULED, AllocationJobStatus.RUNNING)))
            .thenReturn(null);

        when(transactionRepository.countByTypeAndStatuesNotInAndAllocationTypesAndAllocationYears(
            eq("AllocateAllowances"), anyList(), anyList(), eq("2022")))
            .thenReturn(1L);

        // when
        boolean result = allocationUtils.hasPendingAllocationJobOrTransactions(category, year);

        // then
        assertTrue(result);
    }

    @Test
    void testNotHasPendingAllocationJobOrTransactions() {
        // given
        AllocationCategory category = AllocationCategory.INSTALLATION;
        Integer year = 2022;
        when(allocationJobRepository
            .findByCategoryAndYearAndStatusIn(category, year, List.of(AllocationJobStatus.SCHEDULED, AllocationJobStatus.RUNNING)))
            .thenReturn(null);

        when(transactionRepository.countByTypeAndStatuesNotInAndAllocationTypesAndAllocationYears(
            eq("AllocateAllowances"), anyList(), anyList(), eq("2022")))
            .thenReturn(0L);

        // when
        boolean result = allocationUtils.hasPendingAllocationJobOrTransactions(category, year);

        // then
        assertFalse(result);
    }

    @Test
    void testNotHasPendingAllocationOrTransactions() {
        // given
        AllocationCategory category = AllocationCategory.INSTALLATION;
        Task task = new Task();
        task.setDifference("diff");
        AllocationOverview allocationOverview = new AllocationOverview();
        allocationOverview.setRows(Map.of(AllocationType.NAVAT, new AllocationOverviewRow()));

        when(taskRepository.findPendingTasksByType(RequestType.ALLOCATION_REQUEST))
            .thenReturn(List.of(task));
        when(mapper.convertToPojo("diff", AllocationOverview.class)).thenReturn(allocationOverview);
        when(allocationJobRepository
            .findByCategoryAndStatusIn(category, List.of(AllocationJobStatus.SCHEDULED, AllocationJobStatus.RUNNING)))
            .thenReturn(Collections.emptyList());

        when(transactionRepository.countByTypeAndStatuesNotInAndAllocationTypes(
            eq("AllocateAllowances"), anyList(), anyList()))
            .thenReturn(0L);

        // when
        boolean result = allocationUtils.hasPendingAllocationOrTransactions(category);

        // then
        assertFalse(result);
    }

    @Test
    void testHasPendingAllocationOrTransactions() {
        // given
        AllocationCategory category = AllocationCategory.INSTALLATION;
        Task task = new Task();
        task.setDifference("diff");
        AllocationOverview allocationOverview = new AllocationOverview();
        allocationOverview.setRows(Map.of(AllocationType.NAVAT, new AllocationOverviewRow()));

        when(taskRepository.findPendingTasksByType(RequestType.ALLOCATION_REQUEST))
            .thenReturn(List.of(task));
        when(mapper.convertToPojo("diff", AllocationOverview.class)).thenReturn(allocationOverview);
        when(allocationJobRepository
            .findByCategoryAndStatusIn(category, List.of(AllocationJobStatus.SCHEDULED, AllocationJobStatus.RUNNING)))
            .thenReturn(Collections.emptyList());

        when(transactionRepository.countByTypeAndStatuesNotInAndAllocationTypes(
            eq("AllocateAllowances"), anyList(), anyList()))
            .thenReturn(1L);

        // when
        boolean result = allocationUtils.hasPendingAllocationOrTransactions(category);

        // then
        assertTrue(result);
    }
}
