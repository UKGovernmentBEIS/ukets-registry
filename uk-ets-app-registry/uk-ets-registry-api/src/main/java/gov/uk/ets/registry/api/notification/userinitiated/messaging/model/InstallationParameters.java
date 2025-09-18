package gov.uk.ets.registry.api.notification.userinitiated.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstallationParameters {

    private Long accountId;
    private String name;
    private String permitId;
}
