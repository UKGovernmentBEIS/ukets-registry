package gov.uk.ets.registry.api.authz.ruleengine.features.allocation;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.service.AllocationJobService;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckExecutionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.checks.allocation.CheckAllocationAccountHasEnoughUnits;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.util.List;
import java.util.stream.Stream;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AllocationSecurityStoreSliceLoader {

    private final TaskRepository taskRepository;
    private final AllocationJobService allocationJobService;
    private final BusinessCheckExecutionService checkService;
    private final CheckAllocationAccountHasEnoughUnits checkAllocationAccountHasEnoughUnits;
    private final Mapper mapper;
    private BusinessSecurityStore businessSecurityStore;
    private RuleInputStore ruleInputStore;

    public void load() {
        if (businessSecurityStore.getAllocationSecurityStoreSlice() != null ||
            !ruleInputStore.containsKey(RuleInputType.TASK_REQUEST_ID) ||
            !ruleInputStore.containsKey(RuleInputType.TASK_OUTCOME)) {
            return;
        }

        TaskOutcome taskOutcome = (TaskOutcome) ruleInputStore.get(RuleInputType.TASK_OUTCOME);
        if (TaskOutcome.APPROVED != taskOutcome) {
            return;
        }

        Long requestId = (Long) ruleInputStore.get(RuleInputType.TASK_REQUEST_ID);
        Task task = taskRepository.findByRequestId(requestId);

        if (task.getType() != RequestType.ALLOCATION_REQUEST) {
            return;
        }

        AllocationSecurityStoreSlice slice = new AllocationSecurityStoreSlice();

        List<Long> requestIds = allocationJobService.getScheduledJobs().stream()
            .map(AllocationJob::getRequestIdentifier)
            .toList();
        List<Task> tasks = taskRepository.findAllByRequestIdIn(requestIds);

        AllocationOverview[] allocationOverviews = Stream.concat(tasks.stream(), Stream.of(task))
            .map(t -> mapper.convertToPojo(t.getDifference(), AllocationOverview.class))
            .toArray(AllocationOverview[]::new);

        BusinessCheckContext context = new BusinessCheckContext();
        context.store(AllocationOverview[].class.getName(), allocationOverviews);

        BusinessCheckResult result = checkService.execute(context, List.of(checkAllocationAccountHasEnoughUnits));
        slice.setEnoughUnitsOnAllocationAccounts(result.success());

        businessSecurityStore.setAllocationSecurityStoreSlice(slice);
    }


    @Resource(name = "requestScopedBusinessSecurityStore")
    protected void setBusinessSecurityStore(BusinessSecurityStore businessSecurityStore) {
        this.businessSecurityStore = businessSecurityStore;
    }

    @Resource(name = "requestScopedRuleInputStore")
    protected void setRuleInputStore(RuleInputStore ruleInputStore) {
        this.ruleInputStore = ruleInputStore;
    }
}
