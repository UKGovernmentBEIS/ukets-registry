package gov.uk.ets.registry.api.itl.notice.repository;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ITLNoticeLogHistoryRepository extends JpaRepository<ITLNotificationHistory, Long>, ITLNoticeSearchRepository {

    @Query("select nlh from ITLNotificationHistory nlh inner join ITLNotification nlg on nlg.id=nlh.notification.id where nlg.notificationIdentifier=?1 order by  nlh.createdDate")
    Set<ITLNotificationHistory> findAllByNotificationIdentifier(long notificationIdentifier);
}
