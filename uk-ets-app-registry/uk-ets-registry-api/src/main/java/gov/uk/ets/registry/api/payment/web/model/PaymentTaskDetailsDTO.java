package gov.uk.ets.registry.api.payment.web.model;

import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentTaskDetailsDTO extends TaskDetailsDTO {

    private BigDecimal amountRequested;
    private BigDecimal amountPaid;
    private String description;
    private String uuid;
    private String paymentLink;
    private PaymentFileDTO invoiceFile;
    private PaymentFileDTO receiptFile;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    
    public PaymentTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO) {
        super(taskDetailsDTO);
    }
    
}
