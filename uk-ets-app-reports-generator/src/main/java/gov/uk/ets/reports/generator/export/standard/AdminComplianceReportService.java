package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AdminComplianceReportData;
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
public class AdminComplianceReportService implements ReportTypeService<AdminComplianceReportData> {
    private final ReportDataMapper<AdminComplianceReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0032;
    }

    @Override
    public List<Object> getReportDataRow(AdminComplianceReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getRegulator());
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAccountNumber());
        data.add(reportData.getAccountName());
        data.add(reportData.getAccountType());
        data.add(reportData.getAccountStatus());
        data.add(reportData.getOperatorId());
        data.add(reportData.getPermitOrMonitoringPlanId());
        data.add(reportData.getInstallationName());
        data.add(reportData.getFirstYearOfVerifiedEmissions());
        data.add(reportData.getLastYearOfVerifiedEmissions());
        data.add(reportData.getDynamicComplianceStatus());

        data.add(reportData.getVerifiedEmissions2021());
        data.add(reportData.getVerifiedEmissions2022());
        data.add(reportData.getVerifiedEmissions2023());
        data.add(reportData.getVerifiedEmissions2024());
        data.add(reportData.getVerifiedEmissions2025());
        data.add(reportData.getVerifiedEmissions2026());
        data.add(reportData.getVerifiedEmissions2027());
        data.add(reportData.getVerifiedEmissions2028());
        data.add(reportData.getVerifiedEmissions2029());
        data.add(reportData.getVerifiedEmissions2030());

        data.add(reportData.getCumulativeEmissions());
        data.add(reportData.getCumulativeSurrenders());

        data.add(reportData.getStaticSurrenderStatus2021());
        data.add(reportData.getStaticSurrenderStatus2022());
        data.add(reportData.getStaticSurrenderStatus2023());
        data.add(reportData.getStaticSurrenderStatus2024());
        data.add(reportData.getStaticSurrenderStatus2025());
        data.add(reportData.getStaticSurrenderStatus2026());
        data.add(reportData.getStaticSurrenderStatus2027());
        data.add(reportData.getStaticSurrenderStatus2028());
        data.add(reportData.getStaticSurrenderStatus2029());
        data.add(reportData.getStaticSurrenderStatus2030());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Regulator",
                "AH name",
                "Account number",
                "Account name",
                "Account type",
                "Account status",
                "Operator ID",
                "Permit ID or Monitoring plan ID",
                "Installation name",
                "First Year of Verified emissions submission",
                "Last Year of Verified emissions submission",
                "Dynamic surrender status",
                "Verified emissions 2021",
                "Verified emissions 2022",
                "Verified emissions 2023",
                "Verified emissions 2024",
                "Verified emissions 2025",
                "Verified emissions 2026",
                "Verified emissions 2027",
                "Verified emissions 2028",
                "Verified emissions 2029",
                "Verified emissions 2030",
                "Cumulative emissions",
                "Cumulative surrenders",
                "Static surrender status 2021",
                "Static surrender status 2022",
                "Static surrender status 2023",
                "Static surrender status 2024",
                "Static surrender status 2025",
                "Static surrender status 2026",
                "Static surrender status 2027",
                "Static surrender status 2028",
                "Static surrender status 2029",
                "Static surrender status 2030"
            );
    }

    @Override
    public List<AdminComplianceReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
