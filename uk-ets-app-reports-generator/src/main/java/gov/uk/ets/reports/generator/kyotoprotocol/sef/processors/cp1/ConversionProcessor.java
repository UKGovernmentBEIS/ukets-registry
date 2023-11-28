package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import java.util.Map;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.*;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.SEFEntry;

/**
 * @author gkountak
 * 
 */
public class ConversionProcessor extends AbstractTransactionProcessor {

	private static final int UNIT_CONVERSION_DIFF = 2;

	@Override
	public Map<String, SEFSubmission> process(Map<String, SEFSubmission> sefSubmissions, SEFEntry entry,
											  ITLCommitmentPeriodEnum reportCP, short reportedYear) {

		SEFSubmission sefSubmission = getOrCreateSubmission(sefSubmissions, entry.getAcquiringRegistryCode());

		if (entry.isCDMTheAcquiringRegistry()) {
			return sefSubmissions;
		}

		int transactionYear = entry.getTransactionYear();

		if (transactionYear < reportedYear) {

			updateTable1(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), entry.getAmount());

			updateTable1(sefSubmission, entry.getTransferringAccountType(),
					ITLUnitTypeEnum.getFromCode(entry.getUnitTypeCode().getCode() - UNIT_CONVERSION_DIFF),
					-entry.getAmount());
		}

		/* Update table2a transaction type */
		if (transactionYear == reportedYear) {
			TransactionTypeEnum txTypeEnum = null;
			if ("TRACK_1".equals(entry.getTrack())) {
				txTypeEnum = TransactionTypeEnum.ISSUANCE_CONVERSION_OF_PARTY_VERIFIED_PROJECTS;
			} else {
				txTypeEnum = TransactionTypeEnum.INDEPENDENTLY_VERIFIED_PROJECTS;
			}
			updateTransactionType(sefSubmission, txTypeEnum, EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()),
					AdditionSubtractionTypeEnum.ADDITION, entry.getAmount());
			updateTransactionType(
					sefSubmission,
					txTypeEnum,
					EnumConverter.getXSDUnitTypeEnum(ITLUnitTypeEnum.getFromCode(entry.getUnitTypeCode().getCode()
							- UNIT_CONVERSION_DIFF)), AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());
			
			updateTable2c(sefSubmission, AdditionSubtractionTypeEnum.ADDITION, entry.getUnitTypeCode(), entry.getAmount());
			updateTable2c(sefSubmission, AdditionSubtractionTypeEnum.SUBTRACTION, 
					ITLUnitTypeEnum.getFromCode(entry.getUnitTypeCode().getCode() - UNIT_CONVERSION_DIFF), entry.getAmount());

		}

		updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode(), entry.getAmount());

		updateTable4(sefSubmission, entry.getTransferringAccountType(), entry.getUnitTypeCode().getPreConvertedType(),
				-entry.getAmount());
		
		updateAnnualTransaction(sefSubmission, (short) transactionYear,
				EnumConverter.getXSDUnitTypeEnum(entry.getUnitTypeCode()), AdditionSubtractionTypeEnum.ADDITION,
				entry.getAmount());

		updateAnnualTransaction(
				sefSubmission,
				(short) transactionYear,
				EnumConverter.getXSDUnitTypeEnum(ITLUnitTypeEnum.getFromCode(entry.getUnitTypeCode().getCode()
						- UNIT_CONVERSION_DIFF)), AdditionSubtractionTypeEnum.SUBTRACTION, entry.getAmount());

		return sefSubmissions;
	}

}
