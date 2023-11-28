package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.TrustedAccountsReportData;
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
public class TrustedAccountsReportService implements ReportTypeService<TrustedAccountsReportData> {

    private final ReportDataMapper<TrustedAccountsReportData> mapper;

    @Override
    public List<TrustedAccountsReportData> generateReportData(ReportCriteria reportCriteria) {
        return mapper.mapData(reportCriteria);
    }

    @Override
    public List<TrustedAccountsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(TrustedAccountsReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getAccount().getNumber());
        data.add(reportData.getAccount().getStatus());

        data.add(reportData.getTrustedAccount().getNumber());
        data.add(reportData.getTrustedAccount().getStatus());
        data.add(reportData.getTrustedAccount().getDescription());
        data.add(reportData.getTrustedAccount().getName());
        data.add(reportData.getTrustedAccount().getActivationDate());
        data.add(reportData.getTrustedAccount().getType());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Account number",
                "Account status",

                "Trusted account number",
                "Trusted account status",
                "Trusted account description",
                "Trusted account name",
                "Date of activation (UTC)",
                "Trusted account type"
            );
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0009;
    }
}
