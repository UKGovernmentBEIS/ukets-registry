package gov.uk.ets.reports.generator.kyotoprotocol.commons;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Additions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.ExternalTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Replacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.RequirementForReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValue;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SubTotal;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Subtractions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalAdditionSubtraction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AdaptationFundType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AdaptationFundTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AmountContributed;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AmountTransferred;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualReplacements;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualRetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualRetirements;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransaction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransactionPPSR;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransactions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransactionsPPSR;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Cancellation;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.PPSRTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Retirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.RetirementTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.StartingValues;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table1;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2ARetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2D;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table2E;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table3;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table4;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5D;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table5E;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table6A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table6B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table6C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TotalTxOrEvent;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.RFUtil;

/**
 * @author kattoulp
 */
public class RF2TableFactory extends GenericTableFactory {

    /**
     * @return
     */
    public static Table1 createTable1() {
        Table1 table1 = new Table1();
        for (AccountTypeEnum accType : RFUtil.getAccountTypesPerFormat(/* choose the report format */2)) {
            AccountType accountType = createAccountType(accType);
            table1.getAccountType().add(accountType);
        }
        table1.setTotalUnitQty(createTotalUnitQty());
        return table1;
    }

    /**
     * Creates a {@link Table2A} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table2A} object
     */
    public static Table2A createTable2A() {
        Table2A table2a = new Table2A();
        for (TransactionTypeEnum transactionTypeName : RFUtil.getTransactionTypePerFormat(2)) {
            TransactionType tx = createTransactionType(transactionTypeName);
            table2a.getTransactionType().add(tx);
        }
        return table2a;
    }

    /**
     * @return
     */
    public static Table2ARetirement createTable2ARetirement() {
        Table2ARetirement table2aRetirement = new Table2ARetirement();
        for (RetirementTypeEnum retirementType : RetirementTypeEnum.values()) {
            Retirement retirement = createRetirement(retirementType);
            table2aRetirement.getRetirement().add(retirement);
        }
        return table2aRetirement;
    }

    /**
     * @param retirementType
     * @return
     */
    private static Retirement createRetirement(RetirementTypeEnum retirementType) {
        Retirement retirement = new Retirement();
        retirement.setName(retirementType);
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty qty = new UnitQty();
            qty.setType(unitType);
            qty.setValue(NO);
            if (retirementType == RetirementTypeEnum.RETIREMENT_FROM_PPSR && unitType != UnitTypeEnum.AAU) {
                qty.setValue(NA);
            }
            retirement.getUnitQty().add(qty);
        }
        return retirement;
    }

    /**
     * Creates a {@link Table2B} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table2B} object
     */
    public static Table2B createTable2B() {
        return new Table2B();
    }

    /**
     * Creates a {@link Table2C} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table2C} object
     */
    public static Table2C createTable2C() {
        return new Table2C();
    }

    /**
     * Creates a {@link Table2D} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table2D} object
     */
    public static Table2D createTable2D() {
        Table2D table2D = new Table2D();
        for (AdaptationFundTypeEnum adaptationFundTypeEnum : AdaptationFundTypeEnum.values()) {
            AdaptationFundType adaptationFundType = createAdaptationFundType(adaptationFundTypeEnum);
            table2D.getAdaptationFundType().add(adaptationFundType);
        }

        return table2D;
    }

    /**
     * Creates a {@link Table2E} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table2E} object
     */
    public static Table2E createTable2E() {
        Table2E table2E = new Table2E();
        Additions additions = new Additions();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(unitType);
            ub.setValue(NO);
            additions.getUnitQty().add(ub);
        }
        table2E.setAdditions(additions);

        Subtractions subtractions = new Subtractions();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(unitType);
            ub.setValue(NO);
            subtractions.getUnitQty().add(ub);
        }
        table2E.setSubtractions(subtractions);

        return table2E;
    }

    /**
     * Creates a {@link Table3} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table3} object
     */
    public static Table3 createTable3() {
        Table3 table3 = new Table3();
        for (TransactionOrEventTypeEnum name : RFUtil.getTransactionOrEventTypeEnumPerFormat(2)) {
            table3.getTransactionOrEventType().add(createTransactionOrEventType(name));
        }

        return table3;
    }

    /**
     * Creates a {@link Table4} for a SEFSubmission.
     *
     * @return the newly created {@link Table4}
     */
    public static Table4 createTable4() {
        Table4 table4 = new Table4();
        for (AccountTypeEnum accType : RFUtil.getAccountTypesPerFormat(/* choose the report format */2)) {
            AccountType accountType = createAccountType(accType);
            table4.getAccountType().add(accountType);

        }
        table4.setTotalUnitQty(createTotalUnitQty());
        return table4;
    }

    /**
     * Creates a {@link Table5A} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table5A} object
     */
    public static Table5A createTable5A() {
        Table5A table5A = new Table5A();
        table5A.setStartingValues(createStartingValues());
        return table5A;
    }

    /**
     * Creates a {@link Table5B} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @param cp the containment period
     * @return the newly created {@link Table5B} object
     */
    public static Table5B createTable5B(int cp) {
        Table5B table5B = new Table5B();
        table5B.setAnnualTransactions(createAnnualTransactions(cp));
        return table5B;
    }

    /**
     * Creates a {@link Table5C} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @param cp the containment period
     * @return the newly created {@link Table5C} object
     */
    public static Table5C createTable5C(int cp) {
        Table5C table5C = new Table5C();
        table5C.setAnnualTransactionsPPSR(createAnnualTransactionsPPSR(cp));
        return table5C;
    }

    /**
     * Creates a {@link Table5D} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @param cp the containment period
     * @return the newly created {@link Table5D} object
     */
    public static Table5D createTable5D(int cp) {
        Table5D table5D = new Table5D();
        table5D.setAnnualReplacements(createAnnualReplacements(cp));
        return table5D;
    }

    /**
     * Creates a {@link Table5E} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @param cp the containment period
     * @return the newly created {@link Table5E} object
     */
    public static Table5E createTable5E(int cp) {
        Table5E table5E = new Table5E();
        table5E.setAnnualRetirements(createAnnualRetirements(cp));
        return table5E;
    }

    /**
     * Creates a {@link PPSRTransfer} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @param registryCode
     * @return the newly created {@link PPSRTransfer} object
     */
    public static PPSRTransfer createPpsrTransfer(String registryCode) {
        PPSRTransfer ppsrTransfer = new PPSRTransfer();
        ppsrTransfer.setRegistry(registryCode);
        ppsrTransfer.setAdditions(new Additions());
        for (UnitTypeEnum utEnum : UnitTypeEnum.values()) {
            UnitQty uq = new UnitQty();
            uq.setType(utEnum);
            if (utEnum == UnitTypeEnum.AAU) {
                uq.setValue(NO);
            } else {
                uq.setValue(NA);
            }
            ppsrTransfer.getAdditions().getUnitQty().add(uq);
        }

        ppsrTransfer.setSubtractions(new Subtractions());
        for (UnitTypeEnum utEnum : UnitTypeEnum.values()) {
            UnitQty uq = new UnitQty();
            uq.setType(utEnum);
            if (utEnum == UnitTypeEnum.AAU) {
                uq.setValue(NO);
            } else {
                uq.setValue(NA);
            }
            ppsrTransfer.getSubtractions().getUnitQty().add(uq);
        }

        return ppsrTransfer;
    }

    /**
     * @param cp
     * @return
     */
    private static AnnualRetirements createAnnualRetirements(int cp) {
        AnnualRetirements annualRet = new AnnualRetirements();
        short startYear = 2013;
        short endYear = 2023;
        if (cp == 1) {
            startYear = 2008;
        }
        for (short year = startYear; year <= endYear; year++) {
            annualRet.getAnnualRetirement().add(createAnnualRetirement(year));
        }
        return annualRet;
    }

    /**
     * @param year
     * @return
     */
    private static AnnualRetirement createAnnualRetirement(short year) {
        AnnualRetirement annualRet = new AnnualRetirement();
        annualRet.setYear(year);
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(type);
            unitQty.setValue(NO);
            annualRet.getUnitQty().add(unitQty);
        }
        return annualRet;
    }

    /**
     * @param cp
     * @return
     */
    private static AnnualReplacements createAnnualReplacements(int cp) {
        AnnualReplacements annualRep = new AnnualReplacements();
        short startYear = 2013;
        short endYear = 2023;
        if (cp == 1) {
            startYear = 2008;
        }
        for (short year = startYear; year <= endYear; year++) {
            annualRep.getAnnualReplacement().add(createAnnualReplacement(year));
        }
        return annualRep;
    }

    /**
     * @param year
     * @return
     */
    private static AnnualReplacement createAnnualReplacement(short year) {
        AnnualReplacement annualRep = new AnnualReplacement();
        annualRep.setYear(year);
        RequirementForReplacement req = new RequirementForReplacement();
        for (CerTypeEnum cerType : CerTypeEnum.values()) {
            CerQty qty = new CerQty();
            qty.setType(cerType);
            qty.setValue(NO);
            req.getCerQty().add(qty);
        }
        annualRep.setRequirementForReplacement(req);
        Replacement rep = new Replacement();
        Cancellation can = new Cancellation();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty repUnit = new UnitQty();
            repUnit.setType(type);
            repUnit.setValue(NO);
            rep.getUnitQty().add(repUnit);

            UnitQty canUnit = new UnitQty();
            canUnit.setType(type);
            canUnit.setValue(NO);
            can.getUnitQty().add(canUnit);
        }

        annualRep.setReplacement(rep);
        annualRep.setCancellation(can);
        return annualRep;
    }

    /**
     * @return
     */
    private static AnnualTransactionsPPSR createAnnualTransactionsPPSR(int cp) {
        AnnualTransactionsPPSR annualTransactionsPPSR = new AnnualTransactionsPPSR();
        short startYear = 2013;
        short endYear = 2023;
        if (cp == 1) {
            startYear = 2007;
        }
        for (short year = startYear; year <= endYear; year++) {
            annualTransactionsPPSR.getAnnualTransactionPPSR().add(createAnnualTransactionPPSR(year));
        }
        return annualTransactionsPPSR;
    }

    /**
     * @param year
     * @return
     */
    private static AnnualTransactionPPSR createAnnualTransactionPPSR(short year) {
        AnnualTransactionPPSR tx = new AnnualTransactionPPSR();
        tx.setYear(year);
        Additions addition = new Additions();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unit = new UnitQty();
            unit.setType(type);
            unit.setValue(NA);
            if (type == UnitTypeEnum.AAU) {
                unit.setValue(NO);
            }
            addition.getUnitQty().add(unit);
        }
        tx.setAdditions(addition);

        Subtractions subtraction = new Subtractions();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unit = new UnitQty();
            unit.setType(type);
            unit.setValue(NA);
            if (type == UnitTypeEnum.AAU) {
                unit.setValue(NO);
            }
            subtraction.getUnitQty().add(unit);
        }
        tx.setSubtractions(subtraction);

        return tx;
    }

    /**
     * Creates an {@link AnnualTransactions} object to be inserted in a {@link Table5A} object
     *
     * @return the newly created {@link AnnualTransactions} object.
     */
    private static AnnualTransactions createAnnualTransactions(int cp) {
        AnnualTransactions txs = new AnnualTransactions();
        short startYear = 2013;
        short endYear = 2023;
        if (cp == 1) {
            startYear = 2007;
        }
        for (short year = startYear; year <= endYear; year++) {
            txs.getAnnualTransaction().add(createAnnualTransaction(year));
        }
        return txs;
    }

    /**
     * Creates an {@link AnnualTransaction} object to be inserted in an {@link AnnualTransactions} object
     *
     * @param year
     * @return the newly created {@link AnnualTransaction} object.
     */
    static AnnualTransaction createAnnualTransaction(short year) {
        AnnualTransaction tx = new AnnualTransaction();
        tx.setYear(year);

        Additions addition = new Additions();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unit = new UnitQty();
            unit.setType(type);
            unit.setValue(NO);
            addition.getUnitQty().add(unit);
        }
        tx.setAdditions(addition);

        Subtractions subtraction = new Subtractions();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unit = new UnitQty();
            unit.setType(type);
            unit.setValue(NO);
            subtraction.getUnitQty().add(unit);
        }
        tx.setSubtractions(subtraction);

        return tx;
    }

    /**
     * Creates {@link StartingValues} object to be inserted in a {@link Table5A} object
     *
     * @return the newly created {@link StartingValues} object
     */
    private static StartingValues createStartingValues() {
        StartingValues values = new StartingValues();
        for (StartingValueEnum name : RFUtil.getStartingValueEnum(2)) {
            values.getStartingValue().add(createStartingValue(name));
        }
        return values;
    }

    /**
     * Creates a {@link StartingValue} object to be inserted in a {@link StartingValues} object
     *
     * @param name
     * @return the newly created {@link StartingValue} object
     */
    private static StartingValue createStartingValue(StartingValueEnum name) {
        StartingValue startingValue = new StartingValue();
        startingValue.setName(name);
        startingValue.setAdditions(createStartingValueAdditions(name));
        startingValue.setSubtractions(createStartingValueSubtractions(name));
        return startingValue;
    }

    /**
     * Creates a {@link Subtractions} object to be inserted in a {@link StartingValue} object
     *
     * @param name
     * @return the newly created {@link Subtractions} object
     */
    private static Subtractions createStartingValueSubtractions(StartingValueEnum name) {
        Subtractions subtractions = new Subtractions();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(type);
            unitQty.setValue(NA);

            if (name == StartingValueEnum.CANCELLATION_REMAINING_UNITS_CARRY_OVER || RF2TableFactory
                    .isStartingValueApplicableForSubtractions(name, type)) {
                unitQty.setValue(NO);
            }

            subtractions.getUnitQty().add(unitQty);
        }
        return subtractions;
    }

    /**
     * @param name
     * @param type
     * @return
     */
    private static boolean isStartingValueApplicableForSubtractions(StartingValueEnum name, UnitTypeEnum type) {
        boolean isTypeAAU = (name == StartingValueEnum.ART_3_PAR_7_TER_CANCELLATIONS || name == StartingValueEnum.CARRY_OVER_PPSR
                || name == StartingValueEnum.CANCELLATION_FOLLOWING_INCREASE_AMBITION) && type == UnitTypeEnum.AAU;
        boolean isTypeNotTCerOrLCer =
                name == StartingValueEnum.NON_COMPLIANCE_CANCELLATION && (type != UnitTypeEnum.T_CER && type != UnitTypeEnum.L_CER);
        boolean isTypeCerOrEru = name == StartingValueEnum.CARRY_OVER && (type == UnitTypeEnum.ERU || type == UnitTypeEnum.CER);

        return isTypeAAU || isTypeCerOrEru || isTypeNotTCerOrLCer;
    }

    /**
     * Creates an {@link Additions} object to be inserted in a {@link StartingValue} object
     *
     * @param name
     * @return the newly created {@link Additions} object
     */
    private static Additions createStartingValueAdditions(StartingValueEnum name) {
        Additions additions = new Additions();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(type);
            unitQty.setValue(NA);

            if ((name == StartingValueEnum.ISSUANCE_PURSUANT_TO_ART_37_38 || name == StartingValueEnum.ASSIGNED_AMOUNT_UNITS_ISSUED
                    || name == StartingValueEnum.CARRY_OVER_PPSR) && type == UnitTypeEnum.AAU) {
                unitQty.setValue(NO);
            }

            if (name == StartingValueEnum.CARRY_OVER && (type == UnitTypeEnum.ERU || type == UnitTypeEnum.CER)) {
                unitQty.setValue(NO);
            }

            additions.getUnitQty().add(unitQty);
        }
        return additions;
    }

    /**
     * Creates a {@link TransactionOrEventType} object for a {@link Table3}
     *
     * @param name the name to be given to the created {@link TransactionOrEventType} object
     * @return the newly created {@link TransactionOrEventType} object
     */
    private static TransactionOrEventType createTransactionOrEventType(TransactionOrEventTypeEnum name) {
        TransactionOrEventType tx = new TransactionOrEventType();
        tx.setName(name);
        tx.setRequirementForReplacement(createTransactionOrEventTypeRequirement(name));
        tx.setReplacement(createTransactionOrEventTypeReplacement(name));
        tx.setCancellation(createTransactionOrEventTypeCancellation(name));
        return tx;
    }

    /**
     * @param name
     * @return
     */
    private static Cancellation createTransactionOrEventTypeCancellation(TransactionOrEventTypeEnum name) {
        Cancellation cancel = new Cancellation();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(type);
            ub.setValue(NA);

            if (isApplicableForTransactionCancellation(name, type)) {
                ub.setValue(NO);
            }

            cancel.getUnitQty().add(ub);
        }
        return cancel;
    }

    /**
     * @param name
     * @param type
     * @return
     */
    private static boolean isApplicableForTransactionCancellation(TransactionOrEventTypeEnum name, UnitTypeEnum type) {

        boolean isTCer = name == TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS && type == UnitTypeEnum.T_CER;
        boolean isLCer = (name == TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS
                || name == TransactionOrEventTypeEnum.SUBJECT_TO_REVERSAL_OF_STORAGE
                || name == TransactionOrEventTypeEnum.SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT) && type == UnitTypeEnum.L_CER;
        boolean isNotLCerOrTCer = (name == TransactionOrEventTypeEnum.CC_SUBJECT_TO_NET_REVERSAL_OF_STORAGE
                || name == TransactionOrEventTypeEnum.CC_SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT) && (type != UnitTypeEnum.L_CER
                && type != UnitTypeEnum.T_CER);

        return isTCer || isLCer || isNotLCerOrTCer;
    }

    /**
     * Creates a {@link Replacement} object to be included in {@link TransactionOrEventType} object
     *
     * @param name the name of the containing {@link TransactionOrEventType} object
     * @return the newly created {@link Replacement} object
     */
    private static Replacement createTransactionOrEventTypeReplacement(TransactionOrEventTypeEnum name) {
        Replacement rep = new Replacement();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(type);
            ub.setValue(NA);

            if (name == TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS && type != UnitTypeEnum.L_CER) {
                ub.setValue(NO);
            }
            if (name == TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS && (type != UnitTypeEnum.L_CER
                    && type != UnitTypeEnum.T_CER)) {
                ub.setValue(NO);
            }
            if ((name == TransactionOrEventTypeEnum.SUBJECT_TO_REVERSAL_OF_STORAGE
                    || name == TransactionOrEventTypeEnum.SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT) && type != UnitTypeEnum.T_CER) {
                ub.setValue(NO);
            }

            rep.getUnitQty().add(ub);
        }

        return rep;
    }

    /**
     * Creates an {@link RequirementForReplacement} object to be included in a {@link TransactionOrEventType} object
     *
     * @param name the name of the containing {@link TransactionOrEventType} object
     * @return the newly created {@link RequirementForReplacement} object
     */
    private static RequirementForReplacement createTransactionOrEventTypeRequirement(TransactionOrEventTypeEnum name) {
        RequirementForReplacement req = new RequirementForReplacement();
        for (CerTypeEnum type : RFUtil.getCerTypePerFormat(/* RF2 */2)) {
            CerQty cer = new CerQty();
            cer.setType(type);
            cer.setValue(NA);
            if (type == CerTypeEnum.T_CER && (name == TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS
                    || name == TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS)) {

                cer.setValue(NO);
            }
            if (RF2TableFactory.isRequirementApplicableForLCer(name, type)) {
                cer.setValue(NO);
            }

            if (type == CerTypeEnum.CER && (name == TransactionOrEventTypeEnum.CC_SUBJECT_TO_NET_REVERSAL_OF_STORAGE
                    || name == TransactionOrEventTypeEnum.CC_SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT)) {

                cer.setValue(NO);
            }
            req.getCerQty().add(cer);
        }
        return req;
    }

    /**
     * @param name
     * @param type
     * @return
     */
    private static boolean isRequirementApplicableForLCer(TransactionOrEventTypeEnum name, CerTypeEnum type) {
        boolean isSubjectTo = name == TransactionOrEventTypeEnum.SUBJECT_TO_REVERSAL_OF_STORAGE
                || name == TransactionOrEventTypeEnum.SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT;

        return type == CerTypeEnum.L_CER && (name == TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS
                || name == TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS
                || isSubjectTo);
    }

    /**
     * @param adaptationFundTypeEnum
     * @return
     */
    private static AdaptationFundType createAdaptationFundType(AdaptationFundTypeEnum adaptationFundTypeEnum) {
        AdaptationFundType type = new AdaptationFundType();
        type.setName(adaptationFundTypeEnum);
        type.setAmountTransferred(createAdaptationFundTypeAmountTransferred(adaptationFundTypeEnum));
        type.setAmountContributed(createAdaptationFundTypeAmountContributed(adaptationFundTypeEnum));
        return type;
    }

    /**
     * @param adaptationFundTypeEnum
     * @return
     */
    private static AmountContributed createAdaptationFundTypeAmountContributed(AdaptationFundTypeEnum adaptationFundTypeEnum) {
        AmountContributed amount = new AmountContributed();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(unitType);
            unitQty.setValue(NO);
            if (adaptationFundTypeEnum == AdaptationFundTypeEnum.FIRST_INTERNATIONAL_TRANSFERS_OF_AAU && unitType != UnitTypeEnum.AAU) {
                unitQty.setValue(NA);
            }
            if ((adaptationFundTypeEnum == AdaptationFundTypeEnum.ISSUANCE_ERU_FROM_PARTY_VERIFIED_PROJECTS
                    || adaptationFundTypeEnum == AdaptationFundTypeEnum.ISSUANCE_INDEPENDENTLY_VERIFIED_ERU) && unitType != UnitTypeEnum.ERU) {
                unitQty.setValue(NA);
            }
            amount.getUnitQty().add(unitQty);
        }
        return amount;
    }

    /**
     * @param adaptationFundTypeEnum
     * @return
     */
    private static AmountTransferred createAdaptationFundTypeAmountTransferred(AdaptationFundTypeEnum adaptationFundTypeEnum) {
        AmountTransferred amount = new AmountTransferred();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(unitType);
            unitQty.setValue(NO);
            if (adaptationFundTypeEnum == AdaptationFundTypeEnum.FIRST_INTERNATIONAL_TRANSFERS_OF_AAU && unitType != UnitTypeEnum.AAU) {
                unitQty.setValue(NA);
            }
            if ((adaptationFundTypeEnum == AdaptationFundTypeEnum.ISSUANCE_ERU_FROM_PARTY_VERIFIED_PROJECTS
                    || adaptationFundTypeEnum == AdaptationFundTypeEnum.ISSUANCE_INDEPENDENTLY_VERIFIED_ERU) && unitType != UnitTypeEnum.ERU) {
                unitQty.setValue(NA);
            }
            amount.getUnitQty().add(unitQty);
        }
        return amount;
    }

    /**
     * Creates a {@link TransactionType} object
     *
     * @param transactionTypeName
     * @return the newly created {@link TransactionType} object
     */
    private static TransactionType createTransactionType(TransactionTypeEnum transactionTypeName) {
        TransactionType tx = new TransactionType();
        tx.setName(transactionTypeName);

        Additions additions = createTransactionTypeAdditions(transactionTypeName);
        tx.setAdditions(additions);
        Subtractions subtractions = createTransactionTypeSubtractions(transactionTypeName);
        tx.setSubtractions(subtractions);
        return tx;
    }

    /**
     * Creates a {@link Subtractions} object to be inserted in a {@link TransactionType} object
     *
     * @param transactionTypeName
     * @return the newly created {@link Subtractions} object
     */
    private static Subtractions createTransactionTypeSubtractions(TransactionTypeEnum transactionTypeName) {
        Subtractions subtractions = new Subtractions();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = createTransactionTypeSubtractionsUnitQty(transactionTypeName, unitType);
            subtractions.getUnitQty().add(unitQty);
        }
        return subtractions;
    }

    /**
     * Creates a {@link UnitQty} object to be inserted in a {@link Subtractions} object itself part of a {@link TransactionType} object
     *
     * @param transactionTypeName
     * @param unitType
     * @return the newly created {@link UnitQty} object
     */
    private static UnitQty createTransactionTypeSubtractionsUnitQty(TransactionTypeEnum transactionTypeName, UnitTypeEnum unitType) {
        UnitQty unitQty = new UnitQty();
        unitQty.setType(unitType);
        unitQty.setValue(NO);

        boolean isTypeNotLCer = (transactionTypeName == TransactionTypeEnum.CANCELLATION_FOR_NON_SUBMISSION_OF_CERT_REPORT
                || transactionTypeName == TransactionTypeEnum.CANCELLATION_FOR_REVERSAL_OF_STORAGE) && unitType != UnitTypeEnum.L_CER;

        if (RF2TableFactory.isTypeNotAAUOrRMUForSubtractions(transactionTypeName, unitType) ||
                RF2TableFactory.isTypeLCerOrTCerForSubtractions(transactionTypeName, unitType) ||
                isTypeNotLCer) {
            unitQty.setValue(NA);
        }

        return unitQty;
    }

    /**
     * @param transactionTypeName
     * @param unitType
     * @return
     */
    private static boolean isTypeNotAAUOrRMUForSubtractions(TransactionTypeEnum transactionTypeName, UnitTypeEnum unitType) {
        boolean isTypeNotAAUOrRMU = (transactionTypeName == TransactionTypeEnum.ISSUANCE_CONVERSION_OF_PARTY_VERIFIED_PROJECTS
                || transactionTypeName == TransactionTypeEnum.INDEPENDENTLY_VERIFIED_PROJECTS) && (unitType != UnitTypeEnum.AAU
                && unitType != UnitTypeEnum.RMU);

        boolean isTypeNotAAU =
                (transactionTypeName == TransactionTypeEnum.ART_31_TER_QUATER_AMBITION_INCREASE_CANCELLATION) && unitType != UnitTypeEnum.AAU;

        return isTypeNotAAU || isTypeNotAAUOrRMU;
    }

    /**
     * @param transactionTypeName
     * @param unitType
     * @return
     */
    private static boolean isTypeLCerOrTCerForSubtractions(TransactionTypeEnum transactionTypeName, UnitTypeEnum unitType) {
        boolean isTypeLCerOrTCer =
                (RF2TableFactory.isTransactionTypeART(transactionTypeName) || transactionTypeName == TransactionTypeEnum.REPLACEMENT_EXPIREDL_CE_RS)
                        && (unitType == UnitTypeEnum.T_CER || unitType == UnitTypeEnum.L_CER);

        boolean isTypeLCer = transactionTypeName == TransactionTypeEnum.REPLACEMENT_EXPIREDT_CE_RS && unitType == UnitTypeEnum.L_CER;

        boolean isTypeTCer = (transactionTypeName == TransactionTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT
                || transactionTypeName == TransactionTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE) && unitType == UnitTypeEnum.T_CER;

        return isTypeLCerOrTCer || isTypeLCer || isTypeTCer;
    }

    /**
     * Creates an Additions object to be inserted in a Transaction Type object
     *
     * @param transactionTypeName
     */
    private static Additions createTransactionTypeAdditions(TransactionTypeEnum transactionTypeName) {
        Additions additions = new Additions();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = createTransactionTypeAdditionUnitQty(transactionTypeName, unitType);
            additions.getUnitQty().add(unitQty);
        }
        return additions;
    }

    /**
     * This function creates an Unit Qty object to be inserted in an Addition object itself part of a Transaction Type object
     *
     * @param transactionTypeName
     * @param unitType
     */
    private static UnitQty createTransactionTypeAdditionUnitQty(TransactionTypeEnum transactionTypeName, UnitTypeEnum unitType) {
        UnitQty unitQty = new UnitQty();
        unitQty.setType(unitType);
        unitQty.setValue(NO);

        if (transactionTypeName == TransactionTypeEnum.ISSUANCE_CONVERSION_OF_PARTY_VERIFIED_PROJECTS
                || transactionTypeName == TransactionTypeEnum.INDEPENDENTLY_VERIFIED_PROJECTS) {
            if (unitType != UnitTypeEnum.ERU) {
                unitQty.setValue(NA);
            }
        } else if (isTransactionTypeART(transactionTypeName)) {
            if (unitType != UnitTypeEnum.RMU) {
                unitQty.setValue(NA);
            }
        } else {
            unitQty.setValue(NA);
        }

        return unitQty;
    }

    /**
     * Creates the {@link SubTotal} object for a Table2A object.
     *
     * @return the {@link SubTotal} object for a Table2A.
     */
    static SubTotal createTable2ASubTotal() {
        SubTotal subTotal = new SubTotal();
        subTotal.setAdditions(new Additions());
        subTotal.setSubtractions(new Subtractions());

        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty add = new UnitQty();
            add.setType(unitType);
            add.setValue(NO);
            if (unitType == UnitTypeEnum.AAU || unitType == UnitTypeEnum.CER || unitType == UnitTypeEnum.T_CER || unitType == UnitTypeEnum.L_CER) {
                add.setValue(NA);
            }
            subTotal.getAdditions().getUnitQty().add(add);

            UnitQty sub = new UnitQty();
            sub.setType(unitType);
            sub.setValue(NO);
            subTotal.getSubtractions().getUnitQty().add(sub);
        }
        return subTotal;
    }

    /**
     * Creates the {@link SubTotal} object for a Table2C object.
     *
     * @return the {@link SubTotal} object for a Table2C.
     */
    static SubTotal createTable2CSubTotal() {
        SubTotal subTotal = new SubTotal();
        subTotal.setAdditions(new Additions());
        subTotal.setSubtractions(new Subtractions());

        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty add = new UnitQty();
            add.setType(unitType);
            add.setValue(NA);
            if (unitType == UnitTypeEnum.AAU) {
                add.setValue(NO);
            }
            subTotal.getAdditions().getUnitQty().add(add);

            UnitQty sub = new UnitQty();
            sub.setType(unitType);
            sub.setValue(NA);
            if (unitType == UnitTypeEnum.AAU) {
                sub.setValue(NO);
            }
            subTotal.getSubtractions().getUnitQty().add(sub);
        }
        return subTotal;
    }

    /**
     * Creates an {@link AccountType} object for a Table1 or a Table4. TODO: CONSIDER PULLING UP
     *
     * @param accType the type of account to be created
     * @return the newly created {@link AccountType} object
     */
    private static AccountType createAccountType(AccountTypeEnum accType) {
        AccountType accountType = new AccountType();
        accountType.setName(accType);

        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(unitType);
            unitQty.setValue(NO);

            if (RF2TableFactory.isTypeApplicableForAccountCreation(accType, unitType)) {
                unitQty.setValue(NA);
            }
            accountType.getUnitQty().add(unitQty);
        }
        return accountType;
    }

    /**
     * @param accType
     * @param unitType
     * @return
     */
    private static boolean isTypeApplicableForAccountCreation(AccountTypeEnum accType, UnitTypeEnum unitType) {

        boolean isTypeTCer = (accType == AccountTypeEnum.L_CER_REPLACEMENT_FOR_REVERSAL_OF_STORAGE
                || accType == AccountTypeEnum.L_CER_REPLACEMENT_FOR_NON_SUBMISSION_CERT_REPORT) && unitType == UnitTypeEnum.T_CER;

        boolean isTypeLCer = accType == AccountTypeEnum.T_CER_REPLACEMENT_FOR_EXPIRY && unitType == UnitTypeEnum.L_CER;
        boolean isTypeNotTCer = accType == AccountTypeEnum.T_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY && unitType != UnitTypeEnum.T_CER;

        boolean isTCerLCer = isTypeNotTCer || isTypeLCer || isTypeTCer;

        return RF2TableFactory.isTypeNotAAUForAccountTypeCreation(accType, unitType) ||
                RF2TableFactory.isTypeNotLCerForAccountTypeCreation(accType, unitType) ||
                RF2TableFactory.isTypeTCerOrLCerForAccountTypeCreation(accType, unitType)
                || isTCerLCer ;
    }

    /**
     * @param accType
     * @param unitType
     * @return
     */
    private static boolean isTypeTCerOrLCerForAccountTypeCreation(AccountTypeEnum accType, UnitTypeEnum unitType) {
        boolean isCancellationOrReplacement = accType == AccountTypeEnum.NET_SOURCE_CANCELLATION || accType == AccountTypeEnum.NON_COMPLIANCE_CANCELLATION
                || accType == AccountTypeEnum.L_CER_REPLACEMENT_FOR_EXPIRY;
        return isCancellationOrReplacement && (unitType == UnitTypeEnum.T_CER || unitType == UnitTypeEnum.L_CER);
    }

    /**
     * @param accType
     * @param unitType
     * @return
     */
    private static boolean isTypeNotLCerForAccountTypeCreation(AccountTypeEnum accType, UnitTypeEnum unitType) {
        return (accType == AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY
                || accType == AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_REVERSAL_OF_STORAGE
                || accType == AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_NON_SUBMISSION_CERT_REPORT) && unitType != UnitTypeEnum.L_CER;
    }

    /**
     * @param accType
     * @param unitType
     * @return
     */
    private static boolean isTypeNotAAUForAccountTypeCreation(AccountTypeEnum accType, UnitTypeEnum unitType) {
        return (accType == AccountTypeEnum.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT
                || accType == AccountTypeEnum.ART_31_TER_QUATER_AMBITION_INCREASE_CANCELLATION_ACCOUNT
                || accType == AccountTypeEnum.ART_37_TER_CANCELLATION_ACCOUNT) && unitType != UnitTypeEnum.AAU;
    }

    /**
     * Initializes {@link Table1}
     *
     * @return {@link Table1}
     */
    public static Table1 initializeTable1() {
        Table1 table1 = createTable1();
        table1.setTotalUnitQty(createTotalUnitQty());
        return table1;
    }

    /**
     * Initializes a {@link Table2A} object.
     *
     * @return {@link Table2A} object.
     */
    public static Table2A initializeTable2A() {
        Table2A table2A = createTable2A();
        table2A.setSubTotal(createTable2ASubTotal());

        return table2A;
    }

    /**
     * Initializes a {@link Table2A} object.
     *
     * @return {@link Table2A} object.
     */
    public static Table2ARetirement initializeTable2ARetirement() {
        Table2ARetirement table2ARetirement = createTable2ARetirement();
        table2ARetirement.setTotalUnitQty(createTotalUnitQty());

        return table2ARetirement;
    }

    /**
     * Initializes a {@link Table2B} object.
     *
     * @return {@link Table2B} object.
     */
    public static Table2B initializeTable2B() {
        Table2B table2B = createTable2B();
        table2B.setSubTotal(createSubTotal());

        return table2B;
    }

    /**
     * Initializes a {@link Table2C} object.
     *
     * @return {@link Table2C} object.
     */
    public static Table2C initializeTable2C() {
        Table2C table2C = createTable2C();
        table2C.setSubTotal(createTable2CSubTotal());

        return table2C;
    }

    /**
     * Initializes a {@link Table2D} object.
     *
     * @return {@link Table2D} object.
     */
    public static Table2D initializeTable2D() {
        return createTable2D();
    }

    /**
     * Initializes a {@link Table2E} object.
     *
     * @return {@link Table2E} object.
     */
    public static Table2E initializeTable2E() {
        return createTable2E();
    }

    /**
     * Initializes a {@link Table3} object.
     *
     * @return the {@link Table3} object.
     */
    public static Table3 initializeTable3() {
        Table3 table3 = createTable3();
        table3.setTotalTxOrEvent(createTotalTxOrEvent());
        return table3;
    }

    /**
     * Initializes {@link Table4}
     *
     * @return {@link Table4}
     */
    public static Table4 initializeTable4() {
        Table4 table4 = createTable4();
        table4.setTotalUnitQty(createTotalUnitQty());
        return table4;
    }

    /**
     * Initializes a {@link Table5A} object.
     *
     * @return a {@link Table5A} object.
     */
    public static Table5A initializeTable5A() {
        Table5A table5A = createTable5A();
        table5A.setTotalAdditionSubtraction(createTotalAdditionSubtractionForTable5A());
        return table5A;
    }

    /**
     * Initializes a {@link Table5B} object.
     *
     * @param cp Containment period
     * @return a {@link Table5B} object.
     */
    public static Table5B initializeTable5B(int cp) {
        Table5B table5B = createTable5B(cp);
        table5B.setTotalAdditionSubtraction(createTotalAdditionSubtraction());
        return table5B;
    }

    /**
     * Initializes a {@link Table5C} object.
     *
     * @param cp Containment period
     * @return a {@link Table5C} object.
     */
    public static Table5C initializeTable5C(int cp) {
        Table5C table5C = createTable5C(cp);
        table5C.setTotalAdditionSubtraction(createTotalAdditionSubtractionForTable5C());
        return table5C;
    }

    /**
     * Initializes a {@link Table5D} object.
     *
     * @param cp Containment period
     * @return a {@link Table5D} object.
     */
    public static Table5D initializeTable5D(int cp) {
        Table5D table5D = createTable5D(cp);
        table5D.setTotalTxOrEvent(createTotalTxOrEvent());
        return table5D;
    }

    /**
     * Initializes a {@link Table5E} object.
     *
     * @param cp Containment period
     * @return a {@link Table5E} object.
     */
    public static Table5E initializeTable5E(int cp) {
        Table5E table5E = createTable5E(cp);
        table5E.setTotalUnitQty(createTotalUnitQty());
        return table5E;
    }

    /**
     * Creates a {@link Table6A} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table6A} object
     */
    public static Table6A createTable6A() {
        return new Table6A();
    }

    /**
     * Creates a {@link Table6B} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table6B} object
     */
    public static Table6B createTable6B() {
        return new Table6B();
    }

    /**
     * Creates a {@link Table6C} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table6C} object
     */
    public static Table6C createTable6C() {
        return new Table6C();
    }

    /**
     * @return
     */
    private static TotalAdditionSubtraction createTotalAdditionSubtractionForTable5C() {
        TotalAdditionSubtraction total = new TotalAdditionSubtraction();
        total.setAdditions(new Additions());
        total.setSubtractions(new Subtractions());

        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty add = new UnitQty();
            add.setType(unitType);
            add.setValue(NA);
            if (unitType == UnitTypeEnum.AAU) {
                add.setValue(NO);
            }
            total.getAdditions().getUnitQty().add(add);

            UnitQty sub = new UnitQty();
            sub.setType(unitType);
            sub.setValue(NA);
            if (unitType == UnitTypeEnum.AAU) {
                sub.setValue(NO);
            }
            total.getSubtractions().getUnitQty().add(sub);
        }
        return total;
    }

    /**
     * Creates a {@link TotalAdditionSubtraction} object.
     *
     * @return the newly created {@link TotalAdditionSubtraction} object.
     */
    public static TotalAdditionSubtraction createTotalAdditionSubtractionForTable5A() {
        TotalAdditionSubtraction total = new TotalAdditionSubtraction();
        total.setAdditions(new Additions());
        total.setSubtractions(new Subtractions());

        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty add = new UnitQty();
            add.setType(unitType);
            add.setValue(NO);
            if (unitType == UnitTypeEnum.RMU || unitType == UnitTypeEnum.T_CER || unitType == UnitTypeEnum.L_CER) {
                add.setValue(NA);
            }
            total.getAdditions().getUnitQty().add(add);

            UnitQty sub = new UnitQty();
            sub.setType(unitType);
            sub.setValue(NO);
            total.getSubtractions().getUnitQty().add(sub);
        }
        return total;
    }

    /**
     * Creates a {@link TotalTxOrEvent} object
     *
     * @return the newly created {@link TotalTxOrEvent} object
     */
    static TotalTxOrEvent createTotalTxOrEvent() {
        TotalTxOrEvent total = new TotalTxOrEvent();
        total.setReplacement(new Replacement());
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(unitType);
            unitQty.setValue(NO);

            total.getReplacement().getUnitQty().add(unitQty);
        }

        total.setRequirementForReplacement(new RequirementForReplacement());
        for (CerTypeEnum cerType : RFUtil.getCerTypePerFormat(/* RF2 */2)) {
            CerQty cerQty = new CerQty();
            cerQty.setType(cerType);
            cerQty.setValue(NO);

            total.getRequirementForReplacement().getCerQty().add(cerQty);
        }

        total.setCancellation(new Cancellation());
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(unitType);
            unitQty.setValue(NO);

            total.getCancellation().getUnitQty().add(unitQty);
        }
        return total;
    }

    /**
     * $6.34 Create external transfer object
     *
     * @param registryCode
     * @return
     */
    public static ExternalTransfer createExternalTransfer(String registryCode) {
        ExternalTransfer et = new ExternalTransfer();
        et.setRegistry(registryCode);
        et.setAdditions(new Additions());
        for (UnitTypeEnum utEnum : UnitTypeEnum.values()) {
            UnitQty uq = new UnitQty();
            uq.setType(utEnum);
            uq.setValue(NO);
            et.getAdditions().getUnitQty().add(uq);
        }

        et.setSubtractions(new Subtractions());
        for (UnitTypeEnum utEnum : UnitTypeEnum.values()) {
            UnitQty uq = new UnitQty();
            uq.setType(utEnum);
            uq.setValue(NO);
            et.getSubtractions().getUnitQty().add(uq);
        }

        return et;
    }

    /**
     * Checks if the {@link TransactionTypeEnum} is one of the listed types.
     *
     * @param transactionTypeName The transaction type to check
     * @return true if a match is found
     */
    private static boolean isTransactionTypeART(TransactionTypeEnum transactionTypeName) {
        return transactionTypeName == TransactionTypeEnum.ART_33_AFFORESTATION_REFORESTATION
                || transactionTypeName == TransactionTypeEnum.ART_33_DEFORESTATION
                || transactionTypeName == TransactionTypeEnum.ART_34_CROP_LAND_MANAGEMENT
                || transactionTypeName == TransactionTypeEnum.ART_34_FOREST_MANAGEMENT
                || transactionTypeName == TransactionTypeEnum.ART_34_GRAZING_LAND_MANAGEMENT
                || transactionTypeName == TransactionTypeEnum.ART_34_REVEGETATION
                || transactionTypeName == TransactionTypeEnum.ART_34_WET_LAND_DRAINAGE_REWETTING;
    }
}
