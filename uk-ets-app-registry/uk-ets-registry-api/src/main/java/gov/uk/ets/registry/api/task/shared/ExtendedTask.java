package gov.uk.ets.registry.api.task.shared;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import lombok.Getter;
import lombok.Setter;


/**
 * This class was created solely for the purposes of
 * being able to use Collections groupingBy method
 * on the field of difference (which translates to
 * AccountDTO) for the task of account opening with
 * installation transfer [UKETS-5220] migrator.
 * */
@Getter
@Setter
public class ExtendedTask extends Task {
    private AccountDTO accountDTO;

    public ExtendedTask(Task task, AccountDTO accountDTO) {
        super(task.getId(), task.getRequestId(), task.getType(),
                task.getInitiatedBy(), task.getInitiatedDate(), task.getClaimedBy(),
                task.getClaimedDate(), task.getCompletedBy(), task.getCompletedDate(),
                task.getAccount(), task.getTransactionIdentifiers(),
                task.getStatus(), task.getBefore(), task.getAfter(),
                task.getDifference(), task.getParentTask(), task.getUser(),
                task.getFile(), task.getTaskSearchMetadata(), task.getTaskAssignments(),
                task.getDeadline());
        this.accountDTO = accountDTO;
    }

    public long getInstallationId(){
        return this.accountDTO.getInstallationToBeTransferred().getIdentifier();
    }

    /**
        Used in order to avoid using
        casting back to the hibernate entity
        so nothing changes or breaks in terms
        of persistence context.
     */
    public Task convertToNewTask() {
        Task task = new Task();
        task.setId(this.getId());
        task.setRequestId(this.getRequestId());
        task.setType(this.getType());
        task.setInitiatedBy(this.getInitiatedBy());
        task.setInitiatedDate(this.getInitiatedDate());
        task.setClaimedBy(this.getClaimedBy());
        task.setClaimedDate(this.getClaimedDate());
        task.setCompletedBy(this.getCompletedBy());
        task.setCompletedDate(this.getCompletedDate());
        task.setAccount(this.getAccount());
        task.setTransactionIdentifiers(this.getTransactionIdentifiers());
        task.setStatus(this.getStatus());
        task.setBefore(this.getBefore());
        task.setAfter(this.getAfter());
        task.setDifference(this.getDifference());
        task.setParentTask(this.getParentTask());
        task.setUser(this.getUser());
        task.setFile(this.getFile());
        task.setTaskSearchMetadata(this.getTaskSearchMetadata());
        task.setTaskAssignments(this.getTaskAssignments());
        return  task;
    }
}
