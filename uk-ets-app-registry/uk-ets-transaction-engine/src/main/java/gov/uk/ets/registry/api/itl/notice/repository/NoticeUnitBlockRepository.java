package gov.uk.ets.registry.api.itl.notice.repository;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotificationBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface NoticeUnitBlockRepository extends JpaRepository<ITLNotificationBlock, Long> {

    @Query(value = "SELECT nui.* FROM itl_notification_block nui INNER JOIN itl_notification nlg ON nlg.id = nui.notice_log_id" +
            " WHERE nlg.identifier=:notificationIdentifier ORDER BY nui.created_date", nativeQuery = true)
    Set<ITLNotificationBlock> findAllNoticeUnitBlocksOfNotificationIdentifier(
            @Param("notificationIdentifier") Long notificationIdentifier);
}
