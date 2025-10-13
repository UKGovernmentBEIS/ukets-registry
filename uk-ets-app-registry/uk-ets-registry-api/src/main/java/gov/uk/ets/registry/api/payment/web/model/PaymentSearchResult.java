package gov.uk.ets.registry.api.payment.web.model;

import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.payment.domain.PaymentHistoryProjection;
import gov.uk.ets.registry.api.payment.domain.types.PaymentHistoryType;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * This class represents the result displayed in the ui when searching
 * the payments history.
 */
@Builder
@Getter
@Setter
public class PaymentSearchResult implements SearchResult {

    private Long referenceNumber;

    private String paymentId;

    private BigDecimal amount;

    private PaymentHistoryType type;

    private PaymentMethod method;

    private PaymentStatus status;

    private LocalDateTime updated;
    
    /**
     * Static factory method for instantiating a PaymentSearchResult
     * from a PaymentHistoryProjection.
     * 
     * @param paymentHistory
     * @return the created PaymentSearchResult
     */
    public static PaymentSearchResult from(PaymentHistoryProjection paymentHistory) {
        return PaymentSearchResult.builder()
                .type(paymentHistory.getType())
                .status(paymentHistory.getStatus())
                .paymentId(paymentHistory.getPaymentId())
                .amount(paymentHistory.getAmount())
                .referenceNumber(paymentHistory.getReferenceNumber())
                .updated(paymentHistory.getUpdated())
                .method(paymentHistory.getMethod())
                .build();
    }
}
