package gov.uk.ets.registry.api.notification.integration;

import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Date;
import java.util.Set;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
@Setter
public class AccountOpeningSuccessOutcomeNotification extends AccountModificationOutcomeNotification {

    @Builder
    public AccountOpeningSuccessOutcomeNotification(Set<String> recipients,
                                                    String integrationPoint,
                                                    String correlationId,
                                                    String accountFullIdentifier,
                                                    String emitterId,
                                                    String accountName,
                                                    String accountType,
                                                    Date date,
                                                    SourceSystem sourceSystem) {
        super(recipients,
                GroupNotificationType.INTEGRATION_ACCOUNT_OPENING_SUCCESS_OUTCOME,
                integrationPoint,
                correlationId,
                accountFullIdentifier,
                emitterId,
                accountName,
                accountType,
                date,
                sourceSystem);
    }
}