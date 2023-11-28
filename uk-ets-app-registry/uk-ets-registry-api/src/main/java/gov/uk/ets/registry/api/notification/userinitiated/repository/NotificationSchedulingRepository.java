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
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationSchedulingRepository extends JpaRepository<Notification, Long> {


    @Modifying
    @Query(value = "update notification " +
        "set status = ad_hoc_status.updated_status " +
        "from (select n.id as notification_id," +
        "       case when n.start_date_time <= ?1 and n.end_date_time >= ?1 then 'ACTIVE' " +
        "       when n.start_date_time <= ?1 and n.end_date_time is null then 'ACTIVE' " +
        "       when n.start_date_time > ?1  then 'PENDING' " +
        "       when n.end_date_time < ?1 then 'EXPIRED' " +
        "       else 'EXPIRED' " +
        "       end as updated_status " +
        "from notification n inner join notification_definition nd on n.notification_definition_id = nd.id " +
        "where nd.type = 'AD_HOC' and n.status <> 'EXPIRED') as ad_hoc_status " +
        "where notification.id = ad_hoc_status.notification_id ", nativeQuery = true)
    void changeAdHocNotificationsStatus(LocalDateTime dateTime);

    /**
     * The first when checks if there is a non-recurring notification that has already been sent once.
     * If so it must be set to EXPIRED (this is for the case of old notifications, instead of a migration).
     */
    @Modifying
    @Query(value = "update notification " +
        "set status = compliance_status.updated_status " +
        "from (select n.id as notification_id," +
        "       case " +
        "       when n.start_date_time <= ?1 and n.end_date_time is null and n.run_every_x_days is null and " +
        "            n.times_fired = 1 then 'EXPIRED'" +
        "       when n.start_date_time <= ?1 and n.end_date_time >= ?1 then 'ACTIVE' " +
        "       when n.start_date_time <= ?1 and n.end_date_time is null then 'ACTIVE' " +
        "       when n.start_date_time > ?1  then 'PENDING' " +
        "       when n.end_date_time < ?1 then 'EXPIRED' " +
        "       else 'EXPIRED' " +
        "       end as updated_status " +
        "from notification n inner join notification_definition nd on n.notification_definition_id = nd.id " +
        "where nd.type in(" +
        "'EMISSIONS_MISSING_FOR_OHA','SURRENDER_DEFICIT_FOR_OHA','EMISSIONS_MISSING_FOR_AOHA'," +
        "'SURRENDER_DEFICIT_FOR_AOHA', 'YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS'" +
        ")  " +
        "and n.status <> 'EXPIRED') as compliance_status " +
        "where notification.id = compliance_status.notification_id ", nativeQuery = true)
    void changeComplianceNotificationsStatus(LocalDateTime dateTime);

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
