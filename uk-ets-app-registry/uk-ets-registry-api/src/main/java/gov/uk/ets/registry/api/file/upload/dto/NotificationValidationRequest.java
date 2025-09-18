package gov.uk.ets.registry.api.file.upload.dto;

import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.FieldChecks;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationValidationRequest {

    private Long fileId;

    @NotNull(message = "The notification type is mandatory", groups = FieldChecks.class)
    private NotificationType type;

    @NotNull(message = "The notification email body is mandatory", groups = FieldChecks.class)
    private String body;

}
