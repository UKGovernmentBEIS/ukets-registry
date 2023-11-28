package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.Compliance10YearsReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class Compliance10YearsReportService implements ReportTypeService<Compliance10YearsReportData> {

    private final ReportDataMapper<Compliance10YearsReportData> mapper;
    @Override
    public ReportType reportType() {
        return ReportType.R0025;
    }

    @Override
    public List<Object> getReportDataRow(Compliance10YearsReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getRegulator());
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAccountName());
        data.add(reportData.getAccountType());
        data.add(reportData.getInstallationName());
        data.add(reportData.getInstallationIdentifier());
        data.add(reportData.getActivityTypeCode());
        data.add(reportData.getPermitOrMonitoringPlanId());
        data.add(reportData.getFirstYearOfVerifiedEmissions());
        data.add(reportData.getLastYearOfVerifiedEmissions());
        data.add(reportData.getReportingYears());
        data.add(reportData.getReportingMonths());
        reportData.getAnnualEmissionsReported().forEach( v -> data.add(v));
        reportData.getSurrenderedEmissions().forEach( v -> data.add(v));
        reportData.getStaticSurrenderStatus().forEach( v -> data.add(v));
        reportData.getCumulativeVerifiedEmissions().forEach( v -> data.add(v));
        reportData.getCumulativeSurrenders().forEach(v -> data.add(v));
        reportData.getCumulativeAnnualEmissionsReported().forEach( v -> data.add(v));
        reportData.getFreeAllocations().forEach( v -> data.add(v));

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "UK Regulator",
                "AH name",
                "Account name",
                "Account type",
                "Installation name",
                "Installation ID / Aircraft operator ID",
                "Activity type",
                "Permit ID/Monitoring plan ID",
                "FYVE",
                "LYVE",
                "Reporting years",
                "Reporting months",
                "AER - Annual emissions reported 2021",
                "AER - Annual emissions reported 2022",
                "AER - Annual emissions reported 2023",
                "AER - Annual emissions reported 2024",
                "AER - Annual emissions reported 2025",
                "AER - Annual emissions reported 2026",
                "AER - Annual emissions reported 2027",
                "AER - Annual emissions reported 2028",
                "AER - Annual emissions reported 2029",
                "AER - Annual emissions reported 2030",
                "AER - Annual emissions reported Total",
                "Surrendered Emissions (by 30/4 per year) 2021",
                "Surrendered Emissions (by 30/4 per year) 2022",
                "Surrendered Emissions (by 30/4 per year) 2023",
                "Surrendered Emissions (by 30/4 per year) 2024",
                "Surrendered Emissions (by 30/4 per year) 2025",
                "Surrendered Emissions (by 30/4 per year) 2026",
                "Surrendered Emissions (by 30/4 per year) 2027",
                "Surrendered Emissions (by 30/4 per year) 2028",
                "Surrendered Emissions (by 30/4 per year) 2029",
                "Surrendered Emissions (by 30/4 per year) 2030",
                "Surrendered Emissions (by 30/4 per year) Total",
                "Static surrender status 2021",
                "Static surrender status 2022",
                "Static surrender status 2023",
                "Static surrender status 2024",
                "Static surrender status 2025",
                "Static surrender status 2026",
                "Static surrender status 2027",
                "Static surrender status 2028",
                "Static surrender status 2029",
                "Static surrender status 2030",
                "Cumulative verified emissions 2021",
                "Cumulative verified emissions 2022",
                "Cumulative verified emissions 2023",
                "Cumulative verified emissions 2024",
                "Cumulative verified emissions 2025",
                "Cumulative verified emissions 2026",
                "Cumulative verified emissions 2027",
                "Cumulative verified emissions 2028",
                "Cumulative verified emissions 2029",
                "Cumulative verified emissions 2030",
                "Cumulative surrenders (each year by 30/4) 2021",
                "Cumulative surrenders (each year by 30/4) 2022",
                "Cumulative surrenders (each year by 30/4) 2023",
                "Cumulative surrenders (each year by 30/4) 2024",
                "Cumulative surrenders (each year by 30/4) 2025",
                "Cumulative surrenders (each year by 30/4) 2026",
                "Cumulative surrenders (each year by 30/4) 2027",
                "Cumulative surrenders (each year by 30/4) 2028",
                "Cumulative surrenders (each year by 30/4) 2029",
                "Cumulative surrenders (each year by 30/4) 2030",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2021",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2022",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2023",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2024",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2025",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2026",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2027",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2028",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2029",
                "Cumulative AER (per year) - Cumulative surrenders (each year by 30/4) 2030",
                "Free allocations 2021",
                "Free allocations 2022",
                "Free allocations 2023",
                "Free allocations 2024",
                "Free allocations 2025",
                "Free allocations 2026",
                "Free allocations 2027",
                "Free allocations 2028",
                "Free allocations 2029",
                "Free allocations 2030",
                "Free allocations Total"
            );
    }

    @Override
    public List<Compliance10YearsReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<Compliance10YearsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
