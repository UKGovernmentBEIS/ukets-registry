package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AOHAAllocationRAReportData;
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
public class AOHAAllocationRAReportService implements ReportTypeService<AOHAAllocationRAReportData> {

    private final ReportDataMapper<AOHAAllocationRAReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0042;
    }

    @Override
    public List<Object> getReportDataRow(AOHAAllocationRAReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAircraftOperatorId());
        data.add(reportData.getMonitoringPlanId());
        data.add(reportData.getFirstYear());
        data.add(reportData.getLastYear());
        data.add(reportData.getRegulator());
        data.add(reportData.getAllocationYear());
        data.add(reportData.getNavatEntitlement());
        data.add(reportData.getAllowancesReceived());
        data.add(reportData.getWithheld());
        data.add(reportData.getExcluded());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of("Account Holder Name", "Aircraft Operator ID", "Monitoring plan ID", "First Year of Operation", "Last Year of Operation",
                       "Regulator", "Allocation Year", "NAVAT Entitlement", "Allowances received", "Withheld", "Excluded");
    }

    @Override
    public List<AOHAAllocationRAReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
