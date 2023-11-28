package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLAccountTypeEnum;
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
			
			if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.IMPENDING_TCER_LCER_EXPIRY) {
				
				/* SEFCOLLAB-334 */
				TransactionTypeEnum transTypeUpdateTransactionType = TransactionTypeEnum.REPLACEMENT_EXPIREDT_CE_RS;
				TransactionOrEventTypeEnum transTypeUpdateReplacement = TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS;
				
				if (entry.getAcquiringAccountType() != ITLAccountTypeEnum.TCER_REPLACEMENT_ACCOUNT_EXPIRY) {
					transTypeUpdateTransactionType = TransactionTypeEnum.REPLACEMENT_EXPIREDL_CE_RS;
					transTypeUpdateReplacement = TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS;
				}
				
				/* Call Update Transaction Type (Table 2a) */ 
				updateTransactionType(sefSubmission, transTypeUpdateTransactionType, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
				
				/* Call Update Replacement (Table 3)  */
				updateReplacement(sefSubmission, transTypeUpdateReplacement, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());
			}
			
			if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.REVERSAL_STORAGE_CDM) {
				updateTransactionType(sefSubmission, TransactionTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
				updateReplacement(sefSubmission, TransactionOrEventTypeEnum.SUBJECT_TO_REVERSAL_OF_STORAGE,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());
			}

			if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM) {
				updateTransactionType(sefSubmission, TransactionTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
				updateReplacement(sefSubmission,
						TransactionOrEventTypeEnum.SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());
			}

		}

		updateTable2e(sefSubmission, AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(), entry.getAmount());
		
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
