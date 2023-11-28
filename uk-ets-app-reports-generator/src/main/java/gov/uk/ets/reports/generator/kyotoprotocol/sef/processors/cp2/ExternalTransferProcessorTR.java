package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.TransferredContributedEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AdaptationFundTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLAccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLUnitTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.ConfigLoader;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public class ExternalTransferProcessorTR extends AbstractTransactionProcessor {

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, SEFEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		SEFSubmission sefSubmissionSource = getOrCreateSubmission(sefSubmissions, entry.getTransferringRegistryCode());
		
		if (sefSubmissionSource == null) {
			return sefSubmissions;
		}

		int transactionYear = entry.getTransactionYear();
		
		/* SEFCOLLAB-419 */
		if (entry.isCDMTheTransferringRegistry()) {
			if (transactionYear == reportedYear) {
				updateTable2b(sefSubmissionSource, entry.getAcquiringRegistryCode(),
						AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(), entry.getAmount());
			}
			return sefSubmissions;
		}

		updateTablesByTransactionYear(transactionYear, reportedYear, entry, sefSubmissionSource);
		
		updateAnnualTransaction(sefSubmissionSource, (short) transactionYear,
				EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.SUBTRACTION,
				entry.getAmount());
																	 
		if (entry.getTransferringAccountType() == ITLAccountTypeEnum.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT) {
			
			updateAnnualTransactionPPSR(sefSubmissionSource, entry.getUnitTypeCode(), AdditionSubtractionTypeEnum.SUBTRACTION, (short) entry.getTransactionYear(), entry.getAmount());
		}
				
		updateTable4(sefSubmissionSource, entry.getTransferringAccountType(), entry.getUnitTypeCode(),
				-entry.getAmount());

		
		/* SEFCOLLAB-342 */
		if (entry.isCDMTheAcquiringRegistry()
				&& (entry.getAcquiringAccountType() == ITLAccountTypeEnum.CCS_NET_REVERSAL_CANCELLATION_ACCOUNT || entry
						.getAcquiringAccountType() == ITLAccountTypeEnum.CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT)) {

			updateAnnualReplacementsCancellation(sefSubmissionSource,
					EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), (short) entry.getTransactionYear(),
					entry.getAmount());

		}
		
		return sefSubmissions;
	}

	/**
	 * Updates the proper tables according to the transaction year
	 * @param transactionYear the transaction year
	 * @param reportedYear the reported year
	 * @param entry the {@link SEFEntry}
	 * @param sefSubmissionSource the {@link SEFSubmission} source
	 */
	private void updateTablesByTransactionYear(int transactionYear, short reportedYear, SEFEntry entry, SEFSubmission sefSubmissionSource) {
		if (transactionYear < reportedYear) {
			updateTable1(sefSubmissionSource, entry.getTransferringAccountType(), entry.getUnitTypeCode(),
					-entry.getAmount());
		}

		if (transactionYear == reportedYear) {
			AdaptationFundTypeEnum adaptationFundTypeEnum = "TRACK_1".equals(entry.getTrack()) ?
					AdaptationFundTypeEnum.ISSUANCE_ERU_FROM_PARTY_VERIFIED_PROJECTS : AdaptationFundTypeEnum.ISSUANCE_INDEPENDENTLY_VERIFIED_ERU;

			updateTable2b(sefSubmissionSource, entry.getAcquiringRegistryCode(), AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(),
					entry.getAmount());

			if (entry.getTransferringAccountType() == ITLAccountTypeEnum.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT) {
				updateTable2c(sefSubmissionSource, entry.getAcquiringRegistryCode(), AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(),
						entry.getAmount());
			}
			updateTable2e(sefSubmissionSource, AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(), entry.getAmount());

			/* SEFCOLLAB-343 */
			if (entry.isFirstAauTransferFlag() && entry.getOriginalPeriodCode().compareTo(ITLCommitmentPeriodEnum.CP2) >= 0) {
				/* SEFCOLLAB-340  Check the countries that are not part of the aggregation */
				if (!ConfigLoader.getConfigLoader().isInAggregatedMode() || !ConfigLoader.getConfigLoader().getReportedRegistries().contains(entry.getAcquiringRegistryCode())) {
					updateTable2d(sefSubmissionSource, AdaptationFundTypeEnum.FIRST_INTERNATIONAL_TRANSFERS_OF_AAU, TransferredContributedEnum.TRANSFERRED, ITLUnitTypeEnum.AAU, entry.getAmount());
				}
			}

			updateIfRegistryIsCDM(entry, sefSubmissionSource, adaptationFundTypeEnum);
		}
	}

	/**
	 * Updates the tables if the Registry is CDM
	 * @param entry the {@link SEFEntry}
	 * @param sefSubmissionSource the {@link SEFSubmission}
	 * @param adaptationFundTypeEnum the {@link AdaptationFundTypeEnum}
	 */
	private void updateIfRegistryIsCDM(SEFEntry entry, SEFSubmission sefSubmissionSource, AdaptationFundTypeEnum adaptationFundTypeEnum) {
		/* SEFCOLLAB-330 */
		if (entry.isCDMTheAcquiringRegistry()) {

			if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.PARTY_HOLDING_ACCOUNT) {

				if (entry.getUnitTypeCode() == ITLUnitTypeEnum.AAU) {

					updateTable2d(sefSubmissionSource, AdaptationFundTypeEnum.FIRST_INTERNATIONAL_TRANSFERS_OF_AAU,
							TransferredContributedEnum.CONTRIBUTED, ITLUnitTypeEnum.AAU, entry.getAmount());

				} else if (entry.getUnitTypeCode() == ITLUnitTypeEnum.ERU_FROM_AAU || entry.getUnitTypeCode() == ITLUnitTypeEnum.ERU_FROM_RMU) {

					updateTable2d(sefSubmissionSource, adaptationFundTypeEnum, TransferredContributedEnum.CONTRIBUTED, entry.getUnitTypeCode(),
							entry.getAmount());

				}

			} else if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.CCS_NET_REVERSAL_CANCELLATION_ACCOUNT) {

				updateCancellation(sefSubmissionSource, TransactionOrEventTypeEnum.CC_SUBJECT_TO_NET_REVERSAL_OF_STORAGE,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());

			} else if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT) {

				updateCancellation(sefSubmissionSource, TransactionOrEventTypeEnum.CC_SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT,
						EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), entry.getAmount());
			}
		}
	}
}
