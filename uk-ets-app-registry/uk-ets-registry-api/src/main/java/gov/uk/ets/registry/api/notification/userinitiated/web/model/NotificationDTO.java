package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import java.time.LocalDateTime;
import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidNotification
// This is not strictly needed, but it is better to explicitly define that the field-level validations
// should run before the class-level (hence the 'NotificationDTO.class') validations.
@GroupSequence({FieldChecks.class, NotificationDTO.class})
public class NotificationDTO {

    @NotNull(message = "The notification type is mandatory", groups = FieldChecks.class)
    private NotificationType type;

    /**
     * Unfortunately, this annotation can only be used here and not inside ActivationDetails.
     * From Javadoc of EXTERNAL_PROPERTY:
     *
     * <p> <blockquote>"can only be used for properties, not for types (classes)"</blockquote></p>
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "type",
        defaultImpl = ComplianceActivationDetails.class
    )
    @JsonSubTypes(
        {
            @JsonSubTypes.Type(value = AdHocActivationDetails.class, name = "AD_HOC")
        })
    @NotNull(message = "The notification activation details are mandatory", groups = FieldChecks.class)
    @Valid
    private ActivationDetails activationDetails;

    @NotNull(message = "The notification content details are mandatory", groups = FieldChecks.class)
    @Valid
    private ContentDetails contentDetails;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00")
    private LocalDateTime lastUpdated;

    private String updatedBy;

    private NotificationStatus status;

    private Integer tentativeRecipients;
}
