package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.TransactionsPublicReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.keycloak.KeycloakDbService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionsPublicReportService implements ReportTypeService<TransactionsPublicReportData> {

    private final ReportDataMapper<TransactionsPublicReportData> mapper;
    private final KeycloakDbService keycloakDbService;

    @Override
    public ReportType reportType() {
        return ReportType.R0050;
    }

    @Override
    public List<Object> getReportDataRow(TransactionsPublicReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getTransferIdentifier());
        data.add(reportData.getTransferringAccountHolderName());
        data.add(reportData.getTransferringAccountType());
        data.add(reportData.getTransferringAccountIdentifier());
        data.add(reportData.getReceivingAccountHolderName());
        data.add(reportData.getReceivingAccountType()); // Updated to match "Receiving account holder type"
        data.add(reportData.getReceivingAccountIdentifier());
        data.add(reportData.getNumberOfUKAllowancesTransferred());
        data.add(reportData.getTypeOfTransfer());
        data.add(reportData.getDateTransferCompleted());
        data.add(reportData.getTimeTransferCompleted());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
            "Transfer identifier",
            "Transferring account holder name",
            "Transferring account type",
            "Transferring account identifier",
            "Receiving account holder name",
            "Receiving account holder type",
            "Receiving account identifier",
            "Number of UK Allowances transferred",
            "Type of transfer",
            "Date transfer completed",
            "Time transfer completed"
        );
    }

    @Override
    public List<TransactionsPublicReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
