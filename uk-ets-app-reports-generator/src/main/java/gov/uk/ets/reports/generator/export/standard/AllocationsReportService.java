package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AllocationsReportData;
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
public class AllocationsReportService implements ReportTypeService<AllocationsReportData> {

    private final ReportDataMapper<AllocationsReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0027;
    }

    @Override
    public List<Object> getReportDataRow(AllocationsReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getPermitOrMonitoringPlanId());
        data.add(reportData.getIdentifier());
        data.add(reportData.getInstallationName());
        data.add(reportData.getActivityTypeCode());
        data.add(reportData.getRegulator());
        data.add(reportData.getType());
        data.add(reportData.getWithholdStatus2021());
        data.add(reportData.getEntitlement2021());
        data.add(reportData.getAllocated2021());
        data.add(reportData.getRemaining2021());
        data.add(reportData.getWithholdStatus2022());
        data.add(reportData.getEntitlement2022());
        data.add(reportData.getAllocated2022());
        data.add(reportData.getRemaining2022());
        data.add(reportData.getWithholdStatus2023());
        data.add(reportData.getEntitlement2023());
        data.add(reportData.getAllocated2023());
        data.add(reportData.getRemaining2023());
        data.add(reportData.getWithholdStatus2024());
        data.add(reportData.getEntitlement2024());
        data.add(reportData.getAllocated2024());
        data.add(reportData.getRemaining2024());
        data.add(reportData.getWithholdStatus2025());
        data.add(reportData.getEntitlement2025());
        data.add(reportData.getAllocated2025());
        data.add(reportData.getRemaining2025());
        data.add(reportData.getWithholdStatus2026());
        data.add(reportData.getEntitlement2026());
        data.add(reportData.getAllocated2026());
        data.add(reportData.getRemaining2026());
        data.add(reportData.getWithholdStatus2027());
        data.add(reportData.getEntitlement2027());
        data.add(reportData.getAllocated2027());
        data.add(reportData.getRemaining2027());
        data.add(reportData.getWithholdStatus2028());
        data.add(reportData.getEntitlement2028());
        data.add(reportData.getAllocated2028());
        data.add(reportData.getRemaining2028());
        data.add(reportData.getWithholdStatus2029());
        data.add(reportData.getEntitlement2029());
        data.add(reportData.getAllocated2029());
        data.add(reportData.getRemaining2029());
        data.add(reportData.getWithholdStatus2030());
        data.add(reportData.getEntitlement2030());
        data.add(reportData.getAllocated2030());
        data.add(reportData.getRemaining2030());


        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Operator AH",
                "Permit or Monitoring plan",
                "Operator ID",
                "Installation name",
                "Installation Regulated activity",
                "Regulator",
                "Allocation table",
                "2021 Withhold status",
                "2021 Entitlement",
                "2021 Allocated",
                "2021 Remaining",
                "2022 Withhold status",
                "2022 Entitlement",
                "2022 Allocated",
                "2022 Remaining",
                "2023 Withhold status",
                "2023 Entitlement",
                "2023 Allocated",
                "2023 Remaining",
                "2024 Withhold status",
                "2024 Entitlement",
                "2024 Allocated",
                "2024 Remaining",
                "2025 Withhold status",
                "2025 Entitlement",
                "2025 Allocated",
                "2025 Remaining",
                "2026 Withhold status",
                "2026 Entitlement",
                "2026 Allocated",
                "2026 Remaining",
                "2027 Withhold status",
                "2027 Entitlement",
                "2027 Allocated",
                "2027 Remaining",
                "2028 Withhold status",
                "2028 Entitlement",
                "2028 Allocated",
                "2028 Remaining",
                "2029 Withhold status",
                "2029 Entitlement",
                "2029 Allocated",
                "2029 Remaining",
                "2030 Withhold status",
                "2030 Entitlement",
                "2030 Allocated",
                "2030 Remaining"
            );
    }

    @Override
    public List<AllocationsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
