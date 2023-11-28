package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl;

import lombok.Getter;
import lombok.Setter;

/**
 * The Notification Item for the RITL3
 */
@Getter
@Setter
public class RREG3NotificationItem {
    /**
     * The database id
     */
    private String notificationId;

    /**
     * The identifier
     */
    private String notificationIdentifier;

    /**
     * The type code of notification
     */
    private String notificationTypeCode;

    /**
     * The type code of cancelation
     */
    private String cancelation;

    /**
     * The date of the registry
     */
    private String registryNotificationDate;

    /**
     * The date of the action
     */
    private String actionDueDate;

    /**
     * The target Value of notification
     */
    private String targetValue;

    /**
     * The target Date of notification
     */
    private String targetDate;

    /**
     * The postTargetDate Value of notification
     */
    private String postTargetDate;

    /**
     * The carryOver Value of notification
     */
    private String replacement;

    private String projectNumber;

}
