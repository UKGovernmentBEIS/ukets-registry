package gov.uk.ets.registry.api.payment.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * Value of Search criteria on filtering payments.
 */
@Getter
@Builder
public class PaymentFilter {

    /**
     * The reference number.
     */
    private Long referenceNumber;
    
    /**
     * The urid (unique business identifier of user)
     * of the e.g. authorised representative, non admin case.
     */
    private String urid;
    
    /**
     * Whether the search originates from admin.
     */
    private Boolean adminSearch;
}
