package gov.uk.ets.registry.api.transaction;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;

import java.util.Date;

public class CommonTransactionObjectsBuilder {

    public static UnitBlock uniBlock(String registry,
            Long accountIdentifier, UnitType type, CommitmentPeriod period, Long start,
            Long end, String project, EnvironmentalActivity environmentalActivity) {
        UnitBlock block = new UnitBlock();
        block.setOriginatingCountryCode(registry);
        block.setAccountIdentifier(accountIdentifier);
        block.setAcquisitionDate(new Date());
        block.setApplicablePeriod(period);
        block.setOriginalPeriod(period);
        block.setStartBlock(start);
        block.setEndBlock(end);
        block.setType(type);
        block.setProjectNumber(project);
        block.setEnvironmentalActivity(environmentalActivity);
        return block;
    }

}
