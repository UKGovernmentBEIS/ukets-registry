package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.TransactionVolumesAndNumberOfTransactionsReportData;
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
public class TransactionVolumesAndNumberOfTransactionsService implements ReportTypeService<TransactionVolumesAndNumberOfTransactionsReportData> {

    private final ReportDataMapper<TransactionVolumesAndNumberOfTransactionsReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0017;
    }

    @Override
    public List<Object> getReportDataRow(TransactionVolumesAndNumberOfTransactionsReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getNumberOfUnits());
        data.add(reportData.getTransactionType());
        data.add(reportData.getNumberOfTransactions());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Number of Units ",
                "Type of Transaction",
                "Number of Transactions"
            );
    }

    @Override
    public List<TransactionVolumesAndNumberOfTransactionsReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<TransactionVolumesAndNumberOfTransactionsReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
