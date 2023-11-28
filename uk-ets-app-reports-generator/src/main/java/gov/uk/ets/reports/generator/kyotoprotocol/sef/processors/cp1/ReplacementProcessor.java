package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLNotificationTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public class ReplacementProcessor extends AbstractTransactionProcessor {

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
			if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.REVERSAL_STORAGE_CDM) {
				updateTransactionType(sefSubmission, TransactionTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
				updateReplacement(sefSubmission, TransactionOrEventTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());
			}

			if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM) {
				updateTransactionType(sefSubmission, TransactionTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
				updateReplacement(sefSubmission,
						TransactionOrEventTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());
			}

		}

		updateTable2c(sefSubmission, AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(), entry.getAmount());
		
		updateAnnualReplacementsReplacement(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
				(short) transactionYear, entry.getAmount());

		updateTable4(sefSubmission, entry.getAcquiringAccountType(), entry.getUnitTypeCode(), entry.getAmount());

		updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), -entry.getAmount());
		
		updateAnnualTransaction(sefSubmission, (short) transactionYear,
				EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.SUBTRACTION,
				entry.getAmount());

		return sefSubmissions;
	}

}
