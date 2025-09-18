package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AllocationReturnsReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AllocationReturnsReportService implements ReportTypeService<AllocationReturnsReportData> {

    private final ReportDataMapper<AllocationReturnsReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0043;
    }

    @Override
    public List<Object> getReportDataRow(AllocationReturnsReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getRegulator());
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getPermitOrMonitoringPlanId());
        data.add(reportData.getOperatorId());
        data.add(reportData.getInstallationName());
        data.add(reportData.getAllocationType());
        data.add(reportData.getYear());
        data.add(reportData.getQuantity());
        data.add(reportData.getTransactionId());
        data.add(reportData.getExecutionDate());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Regulator",
                "Account Holder Name",
                "Permit or Monitoring Plan",
                "Operator ID",
                "Installation name",
                "Allocation type",
                "Year",
                "Quantity",
                "Transaction ID",
                "Date and Time of completion"
            );
    }

    @Override
    public List<AllocationReturnsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
