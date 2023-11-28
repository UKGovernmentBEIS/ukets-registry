package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.RequirementForReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table3;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLUnitTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.UnitBlockEntry;

/**
 * Reports expired units in holding accounts as {@link Table3} {@link RequirementForReplacement}.
 *
 * @author kattoulp
 */
public class ExpiredToHoldingAccountProcessor extends AbstractTransactionProcessor {

	private static final String EXPIRED_ACCOUNT_MESSAGE = "Expired in Holding Account";

	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, UnitBlockEntry entry) {

		if (entry.getUnitTypeCode() != ITLUnitTypeEnum.TCER && entry.getUnitTypeCode() != ITLUnitTypeEnum.LCER) {
			throw new IllegalArgumentException("Invalid ITL Unit Type value.");
		}

		CerTypeEnum cerType = CerTypeEnum.fromValue(entry.getUnitTypeCode().getDescription());

		SEFSubmission sefSubmission = getOrCreateSubmission(sefSubmissions, entry.getRegistry());

		if (entry.getExpiredYear().intValue() == getReportYear()) {

			TransactionOrEventTypeEnum transactionTypeOrEvent = cerType == CerTypeEnum.T_CER ?
					TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS :
					TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS;

			updateRequirement(sefSubmission, transactionTypeOrEvent, cerType, entry.getAmount());
		}

		updateAnnualReplacementRequirement(sefSubmission, cerType, entry.getExpiredYear(), entry.getAmount());

		return sefSubmissions;
	}

}
