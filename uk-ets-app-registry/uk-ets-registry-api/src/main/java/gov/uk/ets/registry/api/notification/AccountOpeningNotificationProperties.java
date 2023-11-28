package gov.uk.ets.registry.api.notification;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountOpeningNotificationProperties {


    @NotBlank
    private String approveSubject;

    @NotBlank
    private String rejectSubject;

    @NotBlank
    private String arSubject;

    @NotBlank
    private String proposalSubject;
}
