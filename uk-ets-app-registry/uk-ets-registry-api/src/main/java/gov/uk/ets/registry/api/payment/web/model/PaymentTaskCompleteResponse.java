package gov.uk.ets.registry.api.payment.web.model;

import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * The response object to a Payment task completion action.
 */
@Getter
@Setter
public class PaymentTaskCompleteResponse extends TaskCompleteResponse {

    private Long referenceNumber;
    private LocalDate paidOn;
    private String paidBy;

    @Builder(builderMethodName = "paymentCompleteResponseBuilder")
    public PaymentTaskCompleteResponse(Long requestIdentifier, TaskDetailsDTO taskDetails,
              LocalDate paidOn, String paidBy, Long referenceNumber) {
        super(requestIdentifier, taskDetails);
        this.paidOn = paidOn;
        this.paidBy = paidBy;
        this.referenceNumber = referenceNumber;
    }
}
