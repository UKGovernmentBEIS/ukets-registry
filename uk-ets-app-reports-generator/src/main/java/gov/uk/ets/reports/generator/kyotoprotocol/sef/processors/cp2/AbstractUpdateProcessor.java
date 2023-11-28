package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp2;

import java.util.List;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.RF2TableFactory;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.TransferredContributedEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.CerQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.TableUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.TotalUnitQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.UnitQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.ExternalTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Replacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.RequirementForReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValue;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AdaptationFundType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AdaptationFundTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualRetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransaction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransactionPPSR;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransactionsPPSR;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Cancellation;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.PPSRTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Retirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.RetirementTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table1;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2D;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2E;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table4;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TotalTxOrEvent;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.EnumConverter;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLAccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLUnitTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.Quantity;

/**
 * Used to handle all the update operations as mentioned in the ReportGeneratorFunctions.docx
 *
 * @author gkountak
 */
public class AbstractUpdateProcessor {

	public AbstractUpdateProcessor() {
		super();
	}

	/**
	 * This function updates an account holding. An account holding is an
	 * UnitQty having a given type contained in an AccountType having a given
	 * name contained in either a Table1 or a Table4 contained in a
	 * SEFSubmission of a given registry.
	 * 
	 * @param sefSubmission
	 *            the SEFSubmission to be processed
	 * @param accountType
	 *            code indicating the name of the concerned Account Type object
	 *            within the SEFSubmission
	 * @param unitType
	 *            code indicating the type of the UnitQty concerned within the
	 *            Account Type
	 * @param amount
	 *            the amount by which the value of the UnitQty must be modified,
	 *            this value can be positive or negative
	 */
	public void updateTable1(SEFSubmission sefSubmission, ITLAccountTypeEnum accountType, ITLUnitTypeEnum unitType, Long amount) {
	
		Table1 table1 = sefSubmission.getTable1();
	
		UnitQty unitQty = TableUtil.getUnitQty(table1, EnumConverter.getXSDAccountTypeEnum(accountType, ReportFormatEnum.CP2),
				EnumConverter.getXSDUnitTypeEnum(unitType));
	
		updateUnitQty(amount, unitQty);
	
		UnitQty totalUnitQty = TotalUnitQtyUtil.getUnitQtyByUnitType(table1.getTotalUnitQty(),
				EnumConverter.getXSDUnitTypeEnum(unitType));
		updateUnitQty(amount, totalUnitQty);
	
	}
	
	/**
	 * 
	 * @param sefSubmission
	 * @param accountType
	 * @param unitType
	 * @param amount
	 */
	public void updateTable1(SEFSubmission sefSubmission, AccountTypeEnum accountType, ITLUnitTypeEnum unitType, Long amount) {
		Table1 table1 = sefSubmission.getTable1();
		
		UnitQty unitQty = TableUtil.getUnitQty(table1, accountType,	EnumConverter.getXSDUnitTypeEnum(unitType));
	
		updateUnitQty(amount, unitQty);
	
		UnitQty totalUnitQty = TotalUnitQtyUtil.getUnitQtyByUnitType(table1.getTotalUnitQty(),
				EnumConverter.getXSDUnitTypeEnum(unitType));
		updateUnitQty(amount, totalUnitQty);
	}

	/**
	 * Update {@link Table2B}
	 * @param sefSubmission
	 * @param partnerRegistryCode
	 * @param addSubType
	 * @param unitType
	 * @param amount
	 */
	public void updateTable2b(SEFSubmission sefSubmission, String partnerRegistryCode, AdditionSubtractionTypeEnum addSubType, ITLUnitTypeEnum unitType, Long amount) {
	
		List<ExternalTransfer> externalTransfers = sefSubmission.getTable2B().getExternalTransfer();
		ExternalTransfer et = TableUtil.getExternalTransferByRegistry(externalTransfers, partnerRegistryCode);
		if (et == null) {
			et = RF2TableFactory.createExternalTransfer(partnerRegistryCode);
			externalTransfers.add(et);
		}
	
		if (addSubType == AdditionSubtractionTypeEnum.ADDITION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(et.getAdditions().getUnitQty(),
					EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, uq);
			UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable2B().getSubTotal().getAdditions()
					.getUnitQty(), EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, subQ);
	
		} else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(et.getSubtractions().getUnitQty(),
					EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, uq);
			UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable2B().getSubTotal().getSubtractions()
					.getUnitQty(), EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, subQ);
	
		}
	}
	
	
	/**
	 * Update {@link Table2E}
	 * @param sefSubmission
	 * @param addSubType
	 * @param unitType
	 * @param amount
	 */
	public void updateTable2e(SEFSubmission sefSubmission, AdditionSubtractionTypeEnum addSubType, ITLUnitTypeEnum unitType, Long amount) {

		Table2E table2e = sefSubmission.getTable2E();
		
		if (addSubType == AdditionSubtractionTypeEnum.ADDITION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(table2e.getAdditions().getUnitQty(),
					EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, uq);
	
		} else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(table2e.getSubtractions().getUnitQty(),
					EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, uq);	
		}
	}
	
	/**
	 * Updates a {@link Table2C}
	 * 
	 * @param sefSubmission
	 * @param partnerRegistryCode
	 * @param addSubType
	 * @param unitType
	 * @param amount
	 */
	public void updateTable2c(SEFSubmission sefSubmission, String partnerRegistryCode,
			AdditionSubtractionTypeEnum addSubType, ITLUnitTypeEnum unitType, Long amount) {

		Table2C table2c = sefSubmission.getTable2C();
		PPSRTransfer ppsrTransfer = TableUtil.getPpsrTransfer(table2c.getPPSRTransfer(), partnerRegistryCode);
		if (ppsrTransfer == null) {
			ppsrTransfer = RF2TableFactory.createPpsrTransfer(partnerRegistryCode);
			table2c.getPPSRTransfer().add(ppsrTransfer);
		}

		if (addSubType == AdditionSubtractionTypeEnum.ADDITION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(ppsrTransfer.getAdditions().getUnitQty(),
					EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, uq);
			UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(table2c.getSubTotal().getAdditions().getUnitQty(),
					EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, subQ);
		} else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(ppsrTransfer.getSubtractions().getUnitQty(),
					EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, uq);
			UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(table2c.getSubTotal().getSubtractions().getUnitQty(),
					EnumConverter.getXSDUnitTypeEnum(unitType));
			updateUnitQty(amount, subQ);
		}

	}
	
	/**
	 * Updates a {@link Table2D}
	 * 
	 * @param sefSubmission
	 * @param adaptationFundType
	 * @param transferredOrContributed
	 * @param unitType
	 * @param amount
	 */
	public void updateTable2d(SEFSubmission sefSubmission, AdaptationFundTypeEnum adaptationFundType,
							  TransferredContributedEnum transferredOrContributed, ITLUnitTypeEnum unitType, Long amount) {

		AdaptationFundType adaptationFund = TableUtil.getAdaptationFund(sefSubmission.getTable2D()
				.getAdaptationFundType(), adaptationFundType);

		if(adaptationFund == null) {
			return;
		}

		List<UnitQty> unitQtyList = transferredOrContributed == TransferredContributedEnum.TRANSFERRED ?
				adaptationFund.getAmountTransferred().getUnitQty() :
				adaptationFund.getAmountContributed().getUnitQty();

		UnitQty unitQty = UnitQtyUtil.getUnitQtyByUnitType(unitQtyList,	EnumConverter.getXSDUnitTypeEnum(unitType));
		updateUnitQty(amount, unitQty);
	}
	
	/**
	 * Updates an {@link AnnualTransactionPPSR} object in a {@link Table5C} {@link AnnualTransactionsPPSR} object
	 * 
	 * @param sefSubmission
	 * @param unitType
	 * @param addSubType
	 * @param year
	 * @param amount
	 */
	public void updateAnnualTransactionPPSR(SEFSubmission sefSubmission, ITLUnitTypeEnum unitType,
			AdditionSubtractionTypeEnum addSubType, short year, Long amount) {
		if (year < 2013 || year > 2023) {
			return;
		}
		
		AnnualTransactionPPSR pat = TableUtil.getAnnualTransactionPpsr(sefSubmission.getTable5C()
				.getAnnualTransactionsPPSR().getAnnualTransactionPPSR(), year);

		if(pat == null) {
			return;
		}

		List<UnitQty> additionsOrSubtractions;
		List<UnitQty> totalAdditionsOrSubtractions;

		if(addSubType == AdditionSubtractionTypeEnum.ADDITION) {
			additionsOrSubtractions = pat.getAdditions().getUnitQty();
			totalAdditionsOrSubtractions = sefSubmission.getTable5C().getTotalAdditionSubtraction().getAdditions().getUnitQty();
		} else {
			additionsOrSubtractions = pat.getSubtractions().getUnitQty();
			totalAdditionsOrSubtractions = sefSubmission.getTable5C().getTotalAdditionSubtraction().getSubtractions().getUnitQty();
		}

		UnitQty unitQty = UnitQtyUtil.getUnitQtyByUnitType(additionsOrSubtractions,	EnumConverter.getXSDUnitTypeEnum(unitType));
		UnitQty totalUnitQty = UnitQtyUtil.getUnitQtyByUnitType(totalAdditionsOrSubtractions, EnumConverter.getXSDUnitTypeEnum(unitType));

		updateUnitQty(amount, unitQty);
		updateUnitQty(amount, totalUnitQty);
	}

	/**
	 * Update {@link Table4}
	 *
	 * @param sefSubmission
	 * @param accountType
	 * @param unitType
	 * @param amount
	 */
	public void updateTable4(SEFSubmission sefSubmission, ITLAccountTypeEnum accountType, ITLUnitTypeEnum unitType, Long amount) {
	
		Table4 table4 = sefSubmission.getTable4();
		UnitQty unitQty = TableUtil.getUnitQty(table4, EnumConverter.getXSDAccountTypeEnum(accountType, ReportFormatEnum.CP2),
				EnumConverter.getXSDUnitTypeEnum(unitType));
	
		updateUnitQty(amount, unitQty);
	
		UnitQty totalUnitQty = TotalUnitQtyUtil.getUnitQtyByUnitType(table4.getTotalUnitQty(),
				EnumConverter.getXSDUnitTypeEnum(unitType));
		updateUnitQty(amount, totalUnitQty);
	
	}

	/**
	 * Update {@link Table4}
	 *
	 * @param sefSubmission
	 * @param accountType
	 * @param unitType
	 * @param amount
	 */
	public void updateTable4(SEFSubmission sefSubmission, AccountTypeEnum accountType, ITLUnitTypeEnum unitType, Long amount) {
	
		Table4 table4 = sefSubmission.getTable4();
		UnitQty unitQty = TableUtil.getUnitQty(table4, accountType,	EnumConverter.getXSDUnitTypeEnum(unitType));

		updateUnitQty(amount, unitQty);
	
		UnitQty totalUnitQty = TotalUnitQtyUtil.getUnitQtyByUnitType(table4.getTotalUnitQty(),
				EnumConverter.getXSDUnitTypeEnum(unitType));
		updateUnitQty(amount, totalUnitQty);
	
	}

	/**
	 * Updates the starting values.
	 *
	 * @param sefSubmission
	 * @param xsdUnitTypeEnum
	 * @param addSubType
	 * @param startingValueType
	 * @param amount
	 */
	public void updateStartingValue(SEFSubmission sefSubmission, UnitTypeEnum xsdUnitTypeEnum, AdditionSubtractionTypeEnum addSubType, StartingValueEnum startingValueType, Long amount) {
	
		StartingValue sv = TableUtil.getStartingValueByType(sefSubmission.getTable5A().getStartingValues(),
				startingValueType);
		if (addSubType == AdditionSubtractionTypeEnum.ADDITION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(sv.getAdditions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, uq);
			UnitQty totalQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getTotalAdditionSubtraction()
					.getAdditions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, totalQ);
		} else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(sv.getSubtractions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, uq);
			UnitQty totalQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getTotalAdditionSubtraction()
					.getSubtractions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, totalQ);
		}
	}

	/**
	 * Updates a {@link UnitQty} with the specific amount.
	 * 
	 * @param amount
	 * @param uq
	 */
	public void updateUnitQty(Long amount, UnitQty uq) {
		Quantity quantity = new Quantity(uq.getValue());
		quantity.updateValue(amount);
		uq.setValue(quantity.getValue());
	}

	/**
	 * Updates a {@link CerQty} object with the specific amount.
	 * 
	 * @param amount
	 * @param uq
	 */
	public void updateCerQty(Long amount, CerQty uq) {
		Quantity quantity = new Quantity(uq.getValue());
		quantity.updateValue(amount);
		uq.setValue(quantity.getValue());
	}

	/**
	 * Update {@link TransactionOrEventType}
	 *
	 * @param sefSubmission
	 * @param transactionTypeEnum
	 * @param xsdUnitTypeEnum
	 * @param addSubType
	 * @param amount
	 */
	public void updateTransactionType(SEFSubmission sefSubmission, TransactionTypeEnum transactionTypeEnum, UnitTypeEnum xsdUnitTypeEnum, AdditionSubtractionTypeEnum addSubType, Long amount) {
	
		TransactionType txType = TableUtil.getTransactionTypeByType(sefSubmission.getTable2A().getTransactionType(),
				transactionTypeEnum);
		if (addSubType == AdditionSubtractionTypeEnum.ADDITION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(txType.getAdditions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, uq);
			UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable2A().getSubTotal().getAdditions()
					.getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, subQ);
	
		} else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(txType.getSubtractions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, uq);
			UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable2A().getSubTotal().getSubtractions()
					.getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, subQ);
	
		}
	}

	/**
	 * Update {@link Replacement}
	 *
	 * @param sefSubmission
	 * @param transactionOrEventTypeEnum
	 * @param xsdUnitTypeEnum
	 * @param amount
	 */
	public void updateReplacement(SEFSubmission sefSubmission, TransactionOrEventTypeEnum transactionOrEventTypeEnum, UnitTypeEnum xsdUnitTypeEnum, Long amount) {
	
		TransactionOrEventType txEvType = TableUtil.getSef2TransactionOrEventTypeByType(sefSubmission.getTable3()
				.getTransactionOrEventType(), transactionOrEventTypeEnum);
	
		UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(txEvType.getReplacement().getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, uq);
	
		TotalTxOrEvent total = sefSubmission.getTable3().getTotalTxOrEvent();
		UnitQty tuq = UnitQtyUtil.getUnitQtyByUnitType(total.getReplacement().getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, tuq);
	}

	/**
	 * Update {@link RequirementForReplacement}
	 * @param sefSubmission
	 * @param transactionOrEventType
	 * @param cerType
	 * @param amount
	 */
	public void updateRequirement(SEFSubmission sefSubmission, TransactionOrEventTypeEnum transactionOrEventType, CerTypeEnum cerType, Long amount) {
		
		TransactionOrEventType txOrEvent = TableUtil.getSef2TransactionOrEventTypeByType(sefSubmission.getTable3().getTransactionOrEventType(), 
				transactionOrEventType);
		
		CerQty cerQ = CerQtyUtil.getCerQtyByCerType(txOrEvent.getRequirementForReplacement().getCerQty(), cerType);
		updateCerQty(amount, cerQ);
		
		CerQty cerTotal = CerQtyUtil.getCerQtyByCerType(sefSubmission.getTable3().getTotalTxOrEvent().getRequirementForReplacement().getCerQty(), cerType);
		updateCerQty(amount, cerTotal);
	}
	
	/**
	 * Update {@link Cancellation}
	 * 
	 * @param sefSubmission
	 * @param transactionOrEventTypeEnum
	 * @param xsdUnitTypeEnum
	 * @param amount
	 */
	public void updateCancellation(SEFSubmission sefSubmission, TransactionOrEventTypeEnum transactionOrEventTypeEnum,
			 UnitTypeEnum xsdUnitTypeEnum, Long amount) {
		TransactionOrEventType txEvType = TableUtil.getSef2TransactionOrEventTypeByType(sefSubmission.getTable3()
				.getTransactionOrEventType(), transactionOrEventTypeEnum);

		UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(txEvType.getCancellation().getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, uq);

		TotalTxOrEvent total = sefSubmission.getTable3().getTotalTxOrEvent();
		UnitQty tuq = UnitQtyUtil.getUnitQtyByUnitType(total.getCancellation().getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, tuq);
	}
	
	
	/**
	 * 
	 * @param sefSubmission
	 * @param transactionOrEventTypeEnum
	 * @param accTypeEnum
	 * @param amount
	 */
	public void updateCancellation(SEFSubmission sefSubmission, TransactionOrEventTypeEnum transactionOrEventTypeEnum,
			AccountTypeEnum accTypeEnum, Long amount) {
		
	}

	/**
	 * Update the {@link Replacement} object in {@link AnnualReplacement}
	 *
	 * @param sefSubmission
	 * @param xsdUnitTypeEnum
	 * @param year
	 * @param amount
	 */
	public void updateAnnualReplacementsReplacement(SEFSubmission sefSubmission, UnitTypeEnum xsdUnitTypeEnum, short year, Long amount) {
		if (year < 2013 || year > 2023) {
			return;
		}
		
		AnnualReplacement ar = TableUtil.getSef2AnnualReplacementByYear(sefSubmission.getTable5D().getAnnualReplacements()
				.getAnnualReplacement(), year);
	
		UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(ar.getReplacement().getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, uq);
	
		UnitQty tuq = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5D().getTotalTxOrEvent().getReplacement()
				.getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, tuq);
	
	}

	/**
	 * Update the {@link Replacement} object in {@link AnnualReplacement} 
	 * 
	 * @param sefSubmission
	 * @param cerTypeEnum
	 * @param year
	 * @param amount
	 */
	public void updateAnnualReplacementRequirement(SEFSubmission sefSubmission, CerTypeEnum cerTypeEnum, short year, long amount) {
		if (year < 2013 || year > 2023) {
			return;
		}
		
		AnnualReplacement ar = TableUtil.getSef2AnnualReplacementByYear(sefSubmission.getTable5D().getAnnualReplacements().getAnnualReplacement(), year);
		
		CerQty unit = CerQtyUtil.getCerQtyByCerType(ar.getRequirementForReplacement().getCerQty(), cerTypeEnum);
		updateCerQty(amount, unit);
		
		CerQty total = CerQtyUtil.getCerQtyByCerType(sefSubmission.getTable5D().getTotalTxOrEvent().getRequirementForReplacement().getCerQty(), 
				cerTypeEnum);
		updateCerQty(amount, total);
		
	}
	
	/**
	 * Updates a {@link Cancellation} in {@link AnnualReplacement} object
	 * 
	 * @param sefSubmission
	 * @param xsdUnitTypeEnum
	 * @param year
	 * @param amount
	 */
	public void updateAnnualReplacementsCancellation(SEFSubmission sefSubmission, UnitTypeEnum xsdUnitTypeEnum,
			short year, Long amount) {
		if (year < 2013 || year > 2023) {
			return;
		}
		
		AnnualReplacement ap = TableUtil.getSef2AnnualReplacementByYear(sefSubmission.getTable5D()
				.getAnnualReplacements().getAnnualReplacement(), year);
		UnitQty unitQty = UnitQtyUtil.getUnitQtyByUnitType(ap.getCancellation().getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, unitQty);

		UnitQty totalUnitQty = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5D().getTotalTxOrEvent()
				.getCancellation().getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, totalUnitQty);
	}

	/**
	 * Update {@link AnnualTransaction} object
	 *
	 * @param sefSubmission
	 * @param transactionYear
	 * @param xsdUnitTypeEnum
	 * @param addSubType
	 * @param amount
	 */
	public void updateAnnualTransaction(SEFSubmission sefSubmission, short transactionYear, UnitTypeEnum xsdUnitTypeEnum, AdditionSubtractionTypeEnum addSubType, Long amount) {
		
		if (transactionYear < 2013 || transactionYear > 2023) {
			return;
		}
	
		AnnualTransaction at = TableUtil.getSef2AnnualTransactionByYear(sefSubmission.getTable5B().getAnnualTransactions()
				.getAnnualTransaction(), transactionYear);
	
		if (addSubType == AdditionSubtractionTypeEnum.ADDITION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(at.getAdditions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, uq);

			UnitQty totalQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5B().getTotalAdditionSubtraction()
					.getAdditions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, totalQ);
	
		} else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
			UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(at.getSubtractions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, uq);
			
			UnitQty totalQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5B().getTotalAdditionSubtraction()
					.getSubtractions().getUnitQty(), xsdUnitTypeEnum);
			updateUnitQty(amount, totalQ);
		}
	}
	
	/**
	 * Updates an {@link AnnualRetirement} object contained in a {@link Table5C}
	 * object
	 * 
	 * @param sefSubmission
	 *            the SEFSubmission to be processed
	 * @param xsdUnitTypeEnum
	 *            Value of UnitQtyEnum
	 * @param year
	 * @param amount
	 *            the amount by which the value of the UnitQty must be modified,
	 *            this value can be positive or negative
	 */
	void updateAnnualRetirement(SEFSubmission sefSubmission, UnitTypeEnum xsdUnitTypeEnum, short year, Long amount) {
		if (year < 2013 || year > 2023) {
			return;
		}
		
		AnnualRetirement ar = TableUtil.getSef2AnnualRetirement(sefSubmission.getTable5E().getAnnualRetirements()
				.getAnnualRetirement(), year);

		UnitQty unitQty = UnitQtyUtil.getUnitQtyByUnitType(ar.getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, unitQty);

		UnitQty total = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5E().getTotalUnitQty().getUnitQty(),
				xsdUnitTypeEnum);
		updateUnitQty(amount, total);
	}
	
	
	/**
	 * Update Retirement Updates a {@link Retirement} contained in
	 * {@link Table2A}
	 * 
	 * @param sefSubmission
	 *            the SEFSubmission to be processed
	 * @param xsdUnitTypeEnum
	 *            Value of UnitQtyEnum
	 * @param amount
	 *            the amount by which the value of the UnitQty must be modified,
	 *            this value can be positive or negative
	 */
	void updateRetirement(SEFSubmission sefSubmission, RetirementTypeEnum retirementTypeEnum,
			UnitTypeEnum xsdUnitTypeEnum, Long amount) {
		Retirement retirement = TableUtil.getRetirementByName(sefSubmission.getTable2ARetirement().getRetirement(),
				retirementTypeEnum);

		UnitQty unitQty = UnitQtyUtil.getUnitQtyByUnitType(retirement.getUnitQty(), xsdUnitTypeEnum);
		updateUnitQty(amount, unitQty);
		
		UnitQty total = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable2ARetirement().getTotalUnitQty().getUnitQty(),
				xsdUnitTypeEnum);
		updateUnitQty(amount, total);
	}

}