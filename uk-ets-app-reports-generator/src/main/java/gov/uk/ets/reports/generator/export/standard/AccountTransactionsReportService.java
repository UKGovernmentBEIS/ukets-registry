package gov.uk.ets.reports.generator.export.standard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gov.uk.ets.reports.generator.domain.AccountTransactionsReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountTransactionsReportService implements ReportTypeService<AccountTransactionsReportData> {

    private final ReportDataMapper<AccountTransactionsReportData> mapper;
    
    @Override
    public ReportType reportType() {
        return ReportType.R0035;
    }

    @Override
    public List<Object> getReportDataRow(
        AccountTransactionsReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getAccountName());
        data.add(reportData.getCompletionDate());
        data.add(reportData.getTransactionIdentifier());
        data.add(reportData.getOtherAccountIdentifier());
        data.add(reportData.getTransactionType());
        data.add(reportData.getUnitType());
        data.add(reportData.getUnitQuantityIncoming());
        data.add(reportData.getUnitQuantityOutgoing());
        data.add(reportData.getProjectId());
        data.add(reportData.getRunningBalance());
        data.add(reportData.getReportRegistryAccountType());
        data.add(reportData.getReportKyotoAccountType());
        data.add(reportData.getOtherRegistryAccountType());
        data.add(reportData.getOtherKyotoAccountType());
        data.add(reportData.getOtherAccountName());
        data.add(reportData.getReversesIdentifier());
        data.add(reportData.getReversedByIdentifier());
        
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of(
                "Account Holder",
                "Account Name",                
                "Transaction Completion Date",
                "Transaction ID",
                "Transferring / Acquiring Account ID",
                "Transaction Type",
                "Unit Type",
                "Unit Quantity Incoming (+)",
                "Unit Quantity Outgoing (-)",
                "Project ID",
                "Running Balance"
            );
    }

    @Override
    public List<AccountTransactionsReportData> generateReportData(
        ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

}
