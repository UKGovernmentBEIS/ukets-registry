package gov.uk.ets.registry.api.itl.reconciliation.service;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.Builder;
import uk.gov.ets.lib.commons.kyoto.types.UnitBlock;

public class UnitBlockDto extends UnitBlock {

    /**
     * Delegates to parent setters to convert between incompatible types.
     */
    @QueryProjection
    @Builder
    public UnitBlockDto(
        Long startBlock,
        Long endBlock,
        String originatingRegistryCode,
        UnitType unitType,
        KyotoAccountType accountType,
        Long accountIdentifier,
        CommitmentPeriod applicablePeriod
    ) {
        this.setUnitSerialBlockStart(startBlock);
        this.setUnitSerialBlockEnd(endBlock);
        this.setOriginatingRegistryCode(originatingRegistryCode);
        this.setUnitType(unitType.getPrimaryCode());
        this.setSuppUnitType(unitType.getSupplementaryCode());
        this.setAccountType(accountType.getCode());
        this.setAccountIdentifier(accountIdentifier);
        this.setApplicableCommitPeriod(applicablePeriod.getCode());
    }
}
