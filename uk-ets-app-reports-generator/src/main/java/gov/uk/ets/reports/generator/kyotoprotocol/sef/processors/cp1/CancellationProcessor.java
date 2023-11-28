package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.*;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public class CancellationProcessor extends AbstractTransactionProcessor {

	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, SEFEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		if (entry.isCDMTheAcquiringRegistry()) {
			return sefSubmissions;
		}

		SEFSubmission sefSubmission = getOrCreateSubmission(sefSubmissions, entry.getAcquiringRegistryCode());

		int transactionYear = entry.getTransactionYear();
		
		ITLAccountTypeEnum acc = resolveAccountType(entry);
		
		if (transactionYear < reportedYear) {
						
			updateTable1(sefSubmission, acc, entry.getUnitTypeCode(), entry.getAmount());

			updateTable1(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), -entry.getAmount());
		}

		if (transactionYear == reportedYear) {
			if (entry.getNotifLulucfCode() != null) {
				updateTransactionType(sefSubmission,
						EnumConverter.getTransactionTypeFromLuluCFCode(entry.getNotifLulucfCode()),
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
			}

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
			
			updateTable2c(sefSubmission, AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(), entry.getAmount());
			
			if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.VOLUNTARY_CANCELLATION_ACCOUNT ||
					(entry.getAcquiringAccountType() == ITLAccountTypeEnum.MANDATORY_CANCELLATION_ACCOUNT &&
							entry.getNotificationTypeCode() == null)) {
				updateTransactionType(sefSubmission, TransactionTypeEnum.OTHER_CANCELLATION,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
			}
		}
		
		updateTable4(sefSubmission, acc, entry.getUnitTypeCode(), entry.getAmount());
		
		updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), -entry.getAmount());

		updateAnnualTransaction(sefSubmission, (short) transactionYear,
				EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.SUBTRACTION,
				entry.getAmount());
		
		return sefSubmissions;
	}

	/**
	 * Resolves the Account type
	 *
	 * @param entry the {@link SEFEntry}
	 * @return the account's {@link ITLAccountTypeEnum} value
	 */
	private ITLAccountTypeEnum resolveAccountType(SEFEntry entry) {
		ITLAccountTypeEnum acc = entry.getAcquiringAccountType();

		/* If entry.notificationTypeCode = 4 (reversal of storage) then ACC = 422, if entry.notificationTypeCode = 5
		* (non-submission of certification) then ACC = 423, else ACC = entry.acquiringAccountType
		*/
		if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.REVERSAL_STORAGE_CDM) {
			acc = ITLAccountTypeEnum.LCER_REPLACEMENT_ACCOUNT_REVERSAL_OF_STORAGE;
		} else if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM) {
			acc = ITLAccountTypeEnum.LCER_REPLACEMENT_ACCOUNT_FAIL_SUBMISSION_CERT_REP;
		}

		return acc;
	}

}
