package gov.uk.ets.registry.api.tal.repository;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.tal.domain.TrustedAccountFilter;
import gov.uk.ets.registry.api.tal.web.model.search.TALProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TALSearchRepository {
    /**
     * Searches and returns accounts against criteria provided in {@link TrustedAccountFilter}
     *
     * @param filter   The search criteria
     * @param pageable The pagination info
     * @return The {@link Page} of results
     */
    Page<TALProjection> search(TrustedAccountFilter filter, Pageable pageable);

    /**
     * Exposes JPA query used for reporting.
     */
    JPAQuery<TALProjection> getQuery(TrustedAccountFilter filter);
}
