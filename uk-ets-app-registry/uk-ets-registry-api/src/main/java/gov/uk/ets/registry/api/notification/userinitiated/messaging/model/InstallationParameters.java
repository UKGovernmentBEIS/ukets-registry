package gov.uk.ets.registry.api.notification.userinitiated.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InstallationParameters {

    private Long accountId;
    private String name;
    private String permitId;
}
