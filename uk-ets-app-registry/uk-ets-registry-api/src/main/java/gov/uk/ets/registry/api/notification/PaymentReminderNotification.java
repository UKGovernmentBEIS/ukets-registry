package gov.uk.ets.registry.api.notification;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentReminderNotification extends EmailNotification {

    private static final long serialVersionUID = 1L;
    private Date initiationDate;
    private Long requestId;
    private BigDecimal amount;
    private String description;

    @Builder
    public PaymentReminderNotification(Set<String> recipients, GroupNotificationType type, Date initiationDate, Long requestId, BigDecimal amount, String description) {
        super(recipients, type, "Payment reminder for UK ETS Registry task " + Long.toString(requestId), null, null);
        this.initiationDate = initiationDate;
        this.requestId = requestId;
        this.amount = amount;
        this.description = description;
    }
    
}
