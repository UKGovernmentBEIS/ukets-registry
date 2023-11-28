package gov.uk.ets.registry.api.compliance.web.model;

import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(of = {"year","status"})
public class ComplianceStatusHistoryDTO {
	private Long year;
	private ComplianceStatus status;
}
