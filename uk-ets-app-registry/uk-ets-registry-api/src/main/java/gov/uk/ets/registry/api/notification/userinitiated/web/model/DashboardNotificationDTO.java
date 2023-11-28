package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import java.time.LocalDateTime;
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
public class DashboardNotificationDTO {
    String content;
    LocalDateTime posted;
}
