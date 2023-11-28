package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AccountsNoARsReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountsNoARsReportService implements ReportTypeService<AccountsNoARsReportData> {

    private final ReportDataMapper<AccountsNoARsReportData> mapper;

    @Override
    public List<AccountsNoARsReportData> generateReportData(ReportCriteria reportCriteria) {
        return mapper.mapData(reportCriteria);
    }

    @Override
    public List<AccountsNoARsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(AccountsNoARsReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolder().getId());
        data.add(reportData.getAccountHolder().getName());
        data.add(reportData.getAccount().getNumber());
        data.add(reportData.getAccount().getName());
        data.add(reportData.getAccount().getType());
        data.add(reportData.getAccount().getOpeningDate());
        data.add(reportData.getAccount().getStatus());
        data.add(reportData.getCompliantEntity().getInstallationPermitId());
        data.add(reportData.getCompliantEntity().getInstallationName());
        data.add(reportData.getCompliantEntity().getFirstYearOfVerifiedEmissionSubmission());
        data.add(reportData.getCompliantEntity().getLastYearOfVerifiedEmissionSubmission());
        data.add(reportData.getAccount().getComplianceStatus());
        data.add(reportData.getCompliantEntity().getRegulator());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return Stream
            .of("Account Holder ID", "Account Holder Name", "Account number", "Account name", "Account type",
                "Account opening date (UTC)", "Account status", "Permit", "Installation name", "FYV"
                ,"LYV", "Surrender status", "Regulator").collect(Collectors.toList());
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0001;
    }
}
