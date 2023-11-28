package gov.uk.ets.registry.api.compliance.web.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"verifiedEmissions", "lastYearOfVerifiedEmissions"})
public class VerifiedEmissionsResultDTO {
    private List<VerifiedEmissionsDTO> verifiedEmissions;
    private Integer lastYearOfVerifiedEmissions;
}
