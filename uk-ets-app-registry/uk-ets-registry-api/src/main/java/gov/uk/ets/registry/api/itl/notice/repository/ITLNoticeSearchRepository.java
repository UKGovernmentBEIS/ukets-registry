package gov.uk.ets.registry.api.itl.notice.repository;

import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeResult;
import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITLNoticeSearchRepository {

    /**
     * Searches and returns ITL notices against criteria provided in {@link ITLNoticeSearchCriteria}
     *
     * @param criteria The search criteria
     * @param pageable The pagination info
     * @return The {@link Page} of results
     *
     */
    Page<ITLNoticeResult> search(ITLNoticeSearchCriteria criteria, Pageable pageable);
}
