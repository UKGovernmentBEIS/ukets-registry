package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualRetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AnnualTransaction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValues;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table3;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalTxOrEvent;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Retirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table1;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table4;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CorrectiveReplacement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CorrectiveRetirement;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CorrectiveTransaction;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.ExternalTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValue;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AdaptationFundType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AdaptationFundTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransactionPPSR;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.PPSRTransfer;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.RetirementTypeEnum;

import java.util.List;

/**
 * Provides static utility methods for SEF submission tables.
 *
 * @author kattoulp
 *
 */
public class TableUtil {

    private static final String NON_NULLABLE_TRANSACTION_TYPE_MESSAGE = "The transaction type cannot be null";

    /**
     * Will search the specified table and return the account type that corresponds to the account type enum.
     *
     * @param accType the account type found of the specified table.
     * @return
     */
    public static AccountType getAccountTypeByAccountType(gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table4 table4, AccountTypeEnum accType) {
        for (AccountType type : table4.getAccountType()) {
            if (accType == type.getName()) {
                return type;
            }
        }
        return null;
    }

    /**
     * Will search the specified table and return the account type that corresponds to the account type enum.
     *
     * @param accType the account type found of the specified table.
     * @return
     */
    public static AccountType getAccountTypeByAccountType(Table4 table4,
                                                          AccountTypeEnum accType) {
        for (AccountType type : table4.getAccountType()) {
            if (accType == type.getName()) {
                return type;
            }
        }
        return null;
    }

    /**
     * Helper for getting the unit quantity
     *
     * @param accTypeEnum
     * @param unitTypeEnum
     * @return
     */
    public static UnitQty getUnitQty(gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table4 table4, AccountTypeEnum accTypeEnum, UnitTypeEnum unitTypeEnum) {
        AccountType accType = getAccountTypeByAccountType(table4, accTypeEnum);
        return AccountTypeUtil.getUnitQtyByUnitType(accType, unitTypeEnum);
    }

    /**
     * Helper for getting the unit quantity
     *
     * @param accTypeEnum
     * @param unitTypeEnum
     * @return
     */
    public static UnitQty getUnitQty(Table4 table4,
                                     AccountTypeEnum accTypeEnum, UnitTypeEnum unitTypeEnum) {
        AccountType accType = getAccountTypeByAccountType(table4, accTypeEnum);
        return AccountTypeUtil.getUnitQtyByUnitType(accType, unitTypeEnum);
    }

    /**
     * Will search the specified table and return the account type that corresponds to the account type enum.
     *
     * @param accType the account type found of the specified table.
     * @return
     */
    public static AccountType getAccountTypeByAccountType(gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table1 table1, AccountTypeEnum accType) {
        for (AccountType type : table1.getAccountType()) {
            if (accType == type.getName()) {
                return type;
            }
        }
        return null;
    }

    /**
     * Will search the specified table and return the account type that corresponds to the account type enum.
     *
     * @param accType the account type found of the specified table.
     * @return
     */
    public static AccountType getAccountTypeByAccountType(Table1 table1,
                                                          AccountTypeEnum accType) {
        for (AccountType type : table1.getAccountType()) {
            if (accType == type.getName()) {
                return type;
            }
        }
        return null;
    }

    /**
     * Helper for getting the unit quantity
     *
     * @param accTypeEnum
     * @param unitTypeEnum
     * @return
     */
    public static UnitQty getUnitQty(gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.Table1 table1, AccountTypeEnum accTypeEnum, UnitTypeEnum unitTypeEnum) {
        AccountType accType = getAccountTypeByAccountType(table1, accTypeEnum);
        return AccountTypeUtil.getUnitQtyByUnitType(accType, unitTypeEnum);
    }

    /**
     * Helper for getting the unit quantity
     *
     * @param accTypeEnum
     * @param unitTypeEnum
     * @return
     */
    public static UnitQty getUnitQty(Table1 table1,
                                     AccountTypeEnum accTypeEnum, UnitTypeEnum unitTypeEnum) {
        AccountType accType = getAccountTypeByAccountType(table1, accTypeEnum);
        return AccountTypeUtil.getUnitQtyByUnitType(accType, unitTypeEnum);
    }

    /**
     * Will get the proper StartingValue taking into account the type parameter.
     *
     * @param startingValues
     * @param type
     * @return
     */
    public static StartingValue getStartingValueByType(StartingValues startingValues, StartingValueEnum type) {
        for (StartingValue sv : startingValues.getStartingValue()) {
            if (sv.getName() == type) {
                return sv;
            }
        }
        return null;
    }

    /**
     * Will get the proper StartingValue taking into account the type parameter.
     *
     * @param startingValues
     * @param type
     * @return
     */
    public static StartingValue getStartingValueByType(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.StartingValues startingValues, StartingValueEnum type) {
        for (StartingValue sv : startingValues.getStartingValue()) {
            if (sv.getName() == type) {
                return sv;
            }
        }
        return null;
    }

    /**
     * Will get the proper TransactionType from a list of types.
     *
     * @param txTypes
     * @param type
     * @return
     */
    public static TransactionType getTransactionTypeByType(List<TransactionType> txTypes, TransactionTypeEnum type) {
        for (TransactionType txType : txTypes) {
            if (txType.getName() == type) {
                return txType;
            }
        }
        return null;
    }

    /**
     * Wiil get the Transaction or Event type for a specific type.
     * @param txEvTypes
     * @param type
     * @return
     */
    public static TransactionOrEventType getTransactionOrEventTypeByType(List<TransactionOrEventType> txEvTypes, TransactionOrEventTypeEnum type) {
        for (TransactionOrEventType txType : txEvTypes) {
            if (txType.getName() == type) {
                return txType;
            }
        }
        return null;
    }

    /**
     * Wiil get the Transaction or Event type for a specific type.
     * @param txEvTypes
     * @param type
     * @return
     */
    public static gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType getSef2TransactionOrEventTypeByType
    (List<gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType> txEvTypes,
     TransactionOrEventTypeEnum type) {
        for (gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType txType : txEvTypes) {
            if (txType.getName() == type) {
                return txType;
            }
        }
        return null;
    }

    /**
     * Will get the annual replacement for a year
     * @param anReps
     * @param year
     * @return
     */
    public static AnnualReplacement getAnnualReplacementByType(List<AnnualReplacement> anReps, short year) {
        for (AnnualReplacement ar : anReps) {
            if (ar.getYear() == year) {
                return ar;
            }
        }
        return null;
    }

    /**
     * Will get the annual replacement for a year
     * @param anReps
     * @param year
     * @return
     */
    public static gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualReplacement getSef2AnnualReplacementByYear
    (List<gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualReplacement> anReps, short year) {
        for (gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualReplacement ar : anReps) {
            if (ar.getYear() == year) {
                return ar;
            }
        }
        return null;
    }

    /**
     * Will get the external transfer object for the specified registry.
     * @param externalTransfers
     * @param registryCode
     * @return
     */
    public static ExternalTransfer getExternalTransferByRegistry(List<ExternalTransfer> externalTransfers, String registryCode) {
        for (ExternalTransfer et : externalTransfers) {
            if (et.getRegistry().equals(registryCode)) {
                return et;
            }
        }
        return null;
    }

    /**
     * Will get the {@link AnnualTransaction} by year
     * @param annualTransactions
     * @param year
     * @return
     */
    public static AnnualTransaction getAnnualTransactionByYear(List<AnnualTransaction> annualTransactions, short year) {
        for (AnnualTransaction annualTransaction : annualTransactions) {
            if (annualTransaction.getYear() == year) {
                return annualTransaction;
            }
        }
        return null;
    }

    /**
     * Will get the {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransaction} by year
     * @param annualTransactions
     * @param year
     * @return
     */
    public static gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransaction getSef2AnnualTransactionByYear
    (List<gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransaction> annualTransactions, short year) {
        for (gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualTransaction annualTransaction : annualTransactions) {
            if (annualTransaction.getYear() == year) {
                return annualTransaction;
            }
        }
        return null;
    }

    /**
     * Will get the {@link AnnualRetirement} by year
     * @param annualRetirements
     * @param year
     * @return the {@link AnnualRetirement} for the specified year
     */
    public static AnnualRetirement getAnnualRetirement(List<AnnualRetirement> annualRetirements, short year) {
        for(AnnualRetirement annualRetirement : annualRetirements) {
            if(annualRetirement.getYear() == year) {
                return annualRetirement;
            }
        }

        return null;
    }

    /**
     * Will get the {@link AnnualRetirement} by year
     * @param annualRetirements
     * @param year
     * @return the {@link AnnualRetirement} for the specified year
     */
    public static gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualRetirement getSef2AnnualRetirement
    (List<gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualRetirement> annualRetirements, short year) {
        for(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.AnnualRetirement annualRetirement : annualRetirements) {
            if(annualRetirement.getYear() == year) {
                return annualRetirement;
            }
        }

        return null;
    }

    /**
     * Will get the {@link CorrectiveTransaction} by transaction number
     * @param correctiveTransactions
     * @param transactionNumber
     * @return the {@link CorrectiveTransaction} by transaction number
     */
    public static CorrectiveTransaction getCorrectiveTransaction(List<CorrectiveTransaction> correctiveTransactions, String transactionNumber) {
        for (CorrectiveTransaction correctiveTransaction : correctiveTransactions) {
            if (correctiveTransaction.getTransactionNumber().equals(transactionNumber)) {
                return correctiveTransaction;
            }
        }

        return null;
    }

    /**
     * Will get the {@link CorrectiveReplacement} by transaction number
     * @param correctiveReplacements
     * @param transactionNumber
     * @return the {@link CorrectiveReplacement} by transaction number
     */
    public static CorrectiveReplacement getCorrectiveReplacement(List<CorrectiveReplacement> correctiveReplacements, String transactionNumber) {
        for (CorrectiveReplacement correctiveReplacement : correctiveReplacements) {
            if (correctiveReplacement.getTransactionNumber().equals(transactionNumber)) {
                return correctiveReplacement;
            }
        }

        return null;
    }

    /**
     * Will get the {@link CorrectiveRetirement} by transaction number
     * @param correctiveRetirements
     * @param transactionNumber
     * @return the {@link CorrectiveRetirement} by transaction number
     */
    public static CorrectiveRetirement getCorrectiveRetirement(List<CorrectiveRetirement> correctiveRetirements, String transactionNumber) {
        for (CorrectiveRetirement correctiveRetirement : correctiveRetirements) {
            if (correctiveRetirement.getTransactionNumber().equals(transactionNumber)) {
                return correctiveRetirement;
            }
        }

        return null;
    }

    /**
     * Helper for getting the cer quantity
     * @param table3
     * @param txTypeEnum
     * @param cerType
     * @return the cer quantity
     */
    public static CerQty getCerQty(Table3 table3, TransactionOrEventTypeEnum txTypeEnum, CerTypeEnum cerType) {
        TransactionOrEventType txType = getTransactionOrEventTypeByType(table3.getTransactionOrEventType(), txTypeEnum);
        if (txType == null){
            throw new IllegalStateException(TableUtil.NON_NULLABLE_TRANSACTION_TYPE_MESSAGE);
        }
        return CerQtyUtil.getCerQtyByCerType(txType.getRequirementForReplacement().getCerQty(), cerType);
    }

    /**
     * Helper for getting the cer quantity
     * @param table3
     * @param txTypeEnum
     * @param cerType
     * @return the cer quantity
     */
    public static CerQty getCerQty(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table3 table3,
                                   TransactionOrEventTypeEnum txTypeEnum, CerTypeEnum cerType) {
        gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType txType =
                getSef2TransactionOrEventTypeByType(table3.getTransactionOrEventType(), txTypeEnum);
        if(txType == null){
            throw new IllegalStateException(TableUtil.NON_NULLABLE_TRANSACTION_TYPE_MESSAGE);
        }
        return CerQtyUtil.getCerQtyByCerType(txType.getRequirementForReplacement().getCerQty(), cerType);
    }

    /**
     * Helper for getting the unit quantity
     * @param table3
     * @param txTypeEnum
     * @param unitType
     * @return the unit quantity
     */
    public static UnitQty getUnitQty(Table3 table3, TransactionOrEventTypeEnum txTypeEnum, UnitTypeEnum unitType) {
        TransactionOrEventType txType = getTransactionOrEventTypeByType(table3.getTransactionOrEventType(), txTypeEnum);
        if(txType == null){
            throw new IllegalStateException(TableUtil.NON_NULLABLE_TRANSACTION_TYPE_MESSAGE);
        }
        return UnitQtyUtil.getUnitQtyByUnitType(txType.getReplacement().getUnitQty(), unitType);
    }

    /**
     * Helper for getting the replacement unit quantity
     * @param table3
     * @param txTypeEnum
     * @param unitType
     * @return the unit quantity
     */
    public static UnitQty getReplacementUnitQty(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table3 table3,
                                                TransactionOrEventTypeEnum txTypeEnum, UnitTypeEnum unitType) {
        gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType txType =
                getSef2TransactionOrEventTypeByType(table3.getTransactionOrEventType(), txTypeEnum);
        if(txType == null){
            throw new IllegalStateException(TableUtil.NON_NULLABLE_TRANSACTION_TYPE_MESSAGE);
        }
        return UnitQtyUtil.getUnitQtyByUnitType(txType.getReplacement().getUnitQty(), unitType);
    }

    /**
     * Helper for getting the cancellation unit quantity
     * @param table3
     * @param txTypeEnum
     * @param unitType
     * @return the unit quantity
     */
    public static UnitQty getCancellationUnitQty(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Table3 table3,
                                                 TransactionOrEventTypeEnum txTypeEnum, UnitTypeEnum unitType) {
        gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TransactionOrEventType txType =
                getSef2TransactionOrEventTypeByType(table3.getTransactionOrEventType(), txTypeEnum);
        if(txType == null){
            throw new IllegalStateException(TableUtil.NON_NULLABLE_TRANSACTION_TYPE_MESSAGE);
        }
        return UnitQtyUtil.getUnitQtyByUnitType(txType.getCancellation().getUnitQty(), unitType);
    }

    /**
     * Helper for getting a specific cer qty from a {@link TotalTxOrEvent} object
     * @param total
     * @param cerType
     * @return a specific cer qty
     */
    public static CerQty getCerQty(TotalTxOrEvent total, CerTypeEnum cerType) {
        return CerQtyUtil.getCerQtyByCerType(total.getRequirementForReplacement().getCerQty(), cerType);
    }

    /**
     * Helper for getting a specific cer qty from a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TotalTxOrEvent} object
     * @param total
     * @param cerType
     * @return a specific cer qty
     */
    public static CerQty getCerQty(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TotalTxOrEvent total, CerTypeEnum cerType) {
        return CerQtyUtil.getCerQtyByCerType(total.getRequirementForReplacement().getCerQty(), cerType);
    }

    /**
     * Helper for getting a specific unit qty from a {@link TotalTxOrEvent} object
     * @param total
     * @param unitType
     * @return a unit qty
     */
    public static UnitQty getUnitQty(TotalTxOrEvent total, UnitTypeEnum unitType) {
        return UnitQtyUtil.getUnitQtyByUnitType(total.getReplacement().getUnitQty(), unitType);
    }

    /**
     * Helper for getting a specific replacement unit qty from a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TotalTxOrEvent} object
     * @param total
     * @param unitType
     * @return a unit qty
     */
    public static UnitQty getReplacementUnitQty(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TotalTxOrEvent total,
                                                UnitTypeEnum unitType) {
        return UnitQtyUtil.getUnitQtyByUnitType(total.getReplacement().getUnitQty(), unitType);
    }

    /**
     * Helper for getting a specific cancellation unit qty from a {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TotalTxOrEvent} object
     * @param total
     * @param unitType
     * @return a unit qty
     */
    public static UnitQty getCancellationUnitQty(gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.TotalTxOrEvent total,
                                                 UnitTypeEnum unitType) {
        return UnitQtyUtil.getUnitQtyByUnitType(total.getCancellation().getUnitQty(), unitType);
    }

    /**
     * Helper method for getting the {@link PPSRTransfer} object for the specific registry
     *
     * @param ppsrTransfers
     * @param registryCode
     * @return the {@link PPSRTransfer} object for the specific registry
     */
    public static PPSRTransfer getPpsrTransfer(List<PPSRTransfer> ppsrTransfers, String registryCode) {
        for (PPSRTransfer ppsrTransfer : ppsrTransfers) {
            if (ppsrTransfer.getRegistry().equals(registryCode)) {
                return ppsrTransfer;
            }
        }
        return null;
    }

    /**
     * Helper method for getting the {@link AdaptationFundType} object with the specific name
     *
     * @param adaptationFunds
     * @param name
     * @return the {@link AdaptationFundType} object with the specific name
     */
    public static AdaptationFundType getAdaptationFund(List<AdaptationFundType> adaptationFunds, AdaptationFundTypeEnum name) {
        for (AdaptationFundType adaptationFundType : adaptationFunds) {
            if (adaptationFundType.getName() == name) {
                return adaptationFundType;
            }
        }
        return null;
    }


    /**
     * Helper method for getting the {@link AnnualTransactionPPSR} for the specific year
     *
     * @param txs
     * @param year
     * @return the {@link AnnualTransactionPPSR} for the specific year
     */
    public static AnnualTransactionPPSR getAnnualTransactionPpsr(List<AnnualTransactionPPSR> txs, short year) {
        for (AnnualTransactionPPSR annualTransactionsPPSR : txs) {
            if (annualTransactionsPPSR.getYear() == year) {
                return annualTransactionsPPSR;
            }
        }
        return null;
    }

    /**
     * Helper method for getting the {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Retirement} object by name
     *
     * @param retirements
     * @param name
     * @return the {@link gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Retirement} object with the specified name
     */
    public static gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Retirement getRetirementByName(List<gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0.Retirement> retirements, RetirementTypeEnum name) {
        for (Retirement retirement : retirements) {
            if (retirement.getName() == name) {
                return retirement;
            }
        }
        return null;
    }



}
