package gov.uk.ets.registry.api.notification.userinitiated.repository;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.DashboardNotificationDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationResultsRepository {


    @Query("select n from Notification n join fetch n.definition")
    List<Notification> findAllWithDefinitions();

    @Query("select n from Notification n join fetch n.definition " +
        "where n.status = gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus.ACTIVE " +
        "and n.definition.type = ?1")
    Optional<Notification> findActiveNotificationByType(NotificationType type);

    @Query("select n from Notification n join fetch n.definition where n.id = ?1")
    Optional<Notification> getByIdWithDefinition(Long id);

    /**
     * Note that we do not use fetch join here because we do not have a column from the fetch relationship
     * in the select clause (relevant error: 'query specified join fetching,
     * but the owner of the fetched association was not present in the select list').
     */
    @Query("select new gov.uk.ets.registry.api.notification.userinitiated.web.model" +
        ".DashboardNotificationDTO(n.longText, n.schedule.startDateTime) " +
        "from Notification n " +
        "         join n.definition nd " +
        "where n.status = gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus.ACTIVE " +
        "  and nd.type = gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType.AD_HOC " +
        "order by n.schedule.startDateTime desc")
    List<DashboardNotificationDTO> findDashBoardNotifications();


}
