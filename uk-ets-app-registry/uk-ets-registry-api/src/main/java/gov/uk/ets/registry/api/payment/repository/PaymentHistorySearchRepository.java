package gov.uk.ets.registry.api.payment.repository;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.payment.domain.PaymentFilter;
import gov.uk.ets.registry.api.payment.domain.PaymentHistoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom Repository for searching in payment history.
 */
public interface PaymentHistorySearchRepository {

    /**
     * Exposes JPA query needed for searching.
     */
    Page<PaymentHistoryProjection> search(PaymentFilter filter, Pageable pageable);
    
    
    /**
     * Exposes JPA query used for searching.
     */
    JPAQuery<PaymentHistoryProjection> getQuery(PaymentFilter filter);
}
