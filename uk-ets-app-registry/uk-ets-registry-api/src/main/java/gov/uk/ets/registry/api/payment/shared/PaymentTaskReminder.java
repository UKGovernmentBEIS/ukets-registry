package gov.uk.ets.registry.api.payment.shared;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Result object for custom native query to fetch payment tasks.
 */
public interface PaymentTaskReminder {

    Long getReferenceNumber();
    
    BigDecimal getAmountRequested();
    
    String getDescription();
    
    Date getInitiatedDate();
    
    String getClaimantEmail();
    
    String getClaimantUrid();
}
