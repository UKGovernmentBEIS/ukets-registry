package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.OHAAllocationRAReportData;
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
public class OHAAllocationRAReportService implements ReportTypeService<OHAAllocationRAReportData> {

    private final ReportDataMapper<OHAAllocationRAReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0041;
    }

    @Override
    public List<Object> getReportDataRow(OHAAllocationRAReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getInstallationId());
        data.add(reportData.getInstallationName());
        data.add(reportData.getPermitIdentifier());
        data.add(reportData.getActivityType());
        data.add(reportData.getFirstYear());
        data.add(reportData.getLastYear());
        data.add(reportData.getRegulator());
        data.add(reportData.getAllocationYear());
        data.add(reportData.getNatEntitlement());
        data.add(reportData.getNerEntitlement());
        data.add(reportData.getTotalEntitlement());
        data.add(reportData.getNatAllowancesReceived());
        data.add(reportData.getNerAllowancesReceived());
        data.add(reportData.getTotalAllowancesReceived());
        data.add(reportData.getWithheld());
        data.add(reportData.getExcluded());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of("Account Holder Name", "Installation ID", "Installation Name", "Permit ID", "Activity Type",
                "First Year of Operation", "Last Year of Operation", "Regulator", "Allocation Year", "NAT Entitlement", "NER Entitlement", "Total Entitlement",
                "NAT Allowances received", "NER Allowances received", "Total Allowances received", "Withheld", "Excluded");
    }

    @Override
    public List<OHAAllocationRAReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
