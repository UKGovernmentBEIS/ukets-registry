package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLUnitTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public class IssuanceProcessor extends AbstractTransactionProcessor {

	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, SEFEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		if (entry.isCDMTheAcquiringRegistry()) {
			return sefSubmissions;
		}

		int transactionYear = entry.getTransactionYear();

		SEFSubmission sefSubmission = getOrCreateSubmission(sefSubmissions, entry.getAcquiringRegistryCode());

		if (transactionYear < reportedYear) {
			updateTable1(sefSubmission, entry.getAcquiringAccountType(), entry.getUnitTypeCode(), entry.getAmount());
		}

		updateTable4(sefSubmission, entry.getAcquiringAccountType(), entry.getUnitTypeCode(), entry.getAmount());

		if (entry.getUnitTypeCode().equals(ITLUnitTypeEnum.AAU)) {
			updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
					AdditionSubtractionTypeEnum.ADDITION, StartingValueEnum.ASSIGNED_AMOUNT_UNITS_ISSUED,
					entry.getAmount());
		}

		if (entry.getUnitTypeCode().equals(ITLUnitTypeEnum.RMU)) {
			updateAnnualTransaction(sefSubmission, (short) transactionYear,
					EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.ADDITION,
					entry.getAmount());
			
			if (transactionYear == reportedYear) {
				updateTransactionType(sefSubmission,
						EnumConverter.getTransactionTypeFromLuluCFCode(entry.getBlockLulucfCode()),
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.ADDITION,
						entry.getAmount());
				
				updateTable2e(sefSubmission, AdditionSubtractionTypeEnum.ADDITION, entry.getUnitTypeCode(),
						entry.getAmount());
			}
		}

		return sefSubmissions; 
	}
}
