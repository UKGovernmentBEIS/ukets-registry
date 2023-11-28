package gov.uk.ets.registry.api.itl.message.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import gov.uk.ets.registry.api.itl.message.domain.AcceptMessageLog;
import gov.uk.ets.registry.api.itl.message.web.model.ITLMessageSearchCriteria;

public interface ITLMessageSearchRepository {

	  /**
	   * Searches and returns ITL messages against criteria provided in {@link ITLMessageSearchCriteria}
	   * @param criteria The search criteria
	   * @param pageable The pagination info
	   * @return The {@link Page} of results
	   *
	   */
	  Page<AcceptMessageLog> search(ITLMessageSearchCriteria criteria, Pageable pageable);
	  
}
