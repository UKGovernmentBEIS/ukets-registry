package gov.uk.ets.registry.api.notification.userinitiated.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
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

    @NonNull
    private BaseNotificationParameters baseNotificationParameters;
    private InstallationParameters installationParameters;
    private AircraftOperatorParameters aircraftOperatorParameters;
    private Long balance;

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
}
