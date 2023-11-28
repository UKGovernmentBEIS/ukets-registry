package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.FirstTrack2TransferEntry;

/**
 * 
 * @author gkountak
 */
public class FirstTrack2TransferProcessor extends AbstractTransactionProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions,
											  FirstTrack2TransferEntry entry, ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		SEFSubmission sefSubmission = getOrCreateSubmission(sefSubmissions, entry.getRegistry());
		updateAdditionalInformation(sefSubmission, entry.getQuantity());
		return sefSubmissions;
	}

}
