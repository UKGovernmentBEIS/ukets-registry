package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import gov.uk.ets.reports.model.ReportType;

public class ConfigUtil {

    private ConfigUtil() {

    }

    public static boolean isKyotoProtocolReport(ReportType type) {
        return type.equals(ReportType.R0020) || type.equals(ReportType.R0021) || type.equals(ReportType.R0022)
                || type.equals(ReportType.R0023) || type.equals(ReportType.R0024);
    }
}
