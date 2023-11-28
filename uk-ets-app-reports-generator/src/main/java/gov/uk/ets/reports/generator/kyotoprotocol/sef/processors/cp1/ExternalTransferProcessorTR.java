package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
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

		SEFSubmission sefSubmissionSource = null;
					
		sefSubmissionSource = getOrCreateSubmission(sefSubmissions, entry.getTransferringRegistryCode());
		
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
		
		if (transactionYear < reportedYear) {
			updateTable1(sefSubmissionSource, entry.getTransferringAccountType(), entry.getUnitTypeCode(),
					-entry.getAmount());
		}

		if (transactionYear == reportedYear) {
			updateTable2b(sefSubmissionSource, entry.getAcquiringRegistryCode(),
					AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(), entry.getAmount());
			
			updateTable2c(sefSubmissionSource, AdditionSubtractionTypeEnum.SUBTRACTION, entry.getUnitTypeCode(), entry.getAmount());
		}

		updateAnnualTransaction(sefSubmissionSource, (short) transactionYear,
				EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.SUBTRACTION,
				entry.getAmount());

		updateTable4(sefSubmissionSource, entry.getTransferringAccountType(), entry.getUnitTypeCode(),
				-entry.getAmount());

		return sefSubmissions;
	}

}
