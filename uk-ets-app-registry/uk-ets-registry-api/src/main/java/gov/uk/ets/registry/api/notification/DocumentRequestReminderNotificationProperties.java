package gov.uk.ets.registry.api.notification;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentRequestReminderNotificationProperties {

    @NotBlank
    private String subject;
}
