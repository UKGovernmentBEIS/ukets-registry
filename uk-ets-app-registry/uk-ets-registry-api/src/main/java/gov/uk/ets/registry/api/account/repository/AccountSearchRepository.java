package gov.uk.ets.registry.api.account.repository;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.AccountFilter;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repository for searching accounts
 */
public interface AccountSearchRepository {

    /**
     * Searches and returns accounts against criteria provided in {@link AccountFilter}
     *
     * @param filter   The search criteria
     * @param pageable The pagination info
     * @return The {@link Page} of results
     */
    Page<AccountProjection> search(AccountFilter filter, Pageable pageable);

    /**
     * Exposes JPA query used for reporting.
     */
    JPAQuery<AccountProjection> getQuery(AccountFilter filter);
}
