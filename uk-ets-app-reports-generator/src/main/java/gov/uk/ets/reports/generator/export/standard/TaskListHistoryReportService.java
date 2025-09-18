package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.TaskListHistoryReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.Color;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskListHistoryReportService implements ReportTypeService<TaskListHistoryReportData> {

    private final ReportDataMapper<TaskListHistoryReportData> mapper;

    @Override
    public ReportType reportType() {
        return ReportType.R0040;
    }

    @Override
    public List<Object> getReportDataRow(TaskListHistoryReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getTaskId());
        data.add(reportData.getTaskType());
        data.add(reportData.getInitiator());
        data.add(reportData.getInitiatorUid());
        data.add(reportData.getUserInitiated());
        data.add(reportData.getInitiated());
        data.add(reportData.getAge());
        data.add(reportData.getAgeCohort());
        data.add(reportData.getClaimant());
        data.add(reportData.getOwnershipDate());
        data.add(reportData.getCompletedDocumentRequests());
        data.add(reportData.getWorkInitiationLag());
        data.add(reportData.getWorkProcessor());
        data.add(reportData.getDeadline());
        data.add(reportData.getTaskCompletionDate());
        data.add(reportData.getTaskStatus());
        data.add(reportData.getTaskOutcome());
        data.add(reportData.getTaskOutcomeComment());
        data.add(reportData.getAccountHolder());
        data.add(reportData.getAccountType());
        data.add(reportData.getAccountNumber());
        data.add(reportData.getUser());
        data.add(reportData.getUserUid());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
                "Task ID",
                "Task Type",
                "Initiator",
                "Initiator ID",
                "User-Initiated",
                "Initiated",
                "Age",
                "AgeCohort",
                "Claimant",
                "Ownership Date",
                "Completed Document Requests",
                "Work Initiation Lag",
                "Work Processor",
                "Deadline",
                "Task Completion Date",
                "Task Status",
                "Task Outcome",
                "Task Outcome Comment",
                "Account Holder",
                "Account Type",
                "Account Number",
                "User",
                "User ID"
        );
    }

    @Override
    public List<TaskListHistoryReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        List<TaskListHistoryReportData> result = mapper.mapData(reportQueryInfo);

        result.forEach(TaskListHistoryReportData -> {
            // Setting age cohort field.
            TaskListHistoryReportData.setAgeCohort(getColor(TaskListHistoryReportData.getAge()));
        });

        return result;
    }

    @Override
    public String getCellColor(int column, Object value) {
        if (!getReportHeaders(null).get(column).equals("AgeCohort")) {
            return null;
        }

        if (value instanceof String ageCohort) {
            return switch (ageCohort) {
                case "RED" -> Color.RED;
                case "BLUE" -> Color.ROYAL_BLUE;
                case "ORANGE" -> Color.ORANGE;
                default -> null;
            };
        }

        return null;
    }

    private String getColor(Integer age) {
        if (age >= 12) return "RED";
        if (age >= 8) return "BLUE";
        if (age >= 4) return "ORANGE";
        return "WHITE";
    }

}
