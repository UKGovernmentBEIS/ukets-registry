package gov.uk.ets.registry.api.notification.integration;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountModificationSuccessOutcomeNotificationProperties {

    @NotBlank
    private String subject;
}
