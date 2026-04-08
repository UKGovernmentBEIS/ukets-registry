package gov.uk.ets.registry.api.notification;

import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AccountWithholdUpdateGroupNotification extends EmailNotification {

    private OperationEvent integrationPoint;
    private String registryId;
    private Boolean withholdFlag;
    private Year year;

    @Builder
    public AccountWithholdUpdateGroupNotification(Set<String> recipients, GroupNotificationType type,
                                                  OperationEvent integrationPoint, String registryId,
                                                  Boolean withholdFlag, Year year) {
        super(recipients, type, null, null, null);
        this.integrationPoint = integrationPoint;
        this.registryId = registryId;
        this.withholdFlag = withholdFlag;
        this.year = year;
    }
}
