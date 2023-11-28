package gov.uk.ets.registry.api.allocation.service;

import static gov.uk.ets.registry.api.allocation.error.AllocationError.INVALID_ALLOCATION_YEAR;
import static gov.uk.ets.registry.api.allocation.error.AllocationError.PENDING_ALLOCATION_TASK_APPROVAL;
import static gov.uk.ets.registry.api.allocation.error.AllocationError.PENDING_ALLOCATION_TRANSACTIONS;
import static gov.uk.ets.registry.api.allocation.error.AllocationError.PENDING_UPLOAD_ALLOCATION_TABLE;
import static java.util.stream.Collectors.toList;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.error.AllocationBusinessRulesException;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.data.IssuanceBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestAllocationService {

    private final AllocationYearCapService allocationYearCapService;
    private final AllocationConfigurationService allocationConfigurationService;
    private final AllocationUtils allocationUtils;
    private final UserService userService;
    private final EventService eventService;
    private final AllocationCalculationService allocationCalculationService;
    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final Mapper mapper;
    private final RequestAllocationExcelFileGenerator excelFileGenerator;


    /**
     * Retrieves the years that the request allocation task can be created for.
     */
    public List<Integer> getAvailableAllocationYears() {
        Integer allocationYear = allocationConfigurationService.getAllocationYear();
        return allocationYearCapService.getCapsForCurrentPhase().stream()
            .map(IssuanceBlockSummary::getYear)
            .filter(year -> year <= allocationYear)
            .sorted()
            .collect(toList());
    }

    /**
     * Submits a request allocation proposal.
     */
    @Transactional
    public BusinessCheckResult submit(Integer allocationYear, AllocationCategory allocationCategory) {

        validateRequest(allocationYear, allocationCategory);

        User currentUser = userService.getCurrentUser();

        Task task = new Task();

        AllocationOverview allocationOverview =
            allocationCalculationService.calculateAllocationsOverview(allocationYear, allocationCategory);
        task.setDifference(mapper.convertToJson(allocationOverview));

        task.setRequestId(taskRepository.getNextRequestId());
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());

        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setType(RequestType.ALLOCATION_REQUEST);

        Account account = allocationOverview.getBeneficiaryRecipients()
                .stream()
                .anyMatch(f-> f.getType() != AllocationType.NER)
                ?  accountRepository.findByRegistryAccountTypeForNonClosedAccounts(RegistryAccountType.UK_ALLOCATION_ACCOUNT)
                .orElseThrow(() -> new IllegalArgumentException("Account of type allocation was not found."))
                : accountRepository.findByRegistryAccountTypeForNonClosedAccounts(RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT)
                .orElseThrow(() -> new IllegalArgumentException("Account of type new entrants reserve not found."));

        task.setAccount(account);

        taskRepository.save(task);

        String fileName = String.format("UK_AllocationJob_%s_%s_%s.xlsx", allocationYear, allocationCategory, task.getRequestId());
        excelFileGenerator.generateExcel(task, fileName, allocationOverview.getBeneficiaryRecipients());

        String action = String.format("Allocation Year %d Allocation Category %s", allocationYear, allocationCategory);
        eventService
            .createAndPublishEvent(account.getIdentifier().toString(), currentUser.getUrid(), action,
                EventType.TASK_REQUESTED, "Allocation request submitted");

        BusinessCheckResult bcr = new BusinessCheckResult();
        bcr.setRequestIdentifier(task.getRequestId());
        return bcr;
    }

    /**
     * Retrieves a file containing the allocations for the given year.
     */
    public byte[] getAllocationsFile(Integer allocationYear, AllocationCategory allocationCategory) {
        AllocationOverview allocationOverview =
            allocationCalculationService.calculateAllocationsOverview(allocationYear, allocationCategory);

        return excelFileGenerator.generateFile(allocationOverview.getBeneficiaryRecipients(), true);
    }

    private void validateRequest(Integer allocationYear, AllocationCategory allocationCategory) {
        if (!getAvailableAllocationYears().contains(allocationYear)) {
            throw AllocationBusinessRulesException.create(INVALID_ALLOCATION_YEAR);
        }

        if (allocationUtils.getPendingAllocationRequest(allocationYear, allocationCategory) != null) {
            throw AllocationBusinessRulesException.create(PENDING_ALLOCATION_TASK_APPROVAL);
        }

        boolean pendingAllocationTableExists = allocationUtils.pendingAllocationTableTaskExists(allocationCategory);
        if (pendingAllocationTableExists) {
            throw AllocationBusinessRulesException.create(PENDING_UPLOAD_ALLOCATION_TABLE);
        }

        if (allocationUtils.hasPendingAllocationJobOrTransactions(allocationCategory, allocationYear)) {
            throw AllocationBusinessRulesException.create(PENDING_ALLOCATION_TRANSACTIONS);
        }
    }

    /**
     * Returns a list with compliant entities' IDs included in a pending ALLOCATION_REQUEST task.
     **/
    public Set<Long> getEntitiesInPendingAllocationRequestTaskOrJob() {
        List<Task> allocationRequestTasks =
            taskRepository.findPendingTasksByType(RequestType.ALLOCATION_REQUEST);

        return allocationRequestTasks.stream()
            .map(task -> mapper.convertToPojo(allocationRequestTasks.get(0).getDifference(), AllocationOverview.class))
            .map(AllocationOverview::getBeneficiaryRecipients)
            .flatMap(Collection::stream)
            .map(AllocationSummary::getIdentifier)
            .collect(Collectors.toSet());
    }
}
