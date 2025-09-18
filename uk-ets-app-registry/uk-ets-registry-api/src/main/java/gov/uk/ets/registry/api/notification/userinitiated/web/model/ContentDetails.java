package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import jakarta.validation.constraints.NotNull;
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
public class ContentDetails {

    @NotNull(message = "The notification subject is mandatory")
    private String subject;

    @NotNull(message = "The notification content is mandatory")
    private String content;
}
