package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.*;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.DateUtil;
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

		ITLAccountTypeEnum acc = entry.getAcquiringAccountType();
		AccountTypeEnum accEnum = resolveAccountType(entry, acc);

		updateTablesByTransactionYear(accEnum, transactionYear, reportedYear, entry, sefSubmission);

		updateTable4(sefSubmission, accEnum, entry.getUnitTypeCode(), entry.getAmount());

		updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), -entry.getAmount());

		//[SEFCOLLAB-247] Calculate requirement of cancellation of expired units
		if (accEnum == AccountTypeEnum.T_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY) {
			updateRequirement(sefSubmission, TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS,
					CerTypeEnum.T_CER, entry.getAmount());
		}
		if (accEnum == AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY) {
			updateRequirement(sefSubmission, TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS,
					CerTypeEnum.L_CER, entry.getAmount());
		}
		
		/* SEFCOLLAB-337 */
		if (shouldUpdateAnnualTransaction(entry)) {
			updateAnnualTransaction(sefSubmission, (short) transactionYear,	EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.SUBTRACTION,	entry.getAmount());
		}
		
		/* SEFCOLLAB-318 */
		if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.NON_COMPLIANCE_CANCELLATION) {
			updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.SUBTRACTION, StartingValueEnum.NON_COMPLIANCE_CANCELLATION, entry.getAmount());
		}

		if (ITLAccountTypeEnum.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT.equals(entry.getAcquiringAccountType())) {
			updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
					AdditionSubtractionTypeEnum.SUBTRACTION, StartingValueEnum.ART_3_PAR_7_TER_CANCELLATIONS, entry.getAmount());

		}

		if (ITLAccountTypeEnum.AMBITION_INCREASE_CANCELLATION_ACCOUNT.equals(entry.getAcquiringAccountType())) {
			updateStartingValue(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
					AdditionSubtractionTypeEnum.SUBTRACTION, StartingValueEnum.CANCELLATION_FOLLOWING_INCREASE_AMBITION, entry.getAmount());

		}

		if (shouldUpdateAnnualReplacementCancellation(entry)) {
			updateAnnualReplacementsCancellation(sefSubmission, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), (short)entry.getTransactionYear(), entry.getAmount());
		}

		return sefSubmissions;
	}

	private boolean shouldUpdateAnnualReplacementCancellation(SEFEntry entry) {
		return ITLNotificationTypeEnum.REVERSAL_STORAGE_CDM.equals(entry.getNotificationTypeCode())
				|| ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM.equals(entry.getNotificationTypeCode())
				|| (ITLAccountTypeEnum.MANDATORY_CANCELLATION_ACCOUNT.equals(entry.getAcquiringAccountType())
				&& entry.getExpiryDate() != null
				&& (DateUtil.compareDate(entry.getTransactionStatusDatetime(), entry.getExpiryDate()) >= 0));
	}

	private boolean shouldUpdateAnnualTransaction(SEFEntry entry) {
		return entry.getAcquiringAccountType() != ITLAccountTypeEnum.NON_COMPLIANCE_CANCELLATION_ACCOUNT &&
				entry.getAcquiringAccountType() != ITLAccountTypeEnum.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT  &&
				(entry.getAcquiringAccountType() != ITLAccountTypeEnum.MANDATORY_CANCELLATION_ACCOUNT || entry.getNotificationTypeCode() == ITLNotificationTypeEnum.REVERSAL_STORAGE_CDM ||
						entry.getNotificationTypeCode() == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM);
	}

	/**
	 * Resolves the {@link AccountTypeEnum} value according to {@link SEFEntry} and {@link ITLAccountTypeEnum} values
	 * @param entry the {@code SEFEntry}
	 * @param acc the{@code ITLAccountTypeEnum}
	 * @return the resolved {@code AccountTypeEnum} value
	 */
	private AccountTypeEnum resolveAccountType(SEFEntry entry, ITLAccountTypeEnum acc) {
		AccountTypeEnum accEnum;

		if (entry.getNotificationTypeCode() ==  ITLNotificationTypeEnum.REVERSAL_STORAGE_CDM) {
			accEnum = AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_REVERSAL_OF_STORAGE;
		} else if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM) {
			accEnum = AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_NON_SUBMISSION_CERT_REPORT;
		} else {
			accEnum = EnumConverter.getXSDAccountTypeEnum(entry.getAcquiringAccountType(), ReportFormatEnum.CP2);
		}

		if (acc == ITLAccountTypeEnum.MANDATORY_CANCELLATION_ACCOUNT && entry.getNotificationTypeCode() == null) {
			if (entry.getExpiryDate() != null && entry.getTransactionStatusDatetime().after(entry.getExpiryDate())) {
				if (entry.getUnitTypeCode() == ITLUnitTypeEnum.TCER) {
					accEnum = AccountTypeEnum.T_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY;
				} else if (entry.getUnitTypeCode() == ITLUnitTypeEnum.LCER) {
					accEnum = AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY;
				}
			} else {
				accEnum = AccountTypeEnum.CANCELLATION_ACCOUNT_FOR_REMAINING_UNITS_AFTER_CARRYOVER;
			}
		}

		return accEnum;
	}

	/**
	 *
	 * @param accEnum
	 * @param transactionYear
	 * @param reportedYear
	 * @param entry
	 * @param sefSubmission
	 */
	private void updateTablesByTransactionYear(AccountTypeEnum accEnum, int transactionYear, short reportedYear, SEFEntry entry, SEFSubmission sefSubmission) {
		if (transactionYear < reportedYear) {

			updateTable1(sefSubmission, accEnum, entry.getUnitTypeCode(), entry.getAmount());

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
				updateTransactionType(sefSubmission, TransactionTypeEnum.CANCELLATION_FOR_REVERSAL_OF_STORAGE,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());

				updateCancellation (sefSubmission, TransactionOrEventTypeEnum.SUBJECT_TO_REVERSAL_OF_STORAGE,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());

			}

			if (entry.getNotificationTypeCode() == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM) {
				updateTransactionType(sefSubmission, TransactionTypeEnum.CANCELLATION_FOR_NON_SUBMISSION_OF_CERT_REPORT,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());

				updateCancellation(sefSubmission,
						TransactionOrEventTypeEnum.SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());

			}

			/* SEFCOLLAB-327 */
			if (shouldUpdateTable2e(entry)) {
				updateTable2e(sefSubmission, AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(), entry.getAmount());
			}

			if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.VOLUNTARY_CANCELLATION_ACCOUNT) {
				updateTransactionType(sefSubmission, TransactionTypeEnum.VOLUNTARY_CANCELLATION,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
			}

			if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.AMBITION_INCREASE_CANCELLATION_ACCOUNT) {
				updateTransactionType(sefSubmission, TransactionTypeEnum.ART_31_TER_QUATER_AMBITION_INCREASE_CANCELLATION, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
			}
		}
	}

	/**
	 * Resolves whether Table2E should be updated or not
	 * @param entry the {@link SEFEntry}
	 * @return true if Table2E should be updated, false otherwise
	 */
	private boolean shouldUpdateTable2e(SEFEntry entry) {
		return entry.getAcquiringAccountType() != ITLAccountTypeEnum.NON_COMPLIANCE_CANCELLATION_ACCOUNT &&
				entry.getAcquiringAccountType() != ITLAccountTypeEnum.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT &&
				(entry.getAcquiringAccountType() != ITLAccountTypeEnum.MANDATORY_CANCELLATION_ACCOUNT ||
						entry.getNotificationTypeCode() == ITLNotificationTypeEnum.REVERSAL_STORAGE_CDM ||
						entry.getNotificationTypeCode() == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM);

	}
}
