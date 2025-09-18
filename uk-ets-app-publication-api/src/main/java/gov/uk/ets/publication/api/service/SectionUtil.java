package gov.uk.ets.publication.api.service;

import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.reports.model.ReportType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SectionUtil {

    private static final List<ReportType> REPORT_TYPES_WITH_YEAR =
        List.of(ReportType.R0013, ReportType.R0014, ReportType.R0044, ReportType.R0050,
            ReportType.R0020, ReportType.R0021, ReportType.R0022, ReportType.R0023,
            ReportType.R0024, ReportType.R0041, ReportType.R0042, ReportType.R0045);

    public boolean isYearlyReport(ReportType reportType) {
        return REPORT_TYPES_WITH_YEAR.contains(reportType);
    }

    public boolean isValid(ReportType reportType, DisplayType displayType) {
        return DisplayType.ONE_FILE_PER_YEAR != displayType || REPORT_TYPES_WITH_YEAR.contains(reportType);
    }
}
