package gov.uk.ets.registry.api.payment.domain;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.payment.domain.types.PaymentHistoryType;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PaymentHistoryProjection implements SearchResult {

    private Long referenceNumber;

    private String paymentId;

    private PaymentHistoryType type;

    private PaymentMethod method;

    private PaymentStatus status;
    
    private BigDecimal amount;

    private LocalDateTime updated;

    @QueryProjection
    public PaymentHistoryProjection(Long referenceNumber, String paymentId, PaymentHistoryType type,
        PaymentMethod method,  PaymentStatus status, BigDecimal amount, LocalDateTime updated) {

        this.referenceNumber = referenceNumber;
        this.paymentId = paymentId;
        this.type = type;
        this.method = method;
        this.status = status;
        this.amount = amount;
        this.updated = updated;
    }

}
