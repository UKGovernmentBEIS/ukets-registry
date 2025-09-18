package gov.uk.ets.reports.generator.export;

import gov.uk.ets.reports.generator.domain.ReportData;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import java.util.List;

public interface ReportTypeService<T extends ReportData> {

    /**
     * The report type
     *
     * @return
     */
    ReportType reportType();

    /**
     * Return a list of values for each report data row
     *
     * @param reportData
     * @return
     */
    List<Object> getReportDataRow(T reportData);

    /**
     * the report headers
     * @param year TODO
     *
     * @return
     */
    List<String> getReportHeaders(Long year);

    List<T> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo);

    default String getCellColor(int column, Object value) {
        return null;
    }
}
