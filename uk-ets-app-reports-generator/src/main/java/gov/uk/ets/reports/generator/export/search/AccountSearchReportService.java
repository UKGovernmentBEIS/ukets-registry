package gov.uk.ets.reports.generator.export.search;

import gov.uk.ets.reports.generator.domain.AccountSearchReportData;
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
public class AccountSearchReportService implements ReportTypeService<AccountSearchReportData> {

    private final ReportDataMapper<AccountSearchReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0007;
    }

    @Override
    public List<Object> getReportDataRow(AccountSearchReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccount().getNumber());
        data.add(reportData.getAccountHolder().getName());
        data.add(reportData.getAccountHolder().getId());
        data.add(reportData.getAccount().getType());
        data.add(reportData.getAccount().getName());
        data.add(reportData.getAccount().getStatus());
        data.add(reportData.getAccount().getComplianceStatus());
        data.add(reportData.getAccount().getBalance());
        data.add(reportData.getAccount().getRegulatorGroup());
        data.add(reportData.getAccount().getOpeningDate());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of("Account number", "AH name", "AH ID", "Account type", "Account name", "Account status",
                "Compliance status", "Balance", "Regulator", "Opened on (UTC)");
    }

    @Override
    public List<AccountSearchReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
