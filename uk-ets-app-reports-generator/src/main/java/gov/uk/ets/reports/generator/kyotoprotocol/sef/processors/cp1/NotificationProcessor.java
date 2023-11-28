package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.NotificationEntry;

/**
 * 
 * @author gkountak
 */
public class NotificationProcessor extends AbstractTransactionProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, NotificationEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		SEFSubmission sefSubmission = getOrCreateSubmission(sefSubmissions, entry.getRegistry());

		updateAnnualReplacementRequirement(sefSubmission, CerTypeEnum.L_CER, entry.getYear(), entry.getQuantity());

		if (reportedYear == entry.getYear()) {
			updateRequirement(sefSubmission,
					EnumConverter.getEventTypeFromNotification(entry.getITLNotificationTypeCode(), ReportFormatEnum.CP1), CerTypeEnum.L_CER,
					entry.getQuantity());
		}

		return sefSubmissions;
	}

}
