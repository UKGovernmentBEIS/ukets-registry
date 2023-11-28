package gov.uk.ets.registry.api.itl.notice.repository;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotification;
import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeLogRepository extends JpaRepository<ITLNotification, Long> {

    @Query(value = "SELECT n FROM ITLNotification n WHERE n.notificationIdentifier = :notificationIdentifier")
    ITLNotification findNoticeLogsByNotificationIdentifier(@Param("notificationIdentifier") Long notificationIdentifier);

    @Query(
        "select h " +
        "  from ITLNotification n " +
        "  join n.noticeLogHistories h " +
        " where n.status <> gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus.COMPLETED " +
         " and h.status <> gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus.COMPLETED "+
        "   and n.notificationIdentifier = ?1 " +
        "   order by h.createdDate desc")
    List<ITLNotificationHistory> retrieveIncomingNotificationData(Long identifier);

}
