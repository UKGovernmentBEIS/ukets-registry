package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskTransaction;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.repository.TaskTransactionRepository;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AllocationSchedulerService {

    /**
     * Persistence service for transactions.
     */
    private final TransactionPersistenceService persistenceService;

    /**
     * Service for transactions.
     */
    private final AllocationTransactionService transactionService;

    /**
     * Service for allocation job.
     */
    private final AllocationJobService jobService;

    private final TaskRepository taskRepository;

    private final TaskTransactionRepository taskTransactionRepository;

    /**
     * Pause between allocation transactions.
     */
    @Value("${scheduler.allocation.pause:20}")
    private Integer pauseInMilliseconds;

    /**
     * Whether a pause between allocation transactions is enabled.
     */
    @Value("${scheduler.allocation.pause.enable:true}")
    private Boolean pauseEnabled;

    private final EventService eventService;

    /**
     * Launches the allocation transactions.
     *
     * @param overview The allocation overview.
     * @return
     */
    public void launchAllocations(AllocationOverview overview, AllocationJob allocationJob) {

        int totalAllocations = overview.getBeneficiaryRecipients().size();
        int completedAllocations = 0;

        Map<AccountType,AccountSummary> accounts = new HashMap<>();

        for (AllocationSummary recipient : overview.getBeneficiaryRecipients()) {
            switch (recipient.getType()) {
                case NER -> {
                    AccountSummary nerSummary = persistenceService.getAccount(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, AccountStatus.OPEN);
                    accounts.put(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, nerSummary);
                }
                case NAT, NAVAT -> {
                    AccountSummary natSummary = persistenceService.getAccount(AccountType.UK_ALLOCATION_ACCOUNT, AccountStatus.OPEN);
                    accounts.put(AccountType.UK_ALLOCATION_ACCOUNT, natSummary);
                }
            }
        }

        Task task = taskRepository.findByRequestId(allocationJob.getRequestIdentifier());
        for (AllocationSummary entry : overview.getBeneficiaryRecipients()) {
                try {
                    BusinessCheckResult transactionIdentifier = transactionService.executeAllocation(
                            entry.getType() == AllocationType.NER
                                    ? accounts.get(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT).getIdentifier()
                                    : accounts.get(AccountType.UK_ALLOCATION_ACCOUNT).getIdentifier(),
                            entry.getAccountFullIdentifier(),
                            entry.getRemaining(),
                            overview.getYear(),
                            entry.getType()
                    );
                    if (pauseEnabled) {
                        TimeUnit.MILLISECONDS.sleep(pauseInMilliseconds);
                    }
                    completedAllocations++;
                    linkTaskWithTransaction(task, transactionIdentifier.getTransactionIdentifier(), entry.getAccountFullIdentifier());
                } catch (InterruptedException exception) {
                    log.error("Interrupted exception when executing the allocation job", exception);
                    Thread.currentThread().interrupt();
                    AllocationJobStatus finalStatus = getFinalStatus(totalAllocations, completedAllocations);
                    jobService.setStatus(allocationJob, finalStatus);
                    jobService.setErrors(allocationJob, Map.of(-1, "Job was interrupted"));
                    return;

                } catch (Exception exception) {
                    log.error("Exception when executing the allocation job", exception);
                }


        }

        AllocationJobStatus finalStatus = getFinalStatus(totalAllocations, completedAllocations);
        jobService.setStatus(allocationJob, finalStatus);
        if (finalStatus == AllocationJobStatus.FAILED) {
            jobService.setErrors(allocationJob, Map.of(-1, "All transactions have failed"));
        }
        publishEvent(allocationJob.getRequestIdentifier(), finalStatus);
    }

    private AllocationJobStatus getFinalStatus(int totalAllocations, int completedAllocations) {
        if (totalAllocations != 0 && completedAllocations == 0) {
            return AllocationJobStatus.FAILED;
        } else if (totalAllocations > completedAllocations) {
            return AllocationJobStatus.COMPLETED_WITH_FAILURES;
        } else {
            return AllocationJobStatus.COMPLETED;
        }
    }

    private void publishEvent(Long requestIdentifier, AllocationJobStatus finalStatus) {

        if (AllocationJobStatus.COMPLETED_WITH_FAILURES.equals(finalStatus)) {
            eventService.createAndPublishEvent(requestIdentifier.toString(),
                null,
                "Allocation Job executed with errors. See logs for more details.",
                EventType.UPLOAD_ALLOCATION_TABLE,
                "Error in Allocation Job execution");
        } else if (AllocationJobStatus.FAILED.equals(finalStatus)) {
            eventService.createAndPublishEvent(requestIdentifier.toString(),
                null,
                "Allocation Job failed. See logs for more details.",
                EventType.UPLOAD_ALLOCATION_TABLE,
                "Error in Allocation Job execution");
        }

    }

    private void linkTaskWithTransaction(Task task, String transactionIdentifier, String acquiringAccountFullIdentifier) {
        TaskTransaction taskTransaction = new TaskTransaction();
        taskTransaction.setTask(task);
        taskTransaction.setTransactionIdentifier(transactionIdentifier);
        taskTransaction.setRecipientAccountNumber(acquiringAccountFullIdentifier);

        if (task.getTransactionIdentifiers() == null) {
            task.setTransactionIdentifiers(new ArrayList<>());
        }
        task.getTransactionIdentifiers().add(taskTransaction);

        taskRepository.save(task);
        taskTransactionRepository.save(taskTransaction);
    }
}
