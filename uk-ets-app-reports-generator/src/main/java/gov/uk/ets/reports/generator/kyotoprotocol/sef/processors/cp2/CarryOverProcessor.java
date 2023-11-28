package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLAccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * Applies the change of a carry-over transaction to the SEFSubmission. Please
 * note that as SEF Reports are CP specific, the carry-over transaction either
 * creates units or destroy them depending on the reporting CP and the new
 * applicable period code of the carried-over units
 * 
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

		if (transactionYear < reportedYear) {
			updateTable1(sefSubmission, entry.getAcquiringAccountType(), entry.getUnitTypeCode(),
					entry.getApplicablePeriodCode() == reportCP ? entry.getAmount() : -entry.getAmount());
		}

		if (entry.getApplicablePeriodCode() == reportCP) {
			if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT) {
				updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.ADDITION, StartingValueEnum.CARRY_OVER_PPSR, entry.getAmount());
			} else {
				updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.ADDITION, StartingValueEnum.CARRY_OVER, entry.getAmount());

			}
		}
		
		if(entry.getApplicablePeriodCode().getCode() == reportCP.getCode() + 1) {
			//carry over to following cp
			if(entry.getAcquiringAccountType() == ITLAccountTypeEnum.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT) {
				updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, StartingValueEnum.CARRY_OVER_PPSR, entry.getAmount());
			} else {
				updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, StartingValueEnum.CARRY_OVER, entry.getAmount());
			}
			
		}

		if (entry.getApplicablePeriodCode().equals(reportCP)) {
			updateTable4(sefSubmission, entry.getAcquiringAccountType(), entry.getUnitTypeCode(), entry.getAmount());

		} else {
			updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), -entry.getAmount());
		}

		return sefSubmissions;
	}

}
