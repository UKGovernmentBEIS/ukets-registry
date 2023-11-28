package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLCommitmentPeriodEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLNotificationTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.Cp2NotificationEntry;
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
		
		if (ITLNotificationTypeEnum.getFromCode(entry.getNotificationTypeCode()) == ITLNotificationTypeEnum.REVERSAL_STORAGE_CDM
				|| ITLNotificationTypeEnum.getFromCode(entry.getNotificationTypeCode()) == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM) {

			updateAnnualReplacementRequirement(sefSubmission, CerTypeEnum.L_CER, entry.getYear(), entry.getQuantity());

			if (reportedYear == entry.getYear()) {
				updateRequirement(sefSubmission,
						EnumConverter.getEventTypeFromNotification(entry.getITLNotificationTypeCode(), ReportFormatEnum.CP2),
						CerTypeEnum.L_CER, entry.getQuantity());
			}

		}
		
		if (ITLNotificationTypeEnum.getFromCode(entry.getNotificationTypeCode()) == ITLNotificationTypeEnum.NET_REVERSAL_STORAGE_CDM_CCS_PROJECT
				|| ITLNotificationTypeEnum.getFromCode(entry.getNotificationTypeCode()) == ITLNotificationTypeEnum.NON_SUBM_CERT_REPORT_CDM_CCS_PROJECT) {
			
			updateAnnualReplacementRequirement(sefSubmission, CerTypeEnum.CER, entry.getYear(), entry.getQuantity());
			
			if (reportedYear == entry.getYear()) {
				updateRequirement(sefSubmission,
						EnumConverter.getEventTypeFromNotification(entry.getITLNotificationTypeCode(), ReportFormatEnum.CP2),
						CerTypeEnum.CER, entry.getQuantity());
			}
		}
		
		if (ITLNotificationTypeEnum.getFromCode(entry.getNotificationTypeCode()) == ITLNotificationTypeEnum.IMPENDING_TCER_LCER_EXPIRY) {
			Cp2NotificationEntry cp2Entry = (Cp2NotificationEntry) entry;
			CerTypeEnum xsdCerType = CerTypeEnum.fromValue(cp2Entry.getUnitTypeCode().getDescription());
			
			updateAnnualReplacementRequirement(sefSubmission, xsdCerType, entry.getYear(), entry.getQuantity());

			if (reportedYear == entry.getYear()) {
				TransactionOrEventTypeEnum txOrEventType = xsdCerType == CerTypeEnum.T_CER ? TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS
						: TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS;
				updateRequirement(sefSubmission, txOrEventType, xsdCerType, entry.getQuantity());
			}
			
		}
		
		return sefSubmissions;
	}

}
