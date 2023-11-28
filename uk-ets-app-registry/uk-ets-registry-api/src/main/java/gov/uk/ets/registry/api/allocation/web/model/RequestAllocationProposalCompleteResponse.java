package gov.uk.ets.registry.api.allocation.web.model;

import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RequestAllocationProposalCompleteResponse extends TaskCompleteResponse {

    private String executionTime;
    private String executionDate;

    @Builder(builderMethodName = "requestAllocationProposalCompleteResponse")
    public RequestAllocationProposalCompleteResponse(Long requestIdentifier,
                                                     String executionTime,
                                                     String executionDate) {
        super(requestIdentifier, null);
        this.executionTime = executionTime;
        this.executionDate = executionDate;
    }


}
