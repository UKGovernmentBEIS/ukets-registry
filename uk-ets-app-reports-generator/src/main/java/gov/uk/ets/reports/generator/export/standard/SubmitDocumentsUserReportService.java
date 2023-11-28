package gov.uk.ets.reports.generator.export.standard;


import gov.uk.ets.reports.generator.domain.SubmitDocumentsUserReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andreas Karmenis
 * @created 18/01/2023 - 7:17 PM
 * @project uk-ets-app-reports-generator
 */
@RequiredArgsConstructor
@Service
public class SubmitDocumentsUserReportService implements ReportTypeService<SubmitDocumentsUserReportData> {

    private final ReportDataMapper<SubmitDocumentsUserReportData> mapper;
    @Override
    public ReportType reportType() {
        return ReportType.R0036;
    }

    @Override
    public List<Object> getReportDataRow(SubmitDocumentsUserReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getUserId());
        data.add(reportData.getFirstName());
        data.add(reportData.getLastName());
        data.add(reportData.getRequestDocumentId());
        data.add(reportData.getTaskCreationDate());
        data.add(reportData.getTaskLastUpdateDate());
        data.add(reportData.getTaskStatus());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
                "User ID",
                "First And Middle Names",
                "Last Name",
                "Request Document Task ID",
                "Task creation Date",
                "Task Last Updated Date",
                "Task Status"
        );
    }

    @Override
    public List<SubmitDocumentsUserReportData> generateReportData(ReportCriteria criteria) {
        return mapper.mapData(criteria);
    }

    @Override
    public List<SubmitDocumentsUserReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
