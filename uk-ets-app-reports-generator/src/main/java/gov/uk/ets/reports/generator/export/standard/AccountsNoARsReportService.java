package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AccountsNoARsReportData;
import gov.uk.ets.reports.generator.domain.CompliantEntity;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountsNoARsReportService implements ReportTypeService<AccountsNoARsReportData> {

    private final ReportDataMapper<AccountsNoARsReportData> mapper;

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
        data.add(reportData.getAccount().getOpeningDate().toLocalDate());
        data.add(reportData.getAccount().getStatus());
        CompliantEntity compliantEntity  = reportData.getCompliantEntity();
        data.add(ObjectUtils.firstNonNull(
            compliantEntity.getInstallationPermitId(),
            compliantEntity.getAircraftMonitoringPlanId(),
            compliantEntity.getMaritimeMonitoringPlanId()));
        data.add(compliantEntity.getImo());
        data.add(compliantEntity.getInstallationName());
        data.add(compliantEntity.getFirstYearOfVerifiedEmissionSubmission());
        data.add(compliantEntity.getLastYearOfVerifiedEmissionSubmission());
        data.add(compliantEntity.getComplianceStatus());
        data.add(compliantEntity.getRegulator());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return Stream
            .of("Account Holder ID",
                    "Account Holder Name",
                    "Account number",
                    "Account name",
                    "Account type",
                    "Account opening date (UTC)",
                    "Account status",
                    "Permit/EMP",
                    "Company IMO number",
                    "Installation name",
                    "FYV",
                    "LYV",
                    "Surrender status",
                    "Regulator").collect(Collectors.toList());
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0001;
    }
}
