package gov.uk.ets.registry.api.itl.reconciliation.service;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.Builder;
import uk.gov.ets.lib.commons.kyoto.types.Total;

/**
 * Needed for usage in JPA queries because of discrepancy between property types between entity object and SOAP model.
 */
public class TotalDto extends Total {

    /**
     * Delegates to parent setters to convert between incompatible types.
     */
    @Builder
    public TotalDto(
        KyotoAccountType accountType,
        CommitmentPeriod accountCommitPeriod,
        UnitType unitType,
        long unitCount) {
        this.setAccountType(accountType.getCode());
        this.setAccountCommitPeriod(accountCommitPeriod.getCode());
        this.setUnitType(unitType.getPrimaryCode());
        this.setSuppUnitType(unitType.getSupplementaryCode());
        this.setUnitCount(unitCount);
    }
}
