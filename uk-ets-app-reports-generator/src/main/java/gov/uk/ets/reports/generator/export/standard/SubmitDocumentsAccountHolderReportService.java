package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.SubmitDocumentsAccountHolderReportData;
import gov.uk.ets.reports.generator.domain.SubmitDocumentsUserReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Karmenis
 * @created 27/01/2023 - 8:50 PM
 * @project uk-ets-app-reports-generator
 */
@RequiredArgsConstructor
@Service
public class SubmitDocumentsAccountHolderReportService implements ReportTypeService<SubmitDocumentsAccountHolderReportData>  {

    private final ReportDataMapper<SubmitDocumentsAccountHolderReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0037;
    }

    @Override
    public List<Object> getReportDataRow(SubmitDocumentsAccountHolderReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getAccountHolderId());
        data.add(reportData.getAccountHolderName());
        data.add(reportData.getRequestDocumentId());
        data.add(reportData.getTaskCreationDate());
        data.add(reportData.getTaskLastUpdateDate());
        data.add(reportData.getTaskStatus());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {

        return List.of(
                "Account Holder ID",
                "Account Holder Name",
                "Request Document Task ID",
                "Task creation Date",
                "Task Last Updated Date",
                "Task Status"
        );
    }

    @Override
    public List<SubmitDocumentsAccountHolderReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
