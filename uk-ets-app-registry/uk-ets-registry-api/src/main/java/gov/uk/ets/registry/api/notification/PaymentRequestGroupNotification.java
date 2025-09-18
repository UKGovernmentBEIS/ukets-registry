package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
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
public class PaymentRequestGroupNotification extends EmailNotification {

    /**
    * Serialisation version.
    */
    private static final long serialVersionUID = -6832107885063724782L;
    private String requestId;
    private String paymentDescription;

    @Builder
    public PaymentRequestGroupNotification(Set<String> recipients, GroupNotificationType type, String requestId,
                                            String paymentDescription) {
        super(recipients, type, null, null, null);
        this.requestId = requestId;
        this.paymentDescription = paymentDescription;
    }
    
}
