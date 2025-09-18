package gov.uk.ets.registry.api.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailChangeNotificationProperties {
    @NotBlank
    private String requestedSubject;

    @NotBlank
    private String confirmationSubject;

    @NotBlank
    private String approveSubject;

    @NotBlank
    private String rejectSubject;
}
