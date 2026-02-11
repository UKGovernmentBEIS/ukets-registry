package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.OHAAllocationReportData;
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
public class OHAAllocationReportService implements ReportTypeService<OHAAllocationReportData> {

    private final ReportDataMapper<OHAAllocationReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0013;
    }

    @Override
    public List<Object> getReportDataRow(OHAAllocationReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getInstallationId());
        data.add(reportData.getInstallationName());
        data.add(reportData.getPermitIdentifier());
        data.add(reportData.getActivityType());
        data.add(reportData.getFirstYear());
        data.add(reportData.getRegulator());
        data.add(reportData.getEntitled());
        data.add(reportData.getAllocated());
        data.add(reportData.getSalesContactEmail());
        data.add(reportData.getSalesContactPhone());
        data.add(reportData.getUka1To99());
        data.add(reportData.getUka100To999());
        data.add(reportData.getUka1000Plus());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of("Account Holder Name", "Installation ID", "Installation Name", "Permit ID", "Regulated activity",
                "First Year of Operation", "Regulator", "Allocation Entitlement_"+ year, "Allocation Delivered_"+ year,
                "UKA Sales Contact Email", "UKA Sales Contact Phone", "1-99 UKAs", "100-999 UKAs", "1000+ UKAs");
    }

    @Override
    public List<OHAAllocationReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
