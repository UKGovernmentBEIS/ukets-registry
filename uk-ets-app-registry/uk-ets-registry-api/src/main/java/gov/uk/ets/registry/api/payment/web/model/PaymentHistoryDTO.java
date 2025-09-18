package gov.uk.ets.registry.api.payment.web.model;

import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.payment.domain.types.PaymentHistoryType;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class PaymentHistoryDTO implements SearchResult {

    private Long id;

    private Long referenceNumber;

    private String paymentId;

    private BigDecimal amount;

    private PaymentHistoryType type;

    private PaymentMethod method;

    private PaymentStatus status;

    private LocalDateTime created;

    private LocalDateTime updated;
}
