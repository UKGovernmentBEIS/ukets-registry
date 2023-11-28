package gov.uk.ets.registry.api.file.upload.emissionstable.model;

import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"identifier"})
public class SubmitEmissionsValidityInfo {

    private Long identifier;
	private AccountStatus accountStatus;
	private ComplianceStatus complianceStatus;
    private Integer startYear;
    private Integer endYear;
}
