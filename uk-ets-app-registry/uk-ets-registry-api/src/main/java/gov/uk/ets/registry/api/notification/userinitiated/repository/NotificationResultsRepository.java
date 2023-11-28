package gov.uk.ets.registry.api.notification.userinitiated.repository;

import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationResultsRepository {

    Page<NotificationSearchResult> search(NotificationSearchCriteria criteria, Pageable pageable);
}
