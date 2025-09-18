package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.ComplianceDataReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.generator.statistic.ComplianceStatisticDataReportService;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ComplianceDataReportService implements ReportTypeService<ComplianceDataReportData> {
    private final ReportDataMapper<ComplianceDataReportData> mapper;
    private final ComplianceStatisticDataReportService complianceStatisticDataReportService;

    @Override
    public ReportType reportType() {
        return ReportType.R0049;
    }

    @Override
    public List<Object> getReportDataRow(ComplianceDataReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getActiveDate().toString());
        data.add(reportData.getOHADssError());
        data.add(reportData.getOHADssNotApplicable());
        data.add(reportData.getOHADssExempt());
        data.add(reportData.getOHADssC());
        data.add(reportData.getOHADssB());
        data.add(reportData.getOHADssA());
        data.add(reportData.getTotalOHA());
        data.add(reportData.getAOHADssError());
        data.add(reportData.getAOHADssNotApplicable());
        data.add(reportData.getAOHADssExempt());
        data.add(reportData.getAOHADssC());
        data.add(reportData.getAOHADssB());
        data.add(reportData.getAOHADssA());
        data.add(reportData.getTotalAOHA());
        data.add(reportData.getMOHADssError());
        data.add(reportData.getMOHADssNotApplicable());
        data.add(reportData.getMOHADssExempt());
        data.add(reportData.getMOHADssC());
        data.add(reportData.getMOHADssB());
        data.add(reportData.getMOHADssA());
        data.add(reportData.getTotalMOHA());

        data.add(reportData.getOHACumulativeEmissions());
        data.add(reportData.getOHACumulativeSurrenderedAllowances());
        data.add(reportData.getOHASurrenderBalance());
        data.add(reportData.getAOHACumulativeEmissions());
        data.add(reportData.getAOHACumulativeSurrenderedAllowances());
        data.add(reportData.getAOHASurrenderBalance());
        data.add(reportData.getMOHACumulativeEmissions());
        data.add(reportData.getMOHACumulativeSurrenderedAllowances());
        data.add(reportData.getMOHASurrenderBalance());
        data.add(reportData.getTotalCumulativeEmissions());
        data.add(reportData.getTotalCumulativeSurrenderedAllowances());
        data.add(reportData.getTotalSurrenderBalance());

        data.add(reportData.getOhaExemptMostRecentApplicable());
        data.add(reportData.getAOhaExemptMostRecentApplicable());
        data.add(reportData.getMOhaExemptMostRecentApplicable());
        data.add(reportData.getOhaLiveMostRecentApplicable());
        data.add(reportData.getAOhaLiveMostRecentApplicable());
        data.add(reportData.getMOhaLiveMostRecentApplicable());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of("Date",
                "OHA DSS Error",
                "OHA DSS Not Currently Applicable",
                "OHA DSS Exempt",
                "OHA DSS C",
                "OHA DSS B",
                "OHA DSS A",
                "Total OHAs",
                "AOHA DSS Error",
                "AOHA DSS Not Currently Applicable",
                "AOHA DSS Exempt",
                "AOHA DSS C",
                "AOHA DSS B",
                "AOHA DSS A",
                "Total AOHAs",
                "MOHA DSS Error",
                "MOHA DSS Not Currently Applicable",
                "MOHA DSS Exempt",
                "MOHA DSS C",
                "MOHA DSS B",
                "MOHA DSS A",
                "Total MOHAs",
                "OHA Cummulative Emissions",
                "OHA Cummulative Surrendered Allowances",
                "OHA Surrender Balance",
                "AOHA Cummulative Emissions",
                "AOHA Cummulative Surrendered Allowances",
                "AOHA Surrender Balance",
                "MOHA Cummulative Emissions",
                "MOHA Cummulative Surrendered Allowances",
                "MOHA Surrender Balance",
                "Total Cummulative Emissions",
                "Total Cummulative Surrendered Allowances",
                "Total Surrender Balance",
                "OHA Exempt for the Most Recent Applicable Scheme Year",
                "AOHA Exempt for the Most Recent Applicable Scheme Year",
                "MOHA Exempt for the Most Recent Applicable Scheme Year",
                "OHA Live for the Most Recent Applicable Scheme Year",
                "AOHA Live for the Most Recent Applicable Scheme Year",
                "MOHA Live for the Most Recent Applicable Scheme Year"
            );
    }

    @Override
    public List<ComplianceDataReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        complianceStatisticDataReportService.calculateSnapshot(); // Calculate current snapshot
        return mapper.mapData(reportQueryInfo);
    }
}
