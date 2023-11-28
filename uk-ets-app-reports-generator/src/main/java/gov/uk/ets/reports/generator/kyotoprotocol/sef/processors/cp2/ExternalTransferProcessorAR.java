package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import  gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLAccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public class ExternalTransferProcessorAR extends AbstractTransactionProcessor {

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, SEFEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		SEFSubmission sefSubmissionTarget = null;
		
		sefSubmissionTarget = getOrCreateSubmission(sefSubmissions, entry.getAcquiringRegistryCode());		
		
		

		if (sefSubmissionTarget == null) {
			return sefSubmissions;
		}

		int transactionYear = entry.getTransactionYear();

		/* SEFCOLLAB-419 */
		if (entry.isCDMTheAcquiringRegistry()) {
			if (transactionYear == reportedYear) {
				updateTable2b(sefSubmissionTarget, entry.getTransferringRegistryCode(),
						AdditionSubtractionTypeEnum.ADDITION, entry.getUnitTypeCode(), entry.getAmount());
			}
			
			return sefSubmissions;
		}
		
		if (transactionYear < reportedYear) {
			updateTable1(sefSubmissionTarget, entry.getAcquiringAccountType(), entry.getUnitTypeCode(),
					entry.getAmount());
		}

		if (transactionYear == reportedYear) {			
				updateTable2b(sefSubmissionTarget, entry.getTransferringRegistryCode(),
						AdditionSubtractionTypeEnum.ADDITION, entry.getUnitTypeCode(), entry.getAmount());
			if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT) {
				updateTable2c(sefSubmissionTarget, entry.getTransferringRegistryCode(),
						AdditionSubtractionTypeEnum.ADDITION, entry.getUnitTypeCode(), entry.getAmount());
			}
				
				updateTable2e(sefSubmissionTarget, AdditionSubtractionTypeEnum.ADDITION, entry.getUnitTypeCode(), entry.getAmount());
		}
		
		updateAnnualTransaction(sefSubmissionTarget, (short) transactionYear,
				EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.ADDITION,
				entry.getAmount());
	
		if (entry.getAcquiringAccountType() == ITLAccountTypeEnum.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT) {
			updateAnnualTransactionPPSR(sefSubmissionTarget, entry.getUnitTypeCode(), AdditionSubtractionTypeEnum.ADDITION, (short) entry.getTransactionYear(), entry.getAmount());
		}
		
		updateTable4(sefSubmissionTarget, entry.getAcquiringAccountType(), entry.getUnitTypeCode(),
				entry.getAmount());
		
		return sefSubmissions;
	}

}
