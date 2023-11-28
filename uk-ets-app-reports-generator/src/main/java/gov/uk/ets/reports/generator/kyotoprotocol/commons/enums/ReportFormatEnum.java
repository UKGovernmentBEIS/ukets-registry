package gov.uk.ets.reports.generator.kyotoprotocol.commons.enums;

/**
 * Report format enumeration
 *
 * @author kattoulp
 *
 */
public enum ReportFormatEnum {

    CP1("RF-CP1"), CP2("RF-CP2");

    private final String value;

    ReportFormatEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

}
