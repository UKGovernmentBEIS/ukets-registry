package gov.uk.ets.reports.generator.kyotoprotocol.sef.processors.cp1;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.AdditionSubtractionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.RF1TableFactory;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.CerQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.TableUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.TotalUnitQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.UnitQtyUtil;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.ExternalTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table1;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.*;
import gov.uk.ets.reports.generator.kyotoprotocol.sef.util.*;

import java.util.List;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualRetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualTransaction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Replacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.RequirementForReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Retirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValue;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Subtractions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table2B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table2C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table4;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table5C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalTxOrEvent;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;


/**
 * Used to handle all the update operations as mentioned in the ReportGeneratorFunctions.docx
 *
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

        UnitQty unitQty = TableUtil.getUnitQty(table1, EnumConverter.getXSDAccountTypeEnum(accountType, ReportFormatEnum.CP1),
                EnumConverter.getXSDUnitTypeEnum(unitType));

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
            et = RF1TableFactory.createExternalTransfer(partnerRegistryCode);
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
     * Update {@link Table2C}
     * @param sefSubmission
     * @param addSubType
     * @param unitType
     * @param amount
     */
    public void updateTable2c(SEFSubmission sefSubmission, AdditionSubtractionTypeEnum addSubType, ITLUnitTypeEnum unitType, Long amount) {

        Table2C table2c = sefSubmission.getTable2C();

        if (addSubType == AdditionSubtractionTypeEnum.ADDITION) {
            UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(table2c.getAdditions().getUnitQty(),
                    EnumConverter.getXSDUnitTypeEnum(unitType));
            updateUnitQty(amount, uq);

        } else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
            UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(table2c.getSubtractions().getUnitQty(),
                    EnumConverter.getXSDUnitTypeEnum(unitType));
            updateUnitQty(amount, uq);
        }
    }

    /**
     * Update {@link Table4}
     *
     * @param sefSubmissions
     * @param registryCode
     * @param accountType
     * @param unitType
     * @param amount
     */
    public void updateTable4(SEFSubmission sefSubmission, ITLAccountTypeEnum accountType, ITLUnitTypeEnum unitType, Long amount) {

        Table4 table4 = sefSubmission.getTable4();
        UnitQty unitQty = TableUtil.getUnitQty(table4, EnumConverter.getXSDAccountTypeEnum(accountType, ReportFormatEnum.CP1),
                EnumConverter.getXSDUnitTypeEnum(unitType));

        updateUnitQty(amount, unitQty);

        UnitQty totalUnitQty = TotalUnitQtyUtil.getUnitQtyByUnitType(table4.getTotalUnitQty(),
                EnumConverter.getXSDUnitTypeEnum(unitType));
        updateUnitQty(amount, totalUnitQty);

    }

    /**
     * Updates the starting values.
     *
     * @param sefSubmissions
     * @param registryCode
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
            UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getStartingValues()
                    .getSubTotal().getAdditions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, subQ);
            UnitQty totalQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getTotalAdditionSubtraction()
                    .getAdditions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, totalQ);
        } else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
            UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(sv.getSubtractions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, uq);
            UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getStartingValues()
                    .getSubTotal().getSubtractions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, subQ);
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
     * @param sefSubmissions
     * @param acquiringRegistryCode
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
     * @param sefSubmissions
     * @param registryCode
     * @param transactionOrEventTypeEnum
     * @param xsdUnitTypeEnum
     * @param amount
     */
    public void updateReplacement(SEFSubmission sefSubmission, TransactionOrEventTypeEnum transactionOrEventTypeEnum, UnitTypeEnum xsdUnitTypeEnum, Long amount) {

        TransactionOrEventType txEvType = TableUtil.getTransactionOrEventTypeByType(sefSubmission.getTable3()
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

        TransactionOrEventType txOrEvent = TableUtil.getTransactionOrEventTypeByType(sefSubmission.getTable3().getTransactionOrEventType(),
                transactionOrEventType);

        CerQty cerQ = CerQtyUtil.getCerQtyByCerType(txOrEvent.getRequirementForReplacement().getCerQty(), cerType);
        updateCerQty(amount, cerQ);

        // Note that SEFCOLLAB-347 prevents from updating the totals in sefSubmission.getTable3().getTotalTxOrEvent().getRequirementForReplacement().getCerQty()
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

        AnnualReplacement ar = TableUtil.getAnnualReplacementByType(sefSubmission.getTable5B().getAnnualReplacements()
                .getAnnualReplacement(), year);

        UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(ar.getReplacement().getUnitQty(), xsdUnitTypeEnum);
        updateUnitQty(amount, uq);

        UnitQty tuq = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5B().getTotalTxOrEvent().getReplacement()
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

        AnnualReplacement ar = TableUtil.getAnnualReplacementByType(sefSubmission.getTable5B().getAnnualReplacements().getAnnualReplacement(), year);

        CerQty unit = CerQtyUtil.getCerQtyByCerType(ar.getRequirementForReplacement().getCerQty(), cerTypeEnum);
        updateCerQty(amount, unit);

        CerQty total = CerQtyUtil.getCerQtyByCerType(sefSubmission.getTable5B().getTotalTxOrEvent().getRequirementForReplacement().getCerQty(),
                cerTypeEnum);
        updateCerQty(amount, total);

    }

    /**
     * Update {@link AnnualTransaction} object
     *
     * @param sefSubmissions
     * @param registryCode
     * @param transactionYear
     * @param xsdUnitTypeEnum
     * @param addSubType
     * @param amount
     */
    public void updateAnnualTransaction(SEFSubmission sefSubmission, short transactionYear, UnitTypeEnum xsdUnitTypeEnum, AdditionSubtractionTypeEnum addSubType, Long amount) {

        AnnualTransaction at = TableUtil.getAnnualTransactionByYear(sefSubmission.getTable5A().getAnnualTransactions()
                .getAnnualTransaction(), transactionYear);

        if (addSubType == AdditionSubtractionTypeEnum.ADDITION) {
            UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(at.getAdditions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, uq);
            UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getAnnualTransactions()
                    .getSubTotal().getAdditions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, subQ);
            UnitQty totalQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getTotalAdditionSubtraction()
                    .getAdditions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, totalQ);

        } else if (addSubType == AdditionSubtractionTypeEnum.SUBTRACTION) {
            UnitQty uq = UnitQtyUtil.getUnitQtyByUnitType(at.getSubtractions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, uq);
            UnitQty subQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getAnnualTransactions()
                    .getSubTotal().getSubtractions().getUnitQty(), xsdUnitTypeEnum);
            updateUnitQty(amount, subQ);
            UnitQty totalQ = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5A().getTotalAdditionSubtraction()
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
        AnnualRetirement ar = TableUtil.getAnnualRetirement(sefSubmission.getTable5C().getAnnualRetirements()
                .getAnnualRetirement(), year);

        UnitQty unitQty = UnitQtyUtil.getUnitQtyByUnitType(ar.getUnitQty(), xsdUnitTypeEnum);
        updateUnitQty(amount, unitQty);

        UnitQty total = UnitQtyUtil.getUnitQtyByUnitType(sefSubmission.getTable5C().getTotalUnitQty().getUnitQty(),
                xsdUnitTypeEnum);
        updateUnitQty(amount, total);
    }

    /**
     * Update Retirement Updates a {@link Retirement} contained in
     * {@link Table2a}
     *
     * @param sefSubmission
     *            the SEFSubmission to be processed
     * @param xsdUnitTypeEnum
     *            Value of UnitQtyEnum
     * @param amount
     *            the amount by which the value of the UnitQty must be modified,
     *            this value can be positive or negative
     */
    void updateRetirement(SEFSubmission sefSubmission, UnitTypeEnum xsdUnitTypeEnum, Long amount) {
        Retirement retirement = sefSubmission.getTable2A().getRetirement();

        UnitQty unitQty = UnitQtyUtil.getUnitQtyByUnitType(retirement.getUnitQty(), xsdUnitTypeEnum);
        updateUnitQty(amount, unitQty);
    }

    /**
     * Updates a {@link Table2B}
     *
     * @param sefSubmission
     *            the SEFSubmission to be processed
     * @param amount
     *            the amount by which the value of the UnitQty must be modified,
     *            this value can be positive or negative
     */
    void updateAdditionalInformation(SEFSubmission sefSubmission, Long amount) {
        Subtractions subtractions = sefSubmission.getTable2B().getAdditionalInformation().getSubtractions();

        UnitQty unitQty = UnitQtyUtil.getUnitQtyByUnitType(subtractions.getUnitQty(), UnitTypeEnum.ERU);
        updateUnitQty(amount, unitQty);

    }

}
