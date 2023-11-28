package gov.uk.ets.registry.api.transaction.repository;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilter;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionProjectionRepository {
    Page<TransactionProjection> search(TransactionFilter filter, Pageable pageable);

    /**
     * Exposes JPA query needed for reporting.
     */
    JPAQuery<TransactionProjection> getQuery(TransactionFilter filter, Pageable pageable);
}
