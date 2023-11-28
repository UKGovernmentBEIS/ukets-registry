package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.repository.AllocationJobRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.allocationtable.AllocationTableUploadDetails;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Component for common allocation functionalities.
 */
@Component
@AllArgsConstructor
public class AllocationUtils {

    /**
     * Task repository.
     */
    private final TaskRepository taskRepository;

    /**
     * Allocation job repository.
     */
    private final AllocationJobRepository allocationJobRepository;

    /**
     * Transaction repository.
     */
    private final TransactionRepository transactionRepository;
    /**
     * Mapper util.
     */
    private final Mapper mapper;

    /**
     * Retrieves pending allocation request for year and category.
     *
     * @param allocationYear allocation year
     * @param allocationCategory allocation category
     * @return Pending allocation task
     */
    public Task getPendingAllocationRequest(Integer allocationYear, AllocationCategory allocationCategory) {
        Predicate<Task> checkYearAndCategory = task -> {
            String difference = task.getDifference();
            AllocationOverview allocationOverview = mapper.convertToPojo(difference, AllocationOverview.class);
            if (!Objects.equals(allocationOverview.getYear(), allocationYear)) {
                return false;
            }
            return allocationOverview.getRows()
                .keySet()
                .stream()
                .anyMatch(allocationType -> allocationType.getCategory() == allocationCategory);
        };

        return taskRepository.findPendingTasksByType(RequestType.ALLOCATION_REQUEST)
            .stream()
            .filter(checkYearAndCategory)
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks if there is a Pending allocation job or allocation transaction.
     *
     * @return true if a pending allocation job or pending allocation transaction exists, false otherwise
     */
    public boolean hasPendingAllocationOrTransactions() {
        return !allocationJobRepository.findByStatusIn(List.of(AllocationJobStatus.SCHEDULED, AllocationJobStatus.RUNNING)).isEmpty() ||
            transactionRepository.countByTypeAndStatusNotIn(TransactionType.AllocateAllowances, TransactionStatus.getFinalStatuses()) > 0;
    }

    /*
     * Added for UKETS-3301 to check
     * if an allocation task is pending or
     * if an allocation job is pending (SCHEDULED or RUNNING) or
     * if an allocation transaction is pending
     * */
    public boolean hasPendingAllocationOrTransactions(AllocationCategory category) {
        return hasPendingAllocationRequest(category) ||
            hasPendingAllocationJob(category) ||
            hasPendingAllocationTransactions(category);
    }

    /**
     * This method checks for a specific category:
     * a) if an allocation job is pending (SCHEDULED or RUNNING)
     * b) if an allocation transaction is pending.
     */
    public boolean hasPendingAllocationJobOrTransactions(AllocationCategory category, Integer year) {
        return hasPendingAllocationJob(category, year) || hasPendingAllocationTransactions(category, year);
    }

    private boolean hasPendingAllocationRequest(AllocationCategory allocationCategory) {
        Predicate<Task> checkCategory = task -> {
            String difference = task.getDifference();
            AllocationOverview allocationOverview = mapper.convertToPojo(difference, AllocationOverview.class);
            return allocationOverview.getRows()
                .keySet()
                .stream()
                .anyMatch(allocationType -> allocationType.getCategory() == allocationCategory);
        };

        return taskRepository.findPendingTasksByType(RequestType.ALLOCATION_REQUEST)
            .stream()
            .anyMatch(checkCategory);
    }

    private boolean hasPendingAllocationJob(AllocationCategory category) {
        return !allocationJobRepository
            .findByCategoryAndStatusIn(category, List.of(AllocationJobStatus.SCHEDULED, AllocationJobStatus.RUNNING))
            .isEmpty();
    }

    private boolean hasPendingAllocationJob(AllocationCategory category, Integer year) {
        return Objects.nonNull(allocationJobRepository
            .findByCategoryAndYearAndStatusIn(category, year, List.of(AllocationJobStatus.SCHEDULED, AllocationJobStatus.RUNNING)));
    }

    private boolean hasPendingAllocationTransactions(AllocationCategory category) {
        List<String> statuses = TransactionStatus.getFinalStatuses().stream().map(TransactionStatus::name).toList();
        return transactionRepository.countByTypeAndStatuesNotInAndAllocationTypes(
            TransactionType.AllocateAllowances.name(), statuses, category.getTypeNames()) > 0;
    }

    private boolean hasPendingAllocationTransactions(AllocationCategory category, Integer year) {
        List<String> statuses = TransactionStatus.getFinalStatuses().stream().map(TransactionStatus::name).toList();
        return transactionRepository.countByTypeAndStatuesNotInAndAllocationTypesAndAllocationYears(
            TransactionType.AllocateAllowances.name(), statuses, category.getTypeNames(), year.toString() ) > 0;
    }

    public boolean pendingAllocationTableTaskExists(AllocationCategory allocationCategory) {
        List<Task> pendingAllocationTableTasks =
            taskRepository.findPendingTasksByType(RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST);

        return pendingAllocationTableTasks.stream()
            .map(Task::getDifference)
            .filter(Objects::nonNull)
            .map(diff -> mapper.convertToPojo(diff, AllocationTableUploadDetails.class))
            .map(AllocationTableUploadDetails::getAllocationCategory)
            .anyMatch(category -> category == allocationCategory);
    }
}
