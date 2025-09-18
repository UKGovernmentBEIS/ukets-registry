package gov.uk.ets.registry.api.notification.userinitiated.messaging.model;

import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationParameterHolder {
    /**
     * needed to correlate with definition
     */
    private Long notificationId;
    @ToString.Exclude
    private String email;
    /**
     * The generated id (e.g. 1-1-1)
     */
    private String notificationInstanceId;
    /**
     * the recurrence id (aka timesFired + 1) value, used in the event
     */
    private Long recurrenceId;

    private BaseNotificationParameters baseNotificationParameters;
    private InstallationParameters installationParameters;
    private AircraftOperatorParameters aircraftOperatorParameters;
    private Long balance;
    /**
     * Holds the extracted header values from a single row of an uploaded AdHocEmailRecipients CSV file.
     * The keys in the map correspond to column headers, and the values represent the associated data for that row.
     */
    private Map<String, Object> csvRowData = new HashMap<>();

    /**
     * Constructs the accountHolder parameters (i.e. its name) using first and last name if present,
     * name otherwise.
     */
    public AccountHolderParameters getAccountHolderParameters() {
        return AccountHolderParameters.builder()
            .name(
                baseNotificationParameters.getAccountHolderFirstName() != null ?
                    baseNotificationParameters.getAccountHolderFirstName() + " " +
                        baseNotificationParameters.getAccountHolderLastName() :
                    baseNotificationParameters.getAccountHolderName()
            )
            .build();
    }

    /**
     * Creates a dummy NotificationParameterHolder with populated fields for testing.
     *
     * @return A NotificationParameterHolder instance with dummy data.
     */
    public static NotificationParameterHolder getDefaultParameterHolder(NotificationType notificationType) {
        switch (notificationType) {
            case USER_INACTIVITY:
                return getBaseNotificationBuilder(false).build();
            case YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS:
                return getBaseNotificationBuilder(true).build();
            case EMISSIONS_MISSING_FOR_AOHA:
            case SURRENDER_DEFICIT_FOR_AOHA:
                return getBaseNotificationBuilder(true)
                    .aircraftOperatorParameters(AircraftOperatorParameters.builder()
                        .accountId(1L)
                        .monitoringPlan("")
                        .id(1L)
                        .build())
                    .build();
            case EMISSIONS_MISSING_FOR_OHA:
            case SURRENDER_DEFICIT_FOR_OHA:
                return getBaseNotificationBuilder(true)
                    .installationParameters(InstallationParameters.builder()
                        .accountId(1L)
                        .name("")
                        .permitId("")
                        .build())
                    .build();
            default:
                throw new IllegalArgumentException("Notification type " + notificationType + " is not applicable.");
        }
    }

    private static NotificationParameterHolderBuilder getBaseNotificationBuilder(boolean isAccountBased) {
        NotificationParameterHolderBuilder builder = NotificationParameterHolder.builder()
            .notificationId(1L)
            .email("")
            .notificationInstanceId("")
            .recurrenceId(1L)
            .balance(1L);
        BaseNotificationParameters.BaseNotificationParametersBuilder baseParams = BaseNotificationParameters.builder()
            .urid("")
            .firstName("")
            .lastName("")
            .disclosedName("")
            .currentDate(new Date())
            .currentYear(2023);
        if (isAccountBased) {
            baseParams
                .accountIdentifier(1L)
                .accountFullIdentifier("UK-100-1006-3-58")
                .accountHolderFirstName("")
                .accountHolderLastName("")
                .accountHolderName("");
        }
        return builder.baseNotificationParameters(baseParams.build());
    }
}
