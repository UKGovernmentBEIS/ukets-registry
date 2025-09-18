package gov.uk.ets.registry.api.notification.userinitiated.repository;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.AircraftOperatorParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.InstallationParameters;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationSchedulingRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Query(value = "UPDATE notification n " +
        "SET status = temp.updated_status " +
        "FROM (SELECT n.id AS notification_id, " +
        "             CASE " +
        "                 WHEN nd.type <> 'AD_HOC' AND n.start_date_time <= :dateTime AND n.end_date_time IS NULL " +
        "                   AND n.run_every_x_days IS NULL AND n.times_fired = 1 THEN 'EXPIRED' " +
        "                 WHEN n.start_date_time <= :dateTime AND n.end_date_time >= :dateTime THEN 'ACTIVE' " +
        "                 WHEN n.start_date_time <= :dateTime AND n.end_date_time IS NULL THEN 'ACTIVE' " +
        "                 WHEN n.start_date_time > :dateTime THEN 'PENDING' " +
        "                 WHEN n.end_date_time < :dateTime THEN 'EXPIRED' " +
        "                 ELSE 'EXPIRED' " +
        "             END AS updated_status " +
        "      FROM notification n " +
        "      INNER JOIN notification_definition nd ON n.notification_definition_id = nd.id " +
        "      WHERE nd.type IN :types AND n.status not in ('CANCELLED','EXPIRED')) AS temp " +
        "WHERE n.id = temp.notification_id", nativeQuery = true)
    void changeNotificationsStatus(@Param("dateTime") LocalDateTime dateTime, @Param("types") List<String> types);

    @Query(
        "select n from Notification n join fetch n.definition " +
            "where n.definition.type in ?1 " +
            "and n.status = gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus.ACTIVE"
    )
    List<Notification> getActiveNotifications(List<NotificationType> types);

    @Query(
        "select new gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters(" +
            "u.urid, u.firstName, u.lastName, u.disclosedName, " +
            " a.id, a.identifier, a.fullIdentifier," +
            " ah.firstName, ah.lastName, ah.name, " +
            "current_date, year(current_date)" +
            ") " +
            " from Account a " +
            "         inner join a.accountAccesses aa " +
            "         inner join aa.user u " +
            "         inner join a.accountHolder ah " +
            " where a.accountType in ?1 " +
            "  and a.accountStatus in ?2 " +
            "  and aa.user.state in ?3 " +
            "  and aa.state in ?4 " +
            "  and a.complianceStatus in ?5 " +
            "  and aa.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED"
    )
    List<BaseNotificationParameters> getBasicNotificationParameters(List<String> types,
                                                                    List<AccountStatus> accountStatuses,
                                                                    List<UserStatus> userStatuses,
                                                                    List<AccountAccessState> arStatuses,
                                                                    List<ComplianceStatus> complianceStatuses);

    @Query(
        "select distinct new gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters(" +
            "u.urid, u.firstName, u.lastName, u.disclosedName, " +
            "null,null,null,null,null,null, " +
            "current_date, year(current_date)" +
            ") " +
            " from Account a " +
            "         inner join a.accountAccesses aa " +
            "         inner join aa.user u " +
            "         inner join a.accountHolder ah " +
            " where u.urid in ?1 " +
            "  and a.accountStatus in ?2 " +
            "  and aa.user.state in ?3 " +
            "  and aa.state in ?4 " +
            "  and aa.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED"
    )
    List<BaseNotificationParameters> getUserInactivityNotificationParameters( List<String> validUserIds,
                                                                                List<AccountStatus> accountStatuses,
                                                                                List<UserStatus> userStatuses,
                                                                                List<AccountAccessState> arStatuses);
    
    
    /**
     * We need to join unrelated entities, so we use an explicit join.
     */
    @Query(
        "select new gov.uk.ets.registry.api.notification.userinitiated.messaging.model.InstallationParameters(" +
            "a.id, i.installationName, i.permitIdentifier" +
            ") " +
            "from Installation i inner join Account a on i.id = a.compliantEntity.id " +
            "where a.id in ?1"
    )
    List<InstallationParameters> getInstallationParams(List<Long> accountIds);

    /**
     * We need to join  unrelated entities, so we use an explicit join.
     */
    @Query(
        "select new gov.uk.ets.registry.api.notification.userinitiated.messaging.model.AircraftOperatorParameters(" +
            "a.id, ao.monitoringPlanIdentifier, ao.identifier" +
            ") " +
            "from AircraftOperator ao inner join Account a on ao.id = a.compliantEntity.id " +
            "where a.id in ?1"
    )
    List<AircraftOperatorParameters> getAircraftOperatorParameters(List<Long> accountIds);
}
