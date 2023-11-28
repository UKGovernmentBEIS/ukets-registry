package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"type", "originalPeriod", "applicablePeriod", "subjectToSop"})
public class AccountHoldingsSummaryDTO {

    private UnitType type;
    private CommitmentPeriod originalPeriod;
    private CommitmentPeriod applicablePeriod;
    private Boolean subjectToSop;
    private Long availableQuantity;
    private Long reservedQuantity;
}
