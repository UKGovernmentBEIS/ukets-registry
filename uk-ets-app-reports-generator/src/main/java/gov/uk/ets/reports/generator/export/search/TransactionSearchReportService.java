package gov.uk.ets.reports.generator.export.search;

import gov.uk.ets.reports.generator.domain.TransactionSearchReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionSearchReportService implements ReportTypeService<TransactionSearchReportData> {

    private final ReportDataMapper<TransactionSearchReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0006;
    }

    @Override
    public List<Object> getReportDataRow(TransactionSearchReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getTransaction().getId());
        data.add(reportData.getTransaction().getType());
        data.add(ObjectUtils.firstNonNull(reportData.getTransaction().getReversesIdentifier(), reportData.getTransaction().getReversedByIdentifier()));
        data.add(reportData.getTransaction().getUnitType());
        data.add(reportData.getTransaction().getQuantity());
        data.add(reportData.getTransferringAccount().getNumber());
        data.add(reportData.getTransferringAccount().getType());
        data.add(reportData.getTransferringAccount().getName());
        data.add(reportData.getTransferringAccount().getAccountHolderName());
        data.add(reportData.getAcquiringAccount().getNumber());
        data.add(reportData.getAcquiringAccount().getType());
        data.add(reportData.getAcquiringAccount().getName());
        data.add(reportData.getAcquiringAccount().getAccountHolderName());
        data.add(reportData.getTransaction().getTransactionStart());
        data.add(reportData.getTransaction().getLastUpdated());
        data.add(reportData.getTransaction().getStatus());
        // TODO: transaction Failure reasons not provided in query
        data.add("NOT PROVIDED");


        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of("Transaction ID", "Transaction type", "Reversed or Original Transaction ID", "Unit type", "Unit Quantity", "Transferring account ID",
                "Transferring account type", "Transferring account name", "Transferring account holder", "Acquiring account ID", "Acquiring account type",
                "Acquiring account name", "Acquiring account holder", "Transaction start (UTC)",
                "Last updated on (UTC)", "Transaction status", "Failure reasons");
    }

    @Override
    public List<TransactionSearchReportData> generateReportData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<TransactionSearchReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
