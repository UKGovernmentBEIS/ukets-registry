package gov.uk.ets.reports.generator.kyotoprotocol.commons;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AdditionalInformation;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Additions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualReplacements;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualRetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualRetirements;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualTransaction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualTransactions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.ExternalTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.PreviousCP;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Replacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.RequirementForReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Retirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValue;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValues;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SubTotal;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Subtractions;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table1;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table2A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table2B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table2C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table3;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table4;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table5A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table5B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table5C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table6A;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table6B;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table6C;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalTxOrEvent;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.util.RFUtil;

/**
 * Provides static factory methods for all tables of sef submissions
 *
 * @author kattoulp
 */
public class RF1TableFactory extends GenericTableFactory {

    /**
     * Creates a {@link Table1} for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table1} object
     */
    public static Table1 createTable1() {
        Table1 table1 = new Table1();
        for (AccountTypeEnum accType : RFUtil.getAccountTypesPerFormat(/* choose the report format */1)) {
            AccountType accountType = createAccountType(accType);
            table1.getAccountType().add(accountType);
        }
        table1.setTotalUnitQty(createTotalUnitQty());
        return table1;
    }

    /**
     * Creates a {@link Table2A} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table2A} object
     */
    public static Table2A createTable2A() {
        Table2A table2a = new Table2A();
        for (TransactionTypeEnum transactionTypeName : RFUtil.getTransactionTypePerFormat(1)) {
            TransactionType tx = createTransactionType(transactionTypeName);
            table2a.getTransactionType().add(tx);
        }
        table2a.setRetirement(createRetirement());
        return table2a;
    }

    /**
     * Creates a {@link Table2B} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table2B} object
     */
    public static Table2B createTable2B() {
        Table2B table2B = new Table2B();
        table2B.setAdditionalInformation(createAdditionalInformation());
        return table2B;
    }

    /**
     * Creates a {@link Table2C} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table2C} object
     */
    public static Table2C createTable2C() {
        Table2C table2C = new Table2C();
        Additions additions = new Additions();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(unitType);
            ub.setValue(NO);
            additions.getUnitQty().add(ub);
        }
        table2C.setAdditions(additions);

        Subtractions subtractions = new Subtractions();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(unitType);
            ub.setValue(NO);
            subtractions.getUnitQty().add(ub);
        }
        table2C.setSubtractions(subtractions);

        return table2C;
    }

    /**
     * Creates a {@link Table3} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table3} object
     */
    public static Table3 createTable3() {
        Table3 table3 = new Table3();
        for (TransactionOrEventTypeEnum name : RFUtil.getTransactionOrEventTypeEnumPerFormat(1)) {
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
        for (AccountTypeEnum accType : RFUtil.getAccountTypesPerFormat(/* choose the report format */1)) {
            AccountType accountType = createAccountType(accType);
            table4.getAccountType().add(accountType);

        }
        table4.setTotalUnitQty(createTotalUnitQty());
        return table4;
    }

    /**
     * Creates a {@link Table5A} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table5A} object
     */
    public static Table5A createTable5A() {
        Table5A table5A = new Table5A();
        table5A.setStartingValues(createStartingValues());
        table5A.setAnnualTransactions(createAnnualTransactions());
        return table5A;
    }

    /**
     * Creates a {@link Table5B} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table5B} object
     */
    public static Table5B createTable5B() {
        Table5B table5B = new Table5B();
        table5B.setPreviousCP(createPreviousCP());
        table5B.setAnnualReplacements(createAnnualReplacements());
        return table5B;
    }

    /**
     * Creates a {@link Table5C} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table5C} object
     */
    public static Table5C createTable5C() {
        Table5C table5C = new Table5C();
        table5C.setAnnualRetirements(createAnnualRetirements());
        return table5C;
    }

    /**
     * Creates a {@link Table6A} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table6A} object
     */
    public static Table6A createTable6A() {
        return new Table6A();
    }

    /**
     * Creates a {@link Table6B} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table6B} object
     */
    public static Table6B createTable6B() {
        return new Table6B();
    }

    /**
     * Creates a {@link Table6C} object for a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.SEFSubmission}
     *
     * @return the newly created {@link Table6C} object
     */
    public static Table6C createTable6C() {
        return new Table6C();
    }

    /**
     * Creates an {@link AnnualRetirements} object to be inserted in a {@link Table5C} object
     *
     * @return the newly created {@link AnnualRetirements} object
     */
    private static AnnualRetirements createAnnualRetirements() {
        AnnualRetirements ret = new AnnualRetirements();
        for (short year = 2008; year <= 2023; year++) {
            ret.getAnnualRetirement().add(createAnnualRetirement(year));
        }
        return ret;
    }

    /**
     * Creates an {@link AnnualRetirement} object to be inserted in an {@link AnnualRetirements} object
     *
     * @param year
     * @return the newly created {@link AnnualRetirement} object
     */
    private static AnnualRetirement createAnnualRetirement(short year) {
        AnnualRetirement ret = new AnnualRetirement();
        ret.setYear(year);
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unit = new UnitQty();
            unit.setType(type);
            unit.setValue(NO);
            ret.getUnitQty().add(unit);
        }
        return ret;
    }

    /**
     * Creates an {@link AnnualReplacements} object to be inserted in a {@link Table5B} object
     *
     * @return the newly created {@link AnnualReplacements} object
     */
    private static AnnualReplacements createAnnualReplacements() {
        AnnualReplacements rep = new AnnualReplacements();
        for (short year = 2008; year <= 2023; year++) {
            rep.getAnnualReplacement().add(createAnnualReplacement(year));
        }
        return rep;
    }

    /**
     * Creates an {@link AnnualReplacement} object to be inserted in a {@link AnnualReplacements} object
     *
     * @param year
     * @return the newly created {@link AnnualReplacement} object
     */
    private static AnnualReplacement createAnnualReplacement(short year) {
        AnnualReplacement ar = new AnnualReplacement();
        ar.setYear(year);
        ar.setRequirementForReplacement(createARRequirement(year));
        Replacement rep = new Replacement();
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unit = new UnitQty();
            unit.setType(type);
            unit.setValue(NO);
            rep.getUnitQty().add(unit);
        }
        ar.setReplacement(rep);
        return ar;
    }

    /**
     * Creates a {@link RequirementForReplacement} object to be inserted in a {@link AnnualReplacement} object
     *
     * @param year
     * @return the newly created {@link RequirementForReplacement} object
     */
    private static RequirementForReplacement createARRequirement(short year) {
        RequirementForReplacement req = new RequirementForReplacement();
        for (CerTypeEnum type : RFUtil.getCerTypePerFormat(/*RF1*/1)) {
            CerQty cer = new CerQty();
            cer.setType(type);
            cer.setValue(NO);

            if (type == CerTypeEnum.T_CER && (year >= 2008 && year <= 2011)) {
                cer.setValue(NA);
            }
            req.getCerQty().add(cer);
        }
        return req;
    }

    /**
     * Creates a {@link PreviousCP} object to be inserted in a {@link Table5A} object
     *
     * @return the newly created {@link PreviousCP} object
     */
    private static PreviousCP createPreviousCP() {
        PreviousCP cp = new PreviousCP();
        RequirementForReplacement req = new RequirementForReplacement();
        cp.setRequirementForReplacement(req);
        for (CerTypeEnum type : RFUtil.getCerTypePerFormat(/*RF1*/1)) {
            CerQty cer = new CerQty();
            cer.setType(type);
            cer.setValue(NA);
            req.getCerQty().add(cer);
        }

        Replacement rep = new Replacement();
        cp.setReplacement(rep);
        for (UnitTypeEnum type : UnitTypeEnum.values()) {
            UnitQty unit = new UnitQty();
            unit.setType(type);
            unit.setValue(NO);
            rep.getUnitQty().add(unit);
        }

        return cp;
    }

    /**
     * Creates an {@link AnnualTransactions} object to be inserted in a {@link Table5A} object
     *
     * @return the newly created {@link AnnualTransactions} object.
     */
    private static AnnualTransactions createAnnualTransactions() {
        AnnualTransactions txs = new AnnualTransactions();
        for (short year = 2007; year <= 2023; year++) {
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
        for (StartingValueEnum name : RFUtil.getStartingValueEnum(1)) {
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

            if (name == StartingValueEnum.NON_COMPLIANCE_CANCELLATION && (type != UnitTypeEnum.T_CER && type != UnitTypeEnum.L_CER)) {
                unitQty.setValue(NO);
            }

            subtractions.getUnitQty().add(unitQty);
        }
        return subtractions;
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

            if (name == StartingValueEnum.ISSUANCE_PURSUANT_TO_ART_37_38 && type == UnitTypeEnum.AAU) {
                unitQty.setValue(NO);
            }

            if (name == StartingValueEnum.CARRY_OVER && (type == UnitTypeEnum.AAU || type == UnitTypeEnum.ERU || type == UnitTypeEnum.CER)) {
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
        return tx;
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

            if (name == TransactionOrEventTypeEnum.REPLACEMENT_OF_EXPIREDT_CE_RS && type != UnitTypeEnum.L_CER) {
                ub.setValue(NO);
            }
            if (name == TransactionOrEventTypeEnum.REPLACEMENT_OF_EXPIREDL_CE_RS && (type != UnitTypeEnum.L_CER && type != UnitTypeEnum.T_CER)) {
                ub.setValue(NO);
            }

            if (type != UnitTypeEnum.T_CER && (name == TransactionOrEventTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE
                    || name == TransactionOrEventTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT)) {
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
        for (CerTypeEnum type : RFUtil.getCerTypePerFormat(/*RF1*/1)) {
            CerQty cer = new CerQty();
            cer.setType(type);
            cer.setValue(NA);

            if (isExpiredTransaction(name, type)) {
                cer.setValue(NO);
            }
            req.getCerQty().add(cer);
        }
        return req;
    }

    /**
     * Helper method that resolves whether a transaction is expired or not
     *
     * @param name the transaction name
     * @param type the transaction type
     * @return true if expired false otherwise
     */
    private static boolean isExpiredTransaction(TransactionOrEventTypeEnum name, CerTypeEnum type) {
        boolean isExpiredTCerTransaction = name == TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS
                || name == TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS
                || name == TransactionOrEventTypeEnum.CANCELLATION_OFT_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS;

        boolean isExpiredLCerTransaction = name == TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS
                || name == TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS
                || name == TransactionOrEventTypeEnum.CANCELLATION_OFL_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS;

        boolean isSubjectToReplacement = name == TransactionOrEventTypeEnum.SUBJECT_TO_REPLACEMENT_FOR_REVERSAL_OF_STORAGE
                || name == TransactionOrEventTypeEnum.SUBJECT_TO_REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT;

        return (type == CerTypeEnum.T_CER && (isExpiredTCerTransaction)) || (type == CerTypeEnum.L_CER && (isExpiredLCerTransaction
                || isSubjectToReplacement));
    }

    /**
     * Creates a {@link TransactionType} object
     *
     * @param transactionTypeName
     * @return the newly created {@link TransactionType} object
     */
    static TransactionType createTransactionType(TransactionTypeEnum transactionTypeName) {
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

        if (RF1TableFactory.isTransactionTypeApplicableForSubtractions(transactionTypeName, unitType)) {
            unitQty.setValue(NA);
        }

        return unitQty;
    }

    /**
     *
     * @param transactionTypeName
     * @param unitType
     * @return
     */
    private static boolean isTransactionTypeApplicableForSubtractions(TransactionTypeEnum transactionTypeName, UnitTypeEnum unitType) {

        boolean isTypeTCerOrLCer = (unitType == UnitTypeEnum.T_CER || unitType == UnitTypeEnum.L_CER) && (RF1TableFactory.isTransactionTypeART(transactionTypeName)
                || transactionTypeName == TransactionTypeEnum.REPLACEMENT_EXPIREDL_CE_RS);

        boolean isTypeLCer = transactionTypeName == TransactionTypeEnum.REPLACEMENT_EXPIREDT_CE_RS && unitType == UnitTypeEnum.L_CER;
        boolean isTypeTCer = (transactionTypeName == TransactionTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT
                || transactionTypeName == TransactionTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE) && unitType == UnitTypeEnum.T_CER;

        return RF1TableFactory.isTypeNotAAUOrRMUForSubtractions(transactionTypeName, unitType) || isTypeTCerOrLCer || isTypeLCer || isTypeTCer;
    }

    /**
     *
     * @param transactionTypeName
     * @param unitType
     * @return
     */
    private static boolean isTypeNotAAUOrRMUForSubtractions(TransactionTypeEnum transactionTypeName, UnitTypeEnum unitType) {
        return (transactionTypeName == TransactionTypeEnum.ISSUANCE_CONVERSION_OF_PARTY_VERIFIED_PROJECTS ||
                transactionTypeName == TransactionTypeEnum.INDEPENDENTLY_VERIFIED_PROJECTS) &&
                (unitType != UnitTypeEnum.AAU && unitType != UnitTypeEnum.RMU);
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
                || transactionTypeName == TransactionTypeEnum.ART_34_REVEGETATION;
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
     * Creates a {@link Retirement} object
     *
     * @return the newly created {@link Retirement} object
     */
    static Retirement createRetirement() {
        Retirement retirement = new Retirement();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(unitType);
            ub.setValue(NO);
            retirement.getUnitQty().add(ub);
        }

        return retirement;
    }

    /**
     * Creates an {@link AdditionalInformation} object to be inserted in a {@link Table2B} object
     *
     * @return the newly created {@link AdditionalInformation} object
     */
    private static AdditionalInformation createAdditionalInformation() {
        AdditionalInformation additionalInformation = new AdditionalInformation();
        Additions additions = new Additions();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(unitType);
            ub.setValue(NA);
            additions.getUnitQty().add(ub);
        }
        additionalInformation.setAdditions(additions);

        Subtractions subtractions = new Subtractions();
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty ub = new UnitQty();
            ub.setType(unitType);
            ub.setValue(NA);
            if (unitType == UnitTypeEnum.ERU) {
                ub.setValue(NO);
            }
            subtractions.getUnitQty().add(ub);
        }
        additionalInformation.setSubtractions(subtractions);
        return additionalInformation;
    }

    /**
     * Creates an {@link AccountType} object for a Table1 or a Table4.
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

            if (isUnitTypeApplicableForAccountCreation(accType, unitType)) {
                unitQty.setValue(NA);
            }

            accountType.getUnitQty().add(unitQty);
        }
        return accountType;
    }

    /**
     *
     * @param accType
     * @param unitType
     * @return
     */
    private static boolean isUnitTypeApplicableForAccountCreation(AccountTypeEnum accType, UnitTypeEnum unitType) {
        boolean isLCerType = accType == AccountTypeEnum.T_CER_REPLACEMENT_FOR_EXPIRY && (unitType == UnitTypeEnum.L_CER);

        boolean isTCerType = (accType == AccountTypeEnum.L_CER_REPLACEMENT_FOR_REVERSAL_OF_STORAGE
                || accType == AccountTypeEnum.L_CER_REPLACEMENT_FOR_NON_SUBMISSION_CERT_REPORT) &&
                (unitType == UnitTypeEnum.T_CER);

        boolean isAccountTypeValid = accType == AccountTypeEnum.NET_SOURCE_CANCELLATION
                || accType == AccountTypeEnum.NON_COMPLIANCE_CANCELLATION
                || accType == AccountTypeEnum.L_CER_REPLACEMENT_FOR_EXPIRY;

        boolean isTCerAndLCerType = isAccountTypeValid &&
                (unitType == UnitTypeEnum.T_CER	|| unitType == UnitTypeEnum.L_CER);

        return isTCerAndLCerType || isTCerType || isLCerType;
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
     * Creates the {@link SubTotal} object for {@link StartingValues} of {@link Table5A}. [SEFCOLLAB-56] specifies that subtraction-tcer and
     * subtraction-lcer must have the value 'NA'. Also the cells addition-RMU, addition-tCER, and addition-lCER must have the value NA.
     *
     * @return the {@link SubTotal} object for {@link StartingValues} of {@link Table5A}
     */
    private static SubTotal createTable5AStartingValueSubTotal() {
        SubTotal subTotal = new SubTotal();
        subTotal.setAdditions(new Additions());
        subTotal.setSubtractions(new Subtractions());

        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty add = new UnitQty();
            add.setType(unitType);
            add.setValue(NO);
            if (unitType == UnitTypeEnum.RMU || unitType == UnitTypeEnum.T_CER || unitType == UnitTypeEnum.L_CER) {
                add.setValue(NA);
            }
            subTotal.getAdditions().getUnitQty().add(add);

            UnitQty sub = new UnitQty();
            sub.setType(unitType);
            sub.setValue(NO);
            if (unitType == UnitTypeEnum.T_CER || unitType == UnitTypeEnum.L_CER) {
                sub.setValue(NA);
            }
            subTotal.getSubtractions().getUnitQty().add(sub);
        }
        return subTotal;
    }

    /**
     * Creates the {@link SubTotal} object for {@link Table2A}. [SEFCOLLAB-56] specifies that in the section "additions", the cells AAU, CER, tCER,
     * and lCER must have the value NA.
     *
     * @return the {@link SubTotal} object for {@link Table2A}.
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
     * Creates a {@link TotalTxOrEvent} object
     *
     * @return the newly created {@link TotalTxOrEvent} object
     */
    public static TotalTxOrEvent createTotalTxOrEvent() {
        TotalTxOrEvent total = new TotalTxOrEvent();
        total.setReplacement(new Replacement());
        for (UnitTypeEnum unitType : UnitTypeEnum.values()) {
            UnitQty unitQty = new UnitQty();
            unitQty.setType(unitType);
            unitQty.setValue(NO);

            total.getReplacement().getUnitQty().add(unitQty);
        }

        total.setRequirementForReplacement(new RequirementForReplacement());
        for (CerTypeEnum cerType : RFUtil.getCerTypePerFormat(/*RF1*/1)) {
            CerQty cerQty = new CerQty();
            cerQty.setType(cerType);
            cerQty.setValue(NO);

            total.getRequirementForReplacement().getCerQty().add(cerQty);
        }
        return total;
    }

    /**
     * Initializes {@link Table1}
     *
     * @return
     */
    public static Table1 initializeTable1() {
        Table1 table1 = RF1TableFactory.createTable1();
        table1.setTotalUnitQty(createTotalUnitQty());
        return table1;
    }

    /**
     * Initializes a {@link Table2A} object.
     *
     * @return
     */
    public static Table2A initializeTable2A() {
        Table2A table2A = RF1TableFactory.createTable2A();
        table2A.setSubTotal(createTable2ASubTotal());

        return table2A;
    }

    /**
     * Initializes a {@link Table2B} object.
     *
     * @return
     */
    public static Table2B initializeTable2B() {
        Table2B table2B = RF1TableFactory.createTable2B();
        table2B.setSubTotal(createSubTotal());

        return table2B;
    }

    /**
     * Initializes a {@link Table2C} object.
     *
     * @return
     */
    public static Table2C initializeTable2C() {
        return RF1TableFactory.createTable2C();
    }

    /**
     * Initializes a {@link Table3} object
     *
     * @return {@code Table3}
     */
    public static Table3 initializeTable3() {
        Table3 table3 = RF1TableFactory.createTable3();
        table3.setTotalTxOrEvent(createTotalTxOrEvent());
        // fix for SEFCOLLAB-347
        for (CerQty cerQ : table3.getTotalTxOrEvent().getRequirementForReplacement().getCerQty()) {
            cerQ.setValue(NA);
        }

        return table3;
    }

    /**
     * Initializes {@link Table4}
     *
     * @return
     */
    public static Table4 initializeTable4() {
        Table4 table4 = RF1TableFactory.createTable4();
        table4.setTotalUnitQty(createTotalUnitQty());
        return table4;
    }

    /**
     * Initializes a {@link Table5A} object.
     *
     * @return
     */
    public static Table5A initializeTable5A() {
        Table5A table5A = RF1TableFactory.createTable5A();
        table5A.getAnnualTransactions().setSubTotal(createSubTotal());
        table5A.getStartingValues().setSubTotal(createTable5AStartingValueSubTotal());
        table5A.setTotalAdditionSubtraction(createTotalAdditionSubtraction());
        return table5A;
    }

    /**
     * Initializes a {@link Table5B} object.
     *
     * @return
     */
    public static Table5B initializeTable5B() {
        Table5B table5B = RF1TableFactory.createTable5B();
        table5B.setTotalTxOrEvent(createTotalTxOrEvent());

        return table5B;
    }

    /**
     * Initializes a {@link Table5C} object.
     *
     * @return
     */
    public static Table5C initializeTable5C() {
        Table5C table5C = RF1TableFactory.createTable5C();
        table5C.setTotalUnitQty(createTotalUnitQty());

        return table5C;
    }

}
