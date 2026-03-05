package gov.uk.ets.registry.api.regulatornotice.repository;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.regulatornotice.shared.RegulatorNoticeProjection;
import gov.uk.ets.registry.api.regulatornotice.web.model.RegulatorNoticeSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RegulatorNoticeSearchRepository {

    Page<RegulatorNoticeProjection> search(RegulatorNoticeSearchCriteria criteria, Pageable pageable);

    /**
     * Exposes JPA query needed for reporting.
     */
    JPAQuery<RegulatorNoticeProjection> getQuery(RegulatorNoticeSearchCriteria criteria);
}
