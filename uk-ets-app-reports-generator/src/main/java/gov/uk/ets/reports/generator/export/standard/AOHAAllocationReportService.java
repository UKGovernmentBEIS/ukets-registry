package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AOHAAllocationReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AOHAAllocationReportService implements ReportTypeService<AOHAAllocationReportData> {

    private final ReportDataMapper<AOHAAllocationReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0014;
    }

    @Override
    public List<Object> getReportDataRow(AOHAAllocationReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getSalesContactEmail());
        data.add(reportData.getSalesContactPhone());
        data.add(reportData.getAircraftOperatorId());
        data.add(reportData.getMonitoringPlanId());
        data.add(reportData.getFirstYear());
        data.add(reportData.getRegulator());
        data.add(reportData.getAllocated());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of("Account Holder Name", "Sales Contact Email", "Sales Contact Phone", "Aircraft Operator ID", "Monitoring plan ID",
                "First Year of Operation", "Regulator", "Allocation_"+ year);
    }

    @Override
    public List<AOHAAllocationReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<AOHAAllocationReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
