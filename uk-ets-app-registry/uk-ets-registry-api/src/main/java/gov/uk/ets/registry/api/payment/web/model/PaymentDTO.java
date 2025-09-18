package gov.uk.ets.registry.api.payment.web.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * The payment transfer object.
 */
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor
public class PaymentDTO implements Serializable  {

    /**
    * Serialisation version.
    */
    private static final long serialVersionUID = 5819255311085626147L;

    
    /**
     * The description of the payment.
     */
    @NotEmpty
    @Size(min = 3, max = 256)
    private String description;
    
    /**
     * The amount of the payment.
     */
    @DecimalMin(value = "0.03", inclusive = true)
    @Digits(integer = 5, fraction = 2)
    @DecimalMax(value = "15000", inclusive = true)
    private BigDecimal amount;
    
    /**
     * The request identifier of the parent task.
     */
    private Long parentRequestId;
    
    /**
     * The user the payment task shall be assigned.
     */
    private String recipientUrid;
}
