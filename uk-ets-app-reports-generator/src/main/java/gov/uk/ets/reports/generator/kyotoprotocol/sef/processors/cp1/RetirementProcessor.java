package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public class RetirementProcessor extends AbstractTransactionProcessor {

	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, SEFEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		if (entry.isCDMTheAcquiringRegistry()) {
			return sefSubmissions;
		}

		SEFSubmission sefSubmission = getOrCreateSubmission(sefSubmissions, entry.getAcquiringRegistryCode());

		int transactionYear = entry.getTransactionYear();

		if (transactionYear < reportedYear) {
			updateTable1(sefSubmission, entry.getAcquiringAccountType(), entry.getUnitTypeCode(), entry.getAmount());

			updateTable1(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), -entry.getAmount());
		}

		if (transactionYear == reportedYear) {
			updateRetirement(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
					entry.getAmount());
		}

		updateAnnualRetirement(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
				(short) transactionYear, entry.getAmount());

		updateTable4(sefSubmission, entry.getAcquiringAccountType(), entry.getUnitTypeCode(), entry.getAmount());

		updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), -entry.getAmount());

		return sefSubmissions;
	}

}
