package gov.uk.ets.registry.api.notification.userinitiated.messaging.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
public class BaseNotificationParameters {
    /**
     * needed to correlate with email
     */
    private String urid;

    @ToString.Exclude
    private String firstName;

    @ToString.Exclude
    private String lastName;
    /**
     * only used in domain event
     */
    @ToString.Exclude
    private String disclosedName;
    /**
     * needed to correlate with compliant entity
     */
    private Long accountId;
    /**
     * needed for domain events and to correlate the compliance balance
     */
    private Long accountIdentifier;
    /**
     * needed for template variable accountId
     */
    private String accountFullIdentifier;

    @ToString.Exclude
    private String accountHolderFirstName;
    @ToString.Exclude
    private String accountHolderLastName;
    @ToString.Exclude
    private String accountHolderName;
    private Date currentDate;
    private Integer currentYear;
}
