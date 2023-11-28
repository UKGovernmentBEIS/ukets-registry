package gov.uk.ets.reports.generator.export.search;

import gov.uk.ets.reports.generator.domain.TaskSearchUserReportData;
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
public class TaskSearchUserReportService implements ReportTypeService<TaskSearchUserReportData> {

    private final ReportDataMapper<TaskSearchUserReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0026;
    }

    @Override
    public List<Object> getReportDataRow(TaskSearchUserReportData reportData) {
        List<Object> data = new ArrayList<>();
        data.add(reportData.getTask().getTaskId());
        data.add(reportData.getTask().getTaskType());
        data.add(reportData.getTask().getInitiatorName());
        data.add(reportData.getTask().getClaimantName());
        data.add(reportData.getAccount().getNumber());
        data.add(reportData.getAccount().getType());
        data.add(reportData.getAccountHolder().getName());
        data.add(reportData.getTask().getAuthorisedRepresentative());
        data.add(reportData.getTask().getTransactionId());
        data.add(reportData.getTask().getCreatedOn());
        data.add(reportData.getTask().getCompletedOn());
        data.add(reportData.getTask().getTaskStatus());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List
            .of("Task ID", "Task type", "Name of initiator", "Name of claimant", "Account number",
                    "Account type", "AH name", "Acquiring account number", "Transaction ID", "Created on (UTC)",
                    "Completed on (UTC)", "Task status");

    }

    @Override
    public List<TaskSearchUserReportData> generateReportData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<TaskSearchUserReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }
}
