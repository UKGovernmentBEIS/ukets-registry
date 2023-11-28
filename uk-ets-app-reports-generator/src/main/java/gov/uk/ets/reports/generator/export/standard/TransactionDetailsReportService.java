package gov.uk.ets.reports.generator.export.standard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gov.uk.ets.reports.generator.domain.TransactionDetailsReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionDetailsReportService implements ReportTypeService<TransactionDetailsReportData> {

    private final ReportDataMapper<TransactionDetailsReportData> mapper;
    
    @Override
    public ReportType reportType() {
        return ReportType.R0034;
    }

    @Override
    public List<Object> getReportDataRow(
        TransactionDetailsReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getIdentifier());
        data.add(reportData.getType());
        data.add(reportData.getStatus());
        data.add(reportData.getLastUpdateDate());
        data.add(reportData.getTransferringAccountHolder());
        data.add(reportData.getTransferringFullIdentifier());
        data.add(reportData.getAcquiringAccountHolder());
        data.add(reportData.getAcquiringFullIdentifier());
        data.add(reportData.getReference());
        data.add(reportData.getUnitType());
        data.add(reportData.getQuantity());
        data.add(reportData.getProjectNumber());
        data.add(reportData.getApplicablePeriod());


        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Transaction Identifier",
                "Transaction Type",
                "Transaction Status",
                "Date & Time of completion",
                "Transferring Account Holder",
                "Transferring Account",
                "Acquiring Account Holder",
                "Acquiring Account",
                "Transaction Reference",
                "Unit Type",
                "Quantity",
                "Project Number*",
                "Commitment Period*"
            );
    }

    @Override
    public List<TransactionDetailsReportData> generateReportData(
        ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<TransactionDetailsReportData> generateReportData(
        ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

}
