package gov.uk.ets.registry.api.itl.notice.repository;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeLogHistoryRepository extends JpaRepository<ITLNotificationHistory, Long> {
}
