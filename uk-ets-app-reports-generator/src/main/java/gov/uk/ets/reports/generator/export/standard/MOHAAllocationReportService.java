package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.MOHAAllocationReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MOHAAllocationReportService implements ReportTypeService<MOHAAllocationReportData> {

    private final ReportDataMapper<MOHAAllocationReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0044;
    }

    @Override
    public List<Object> getReportDataRow(MOHAAllocationReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getMaritimeOperatorId());
        data.add(reportData.getImo());
        data.add(reportData.getMaritimeMonitoringPlanId());
        data.add(reportData.getExcludedForSchemeYear());
        data.add(reportData.getFirstYear());
        data.add(reportData.getLastYear());
        data.add(reportData.getRegulator());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
            "Account Holder Name",
            "Maritime Operator ID",
            "Company IMO number",
            "Emissions Monitoring Plan ID",
            "\"Excluded\" for scheme year",
            "First Year of Operation",
            "Last Year of Operation",
            "Regulator");
    }

    @Override
    public List<MOHAAllocationReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
