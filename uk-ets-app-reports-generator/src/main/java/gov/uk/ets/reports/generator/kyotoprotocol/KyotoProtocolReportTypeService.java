package gov.uk.ets.reports.generator.kyotoprotocol;

import gov.uk.ets.reports.model.ReportType;

public interface KyotoProtocolReportTypeService {

    ReportType reportType();

    void generateReportData(KyotoProtocolParams params);
}
