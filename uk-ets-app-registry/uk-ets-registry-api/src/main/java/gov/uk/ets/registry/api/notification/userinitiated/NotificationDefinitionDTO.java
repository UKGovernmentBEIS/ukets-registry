package gov.uk.ets.registry.api.notification.userinitiated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDefinitionDTO {

    private String shortText;

    private String longText;

    private Integer tentativeRecipients;
}
