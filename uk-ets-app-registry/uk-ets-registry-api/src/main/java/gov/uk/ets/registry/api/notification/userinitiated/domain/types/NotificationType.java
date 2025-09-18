package gov.uk.ets.registry.api.notification.userinitiated.domain.types;

import java.util.List;

public enum NotificationType {
    EMISSIONS_MISSING_FOR_OHA,
    SURRENDER_DEFICIT_FOR_OHA,
    EMISSIONS_MISSING_FOR_AOHA,
    SURRENDER_DEFICIT_FOR_AOHA,
    YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS,
    AD_HOC, // dashboard registry
    AD_HOC_EMAIL,
    USER_INACTIVITY;

    public static List<NotificationType> getComplianceNotificationTypes() {
        return List.of(
            EMISSIONS_MISSING_FOR_OHA,
            SURRENDER_DEFICIT_FOR_OHA,
            EMISSIONS_MISSING_FOR_AOHA,
            SURRENDER_DEFICIT_FOR_AOHA,
            YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS
        );
    }
}
