package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public class CarryOverProcessor extends AbstractTransactionProcessor {

	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, SEFEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		if (entry.isCDMTheAcquiringRegistry()) {
			return sefSubmissions;
		}

		SEFSubmission sefSubmission = getOrCreateSubmission(sefSubmissions, entry.getAcquiringRegistryCode());

		int transactionYear = entry.getTransactionYear();

		if (transactionYear < reportedYear && reportCP != ITLCommitmentPeriodEnum.CP1) {
			updateTable1(sefSubmission, entry.getAcquiringAccountType(), entry.getUnitTypeCode(),
					entry.getApplicablePeriodCode() == reportCP ? entry.getAmount() : -entry.getAmount());
		}

		if (entry.getApplicablePeriodCode() == reportCP) {
			updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
					AdditionSubtractionTypeEnum.ADDITION, StartingValueEnum.CARRY_OVER, entry.getAmount());
		}


		if (entry.getApplicablePeriodCode().equals(reportCP)) {
			updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), entry.getAmount());
		} else {
			updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), -entry.getAmount());
		}

		return sefSubmissions;
	}

}
