package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UnitBlockFilter {
    private Long accountIdentifier;
    private UnitType unitType;
    private CommitmentPeriod applicablePeriod;
    private CommitmentPeriod originalPeriod;
    private Boolean subjectToSoap;
}
