package gov.uk.ets.registry.api.compliance.web.model;

import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComplianceOverviewDTO {
	private Long totalVerifiedEmissions = 0L;
	private Long totalNetSurrenders = 0L;
	private ComplianceStatus currentComplianceStatus;
}
