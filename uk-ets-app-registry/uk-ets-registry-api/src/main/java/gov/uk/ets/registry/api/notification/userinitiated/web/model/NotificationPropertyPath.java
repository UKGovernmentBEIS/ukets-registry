package gov.uk.ets.registry.api.notification.userinitiated.web.model;

public class NotificationPropertyPath {

    public static final String NOTIFICATION_ID = "notification.id";
    public static final String NOTIFICATION_SUBJECT = "notification.shortText";
    public static final String NOTIFICATION_TYPE = "notification.definition.type";
    public static final String NOTIFICATION_SCHEDULED_DATE = "notification.schedule.startDateTime";
    public static final String NOTIFICATION_EXPIRATION_DATE = "notification.schedule.endDateTime";
    public static final String NOTIFICATION_RECUR_DAYS = "notification.schedule.runEveryXDays";
    public static final String NOTIFICATION_STATUS = "notification.status";

    private NotificationPropertyPath() {}

}
