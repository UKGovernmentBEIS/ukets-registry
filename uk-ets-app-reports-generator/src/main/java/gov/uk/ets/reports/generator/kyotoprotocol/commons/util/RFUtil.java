package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import java.security.InvalidParameterException;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.StartingValueEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;

/**
 * Utility class for Report Format
 */
public final class RFUtil {

    private RFUtil() {}

    private static final String INVALID_REPORT_FORMAT_MESSAGE = "Invalid report format";

    private static final AccountTypeEnum[] accountTypeRF1 = {AccountTypeEnum.ENTITY_HOLDING,
            AccountTypeEnum.NET_SOURCE_CANCELLATION,
            AccountTypeEnum.NON_COMPLIANCE_CANCELLATION,
            AccountTypeEnum.OTHER_CANCELLATION_ACCOUNTS,
            AccountTypeEnum.PARTY_HOLDING, AccountTypeEnum.RETIREMENT_ACCOUNT,
            AccountTypeEnum.L_CER_REPLACEMENT_FOR_EXPIRY,
            AccountTypeEnum.L_CER_REPLACEMENT_FOR_NON_SUBMISSION_CERT_REPORT,
            AccountTypeEnum.L_CER_REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
            AccountTypeEnum.T_CER_REPLACEMENT_FOR_EXPIRY};

    private static final AccountTypeEnum[] accountTypeRF2 = {
            AccountTypeEnum.PARTY_HOLDING,
            AccountTypeEnum.ENTITY_HOLDING,
            AccountTypeEnum.RETIREMENT_ACCOUNT,
            AccountTypeEnum.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT,
            AccountTypeEnum.NET_SOURCE_CANCELLATION,
            AccountTypeEnum.NON_COMPLIANCE_CANCELLATION,
            AccountTypeEnum.VOLUNTARY_CANCELLATION_ACCOUNT,
            AccountTypeEnum.CANCELLATION_ACCOUNT_FOR_REMAINING_UNITS_AFTER_CARRYOVER,
            AccountTypeEnum.ART_31_TER_QUATER_AMBITION_INCREASE_CANCELLATION_ACCOUNT,
            AccountTypeEnum.ART_37_TER_CANCELLATION_ACCOUNT,
            AccountTypeEnum.T_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY,
            AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY,
            AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_REVERSAL_OF_STORAGE,
            AccountTypeEnum.L_CER_CANCELLATION_ACCOUNT_FOR_NON_SUBMISSION_CERT_REPORT,
            AccountTypeEnum.T_CER_REPLACEMENT_FOR_EXPIRY,
            AccountTypeEnum.L_CER_REPLACEMENT_FOR_EXPIRY,
            AccountTypeEnum.L_CER_REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
            AccountTypeEnum.L_CER_REPLACEMENT_FOR_NON_SUBMISSION_CERT_REPORT
    };

    private static final TransactionTypeEnum[] transactionTypeRF1 = {
            TransactionTypeEnum.ISSUANCE_CONVERSION_OF_PARTY_VERIFIED_PROJECTS,
            TransactionTypeEnum.INDEPENDENTLY_VERIFIED_PROJECTS,
            TransactionTypeEnum.ART_33_AFFORESTATION_REFORESTATION,
            TransactionTypeEnum.ART_33_DEFORESTATION,
            TransactionTypeEnum.ART_34_CROP_LAND_MANAGEMENT,
            TransactionTypeEnum.ART_34_FOREST_MANAGEMENT,
            TransactionTypeEnum.ART_34_GRAZING_LAND_MANAGEMENT,
            TransactionTypeEnum.ART_34_REVEGETATION,
            TransactionTypeEnum.REPLACEMENT_EXPIREDL_CE_RS,
            TransactionTypeEnum.REPLACEMENT_EXPIREDT_CE_RS,
            TransactionTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT,
            TransactionTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
            TransactionTypeEnum.OTHER_CANCELLATION
    };

    private static final TransactionTypeEnum[] transactionTypeRF2 = {
            TransactionTypeEnum.ISSUANCE_CONVERSION_OF_PARTY_VERIFIED_PROJECTS,
            TransactionTypeEnum.INDEPENDENTLY_VERIFIED_PROJECTS,
            TransactionTypeEnum.ART_33_AFFORESTATION_REFORESTATION,
            TransactionTypeEnum.ART_33_DEFORESTATION,
            TransactionTypeEnum.ART_34_CROP_LAND_MANAGEMENT,
            TransactionTypeEnum.ART_34_FOREST_MANAGEMENT,
            TransactionTypeEnum.ART_34_GRAZING_LAND_MANAGEMENT,
            TransactionTypeEnum.ART_34_REVEGETATION,
            TransactionTypeEnum.ART_34_WET_LAND_DRAINAGE_REWETTING,
            TransactionTypeEnum.REPLACEMENT_EXPIREDL_CE_RS,
            TransactionTypeEnum.REPLACEMENT_EXPIREDT_CE_RS,
            TransactionTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
            TransactionTypeEnum.CANCELLATION_FOR_REVERSAL_OF_STORAGE,
            TransactionTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT,
            TransactionTypeEnum.CANCELLATION_FOR_NON_SUBMISSION_OF_CERT_REPORT,
            TransactionTypeEnum.VOLUNTARY_CANCELLATION,
            TransactionTypeEnum.ART_31_TER_QUATER_AMBITION_INCREASE_CANCELLATION
    };

    private static final TransactionOrEventTypeEnum[] transactionOrEventTypeRF1 = {
            TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS,
            TransactionOrEventTypeEnum.REPLACEMENT_OF_EXPIREDT_CE_RS,
            TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS,
            TransactionOrEventTypeEnum.CANCELLATION_OFT_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS,
            TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS,
            TransactionOrEventTypeEnum.REPLACEMENT_OF_EXPIREDL_CE_RS,
            TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS,
            TransactionOrEventTypeEnum.CANCELLATION_OFL_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS,
            TransactionOrEventTypeEnum.SUBJECT_TO_REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
            TransactionOrEventTypeEnum.REPLACEMENT_FOR_REVERSAL_OF_STORAGE,
            TransactionOrEventTypeEnum.SUBJECT_TO_REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT,
            TransactionOrEventTypeEnum.REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT
    };

    private static final TransactionOrEventTypeEnum[] transactionOrEventTypeRF2 = {
            TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS,
            TransactionOrEventTypeEnum.T_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS,
            TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS,
            TransactionOrEventTypeEnum.L_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS,
            TransactionOrEventTypeEnum.CC_SUBJECT_TO_NET_REVERSAL_OF_STORAGE,
            TransactionOrEventTypeEnum.CC_SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT,
            TransactionOrEventTypeEnum.SUBJECT_TO_REVERSAL_OF_STORAGE,
            TransactionOrEventTypeEnum.SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT
    };

    private static final StartingValueEnum[] startingValueRF1 = {
            StartingValueEnum.ISSUANCE_PURSUANT_TO_ART_37_38,
            StartingValueEnum.NON_COMPLIANCE_CANCELLATION,
            StartingValueEnum.CARRY_OVER
    };

    private static final StartingValueEnum[] startingValueRF2 = {
            StartingValueEnum.ASSIGNED_AMOUNT_UNITS_ISSUED,
            StartingValueEnum.ART_3_PAR_7_TER_CANCELLATIONS,
            StartingValueEnum.CANCELLATION_FOLLOWING_INCREASE_AMBITION,
            StartingValueEnum.CANCELLATION_REMAINING_UNITS_CARRY_OVER,
            StartingValueEnum.NON_COMPLIANCE_CANCELLATION,
            StartingValueEnum.CARRY_OVER,
            StartingValueEnum.CARRY_OVER_PPSR
    };



    /**
     * Returns an array of {@link AccountTypeEnum} values, valid for the specific report format
     *
     * @param reportFormat the report format, 1 or 2
     * @return an array of {@link AccountTypeEnum} values, valid for the specific report format
     */
    public static AccountTypeEnum[] getAccountTypesPerFormat(int reportFormat) {

        if (reportFormat == 1) {
            return accountTypeRF1;
        } else if (reportFormat == 2) {
            return accountTypeRF2;
        } else {
            throw new InvalidParameterException(RFUtil.INVALID_REPORT_FORMAT_MESSAGE + ": " + reportFormat);
        }
    }

    /**
     * Returns an array of {@link AccountTypeEnum} values, valid for the specific report format
     *
     * @param cp the report format, 1 or 2
     * @return an array of {@link AccountTypeEnum} values, valid for the specific report format
     */
    public static AccountTypeEnum[] getAccountTypesPerFormat(ReportFormatEnum cp) {

        if (cp == ReportFormatEnum.CP1) {
            return accountTypeRF1;
        } else if (cp == ReportFormatEnum.CP2) {
            return accountTypeRF2;
        } else {
            throw new InvalidParameterException(RFUtil.INVALID_REPORT_FORMAT_MESSAGE + ": " + cp);
        }
    }

    /**
     * Returns an array of {@link CerTypeEnum} values, valid for the specific report format
     *
     * @param reportFormat the report format, 1 or 2
     * @return an array of {@link CerTypeEnum} values, valid for the specific report format
     */
    public static CerTypeEnum[] getCerTypePerFormat(int reportFormat) {
        if (reportFormat == 1) {
            return new CerTypeEnum[] { CerTypeEnum.L_CER, CerTypeEnum.T_CER };
        } else if (reportFormat == 2) {
            return new CerTypeEnum[] { CerTypeEnum.L_CER, CerTypeEnum.T_CER, CerTypeEnum.CER };
        } else {
            throw new InvalidParameterException(RFUtil.INVALID_REPORT_FORMAT_MESSAGE + ": " + reportFormat);
        }
    }

    /**
     * Returns an array of {@link TransactionTypeEnum} values, valid for the specific report format
     *
     * @param reportFormat the report format, 1 or 2
     * @return an array of {@link TransactionTypeEnum} values, valid for the specific report format
     */
    public static TransactionTypeEnum[] getTransactionTypePerFormat(int reportFormat) {
        if (reportFormat == 1) {
            return transactionTypeRF1;
        } else if (reportFormat == 2) {
            return transactionTypeRF2;
        } else {
            throw new InvalidParameterException(RFUtil.INVALID_REPORT_FORMAT_MESSAGE + ": " + reportFormat);
        }
    }

    /**
     * Returns an array of {@link TransactionOrEventTypeEnum} values, valid for the specific report format
     *
     * @param reportFormat the report format, 1 or 2
     * @return an array of {@link TransactionOrEventTypeEnum} values, valid for the specific report format
     */
    public static TransactionOrEventTypeEnum[] getTransactionOrEventTypeEnumPerFormat(int reportFormat) {
        if (reportFormat == 1) {
            return transactionOrEventTypeRF1;
        } else if (reportFormat == 2) {
            return transactionOrEventTypeRF2;
        } else {
            throw new InvalidParameterException(RFUtil.INVALID_REPORT_FORMAT_MESSAGE + ": " + reportFormat);
        }
    }

    /**
     * Returns an array of {@link StartingValueEnum} values, valid for the specific reportFormat
     *
     * @param reportFormat the report format, 1 or 2
     * @return an array of {@link StartingValueEnum} values, valid for the specific reportFormat
     */
    public static StartingValueEnum[] getStartingValueEnum(int reportFormat) {
        if (reportFormat == 1) {
            return startingValueRF1;
        } else if (reportFormat == 2) {
            return startingValueRF2;
        } else {
            throw new InvalidParameterException(RFUtil.INVALID_REPORT_FORMAT_MESSAGE + ": " + reportFormat);
        }
    }
}
