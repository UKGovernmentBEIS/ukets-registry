package gov.uk.ets.reports.generator.kyotoprotocol.sef.enums;

import gov.uk.ets.reports.generator.kyotoprotocol.commons.enums.ReportFormatEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionOrEventTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TransactionTypeEnum;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;

/**
 * This will handle the conversion between enum types
 *
 * @author gkountak
 *
 */
public class EnumConverter {

    /**
     * Converts the ITL Account type enumeration to the XSD one for the specific report format
     *
     * @param dbAccountType
     * @return
     */
    public static AccountTypeEnum getXSDAccountTypeEnum(ITLAccountTypeEnum dbAccountType, ReportFormatEnum cp) {
        if(cp == ReportFormatEnum.CP1) {
            return AccountTypeEnum.fromValue(dbAccountType.getDescription());
        }
        if(cp == ReportFormatEnum.CP2) {
            return AccountTypeEnum.fromValue(dbAccountType.getCp2Description());
        }

        throw new IllegalArgumentException("Report Format not supported: " + cp);
    }

    /**
     * Converts the ITL Unit type enumeration to the XSD one
     *
     * @param dbEnum
     * @return
     */
    public static UnitTypeEnum getXSDUnitTypeEnum(ITLUnitTypeEnum dbEnum) {
        return UnitTypeEnum.fromValue(dbEnum.getDescription());
    }

    /**
     * Selects the transaction type per LULUCF code.
     *
     * @param luluCFEnum
     * @return
     */
    public static TransactionTypeEnum getTransactionTypeFromLuluCFCode(ITLLulucfTypeEnum luluCFEnum) {
        switch (luluCFEnum) {
            case AFFO_REFO:
                return TransactionTypeEnum.ART_33_AFFORESTATION_REFORESTATION;
            case DEFORESTATION:
                return TransactionTypeEnum.ART_33_DEFORESTATION;
            case FOREST_MANAGEMENT:
                return TransactionTypeEnum.ART_34_FOREST_MANAGEMENT;
            case CROPLAND_MANAGEMENT:
                return TransactionTypeEnum.ART_34_CROP_LAND_MANAGEMENT;
            case GRAZING_LAND_MANAGEMENT:
                return TransactionTypeEnum.ART_34_GRAZING_LAND_MANAGEMENT;
            case REVEGETATION:
                return TransactionTypeEnum.ART_34_REVEGETATION;
            case WETLAND_DRAINAGE_REWETTING:
                return TransactionTypeEnum.ART_34_WET_LAND_DRAINAGE_REWETTING;

            default:
                throw new IllegalArgumentException("LuluCFCode not supported: " + luluCFEnum);
        }

    }

    /**
     * Returns the TransactionOrEventTypeEnum per ITL notification type
     *
     * @param notificationTypeEnum
     * @param reportFormat TODO
     * @return
     */
    public static TransactionOrEventTypeEnum getEventTypeFromNotification(ITLNotificationTypeEnum notificationTypeEnum,
                                                                          ReportFormatEnum reportFormat) {
        switch (notificationTypeEnum) {
            case REVERSAL_STORAGE_CDM:
                if (reportFormat == ReportFormatEnum.CP1) {
                    return TransactionOrEventTypeEnum.SUBJECT_TO_REPLACEMENT_FOR_REVERSAL_OF_STORAGE;
                } else if (reportFormat == ReportFormatEnum.CP2) {
                    return TransactionOrEventTypeEnum.SUBJECT_TO_REVERSAL_OF_STORAGE;
                } else {
                    throw new IllegalArgumentException("Report Format not supported: " + reportFormat);
                }
            case NON_SUBM_CERT_REPORT_CDM:
                if (reportFormat == ReportFormatEnum.CP1) {
                    return TransactionOrEventTypeEnum.SUBJECT_TO_REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT;
                } else if (reportFormat == ReportFormatEnum.CP2) {
                    return TransactionOrEventTypeEnum.SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT;
                }  else {
                    throw new IllegalArgumentException("Report Format not supported: " + reportFormat);
                }
            case NET_REVERSAL_STORAGE_CDM_CCS_PROJECT:
                return TransactionOrEventTypeEnum.CC_SUBJECT_TO_NET_REVERSAL_OF_STORAGE;
            case NON_SUBM_CERT_REPORT_CDM_CCS_PROJECT:
                return TransactionOrEventTypeEnum.CC_SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT;
            default:
                throw new IllegalArgumentException("Notification not supported: " + notificationTypeEnum);
        }
    }

}
