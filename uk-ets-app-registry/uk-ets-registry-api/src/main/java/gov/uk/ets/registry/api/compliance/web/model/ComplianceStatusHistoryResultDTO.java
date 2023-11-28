package gov.uk.ets.registry.api.compliance.web.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"complianceStatusHistory", "lastYearOfVerifiedEmissions"})
public class ComplianceStatusHistoryResultDTO {
    private List<ComplianceStatusHistoryDTO> complianceStatusHistory;
    private Integer lastYearOfVerifiedEmissions;
}
