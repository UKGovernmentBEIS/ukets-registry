package gov.uk.ets.registry.api.task.service;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import org.springframework.stereotype.Service;

@Service
public class TaskConversionService {

    /**
     * Retrieves the task status based on the task outcome.
     * @param outcome The task outcome.
     * @return a status
     */
    public RequestStateEnum getTaskStatus(TaskOutcome outcome) {
        RequestStateEnum result = null;
        if (TaskOutcome.APPROVED.equals(outcome)) {
            result = RequestStateEnum.APPROVED;

        } else if (TaskOutcome.REJECTED.equals(outcome)) {
            result = RequestStateEnum.REJECTED;
        }
        return result;
    }

    /**
     * Retrieves the common status based on the task outcome.
     * @param outcome The task outcome.
     * @return the common status
     */
    public Status getStatus(TaskOutcome outcome) {
        Status result = null;
        if (TaskOutcome.APPROVED.equals(outcome)) {
            result = Status.ACTIVE;

        } else if (TaskOutcome.REJECTED.equals(outcome)) {
            result = Status.REJECTED;

        }
        return result;
    }

    /**
     * Retrieves the account access status based on the task outcome.
     * @param outcome The task outcome.
     * @return the account access status.
     */
    public AccountAccessState getAccessStatus(TaskOutcome outcome) {
        AccountAccessState result = null;
        if (TaskOutcome.APPROVED.equals(outcome)) {
            result = AccountAccessState.ACTIVE;

        } else if (TaskOutcome.REJECTED.equals(outcome)) {
            result = AccountAccessState.REJECTED;

        }
        return result;
    }

}
