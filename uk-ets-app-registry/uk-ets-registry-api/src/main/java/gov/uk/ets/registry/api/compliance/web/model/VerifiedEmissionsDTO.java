package gov.uk.ets.registry.api.compliance.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"compliantEntityId", "year", "lastUpdated"})
public class VerifiedEmissionsDTO {
    private Long compliantEntityId;
    private Long year;
    private String reportableEmissions;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00")
    private LocalDateTime lastUpdated;
}
