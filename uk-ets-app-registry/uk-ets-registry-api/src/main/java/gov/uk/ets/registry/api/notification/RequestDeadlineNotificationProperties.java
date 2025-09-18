package gov.uk.ets.registry.api.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestDeadlineNotificationProperties {

    @NotBlank
    private String reminderSubject;

    @NotBlank
    private String updateSubject;
}
