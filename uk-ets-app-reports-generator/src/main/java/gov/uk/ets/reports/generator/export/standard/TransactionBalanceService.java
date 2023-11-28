package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.TransactionsBalanceReportData;
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
public class TransactionBalanceService implements ReportTypeService<TransactionsBalanceReportData> {

    private final ReportDataMapper<TransactionsBalanceReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0016;
    }

    @Override
    public List<Object> getReportDataRow(TransactionsBalanceReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getAccountType());
        data.add(reportData.getNumberOfUnits());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Account Type",
                "UKAs"
            );
    }

    @Override
    public List<TransactionsBalanceReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<TransactionsBalanceReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
