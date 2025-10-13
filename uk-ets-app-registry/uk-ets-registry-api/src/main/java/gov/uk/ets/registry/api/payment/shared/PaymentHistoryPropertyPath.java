package gov.uk.ets.registry.api.payment.shared;

/**
 * Payment property paths.
 */
public class PaymentHistoryPropertyPath {

    /**
     * Do not allow instantiation.
     */
    private PaymentHistoryPropertyPath() {
    }
    
    /**
     * The reference number property path.
     */
    public static final String REFERENCE_NUMBER = "paymentHistory.referenceNumber";
    
    /**
     * The payment identifier in gov.uk pay.
     */
    public static final String PAYMENT_ID = "paymentHistory.paymentId";
    
    /**
     * The payment type.
     */
    public static final String PAYMENT_TYPE = "paymentHistory.type";
    
    /**
     * The payment method.
     */
    public static final String PAYMENT_METHOD = "paymentHistory.method";
    
    /**
     * The payment status.
     */
    public static final String PAYMENT_STATUS = "paymentHistory.status";
    
    /**
     * The payment amount.
     */
    public static final String PAYMENT_AMOUNT = "paymentHistory.amount";
    
    /**
     * The last updated date.
     */
    public static final String LAST_UPDATED = "paymentHistory.updated";
 
}