package gov.uk.ets.reports.generator.kyotoprotocol.sef.enums;

/**
 *
 * The report types.
 *
 * @author gkountak
 */
public enum ReportTypeEnum {

    R1ITL("R1_SEF_REPORT"),
    R2REG("R2_DISCREPANT_TRANSACTION"),
    R3REG("R3_NOTIFICATION_LIST"),
    R4REG("R4_NON_REPLACEMENTS_LIST"),
    R5REG("R5_INVALID_UNITS");

    private final String reportType;

    ReportTypeEnum(String s) {
        reportType = s;
    }

    public String getReportType () {
        return reportType;
    }
}
