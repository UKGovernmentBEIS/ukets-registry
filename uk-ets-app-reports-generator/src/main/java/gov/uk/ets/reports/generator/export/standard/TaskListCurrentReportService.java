package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AccountType;
import gov.uk.ets.reports.generator.domain.KeycloakUser;
import gov.uk.ets.reports.generator.domain.TaskListCurrentReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.keycloak.KeycloakDbService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.Color;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TaskListCurrentReportService implements ReportTypeService<TaskListCurrentReportData> {

    private final ReportDataMapper<TaskListCurrentReportData> mapper;
    private final KeycloakDbService keycloakDbService;

    @Override
    public ReportType reportType() {
        return ReportType.R0039;
    }

    @Override
    public List<Object> getReportDataRow(TaskListCurrentReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getTaskId());
        data.add(reportData.getTaskType());
        data.add(reportData.getPriority());
        data.add(reportData.getInitiator());
        data.add(reportData.getInitiatorUid());

        data.add(reportData.getUserInitiated());
        data.add(reportData.getInitiated());
        data.add(reportData.getAge());
        data.add(reportData.getAgeCohort());
        data.add(reportData.getClaimant());

        data.add(reportData.getOwnershipDate());
        data.add(reportData.getOpenDocumentRequests());
        data.add(reportData.getCompletedDocumentRequests());
        data.add(reportData.getWorkAllocationLagDays());
        data.add(reportData.getDeadline());

        data.add(reportData.getAccountHolder());
        data.add(reportData.getAccountType());
        data.add(reportData.getAccountNumber());
        data.add(reportData.getEnrolledARs());
        data.add(reportData.getValidatedARs());

        data.add(reportData.getSuspendedARs());
        data.add(reportData.getTotalARs());
        data.add(reportData.getArNominations());
        data.add(reportData.getUserTasks());
        data.add(reportData.getDynamicSurrenderStatus());

        data.add(reportData.getUser());
        data.add(reportData.getUserID());
        data.add(reportData.getStatus());
        data.add(reportData.getLastSignedIn());

        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
            "Task ID",
            "Task Type",
            "Priority",
            "Initiator",
            "Initiator UID",
            "User-Initiated",
            "Initiated",
            "Age (Weeks)",
            "AgeCohort",
            "Claimant",
            "Ownership Date",
            "Open Document Requests",
            "Completed Document Requests",
            "Work Allocation Lag (Days)",
            "Deadline",
            "Account Holder",
            "Account Type",
            "Account Number",
            "Enrolled ARs",
            "Validated ARs",
            "Suspended ARs",
            "Total ARs",
            "AR Nominations",
            "User Tasks",
            "Dynamic Surrender Status",
            "User",
            "User ID",
            "Status",
            "Last Signed In"
        );
    }

    @Override
    public List<TaskListCurrentReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        List<TaskListCurrentReportData> result = mapper.mapData(reportQueryInfo);

        List<KeycloakUser> keycloakUsers = keycloakDbService.retrieveAllUsers();
        result.forEach(taskListCurrentReportData -> {
            // Setting age cohort field.
            taskListCurrentReportData.setAgeCohort(getColor(taskListCurrentReportData.getAge()));

            // For the account opening tasks we dont have the label, only the enum.
            Optional.ofNullable(taskListCurrentReportData.getAccountType())
                .map(AccountType::getLabel)
                .ifPresent(taskListCurrentReportData::setAccountType);

            // Set AR counters to zero when an account exists.
            Optional.of(taskListCurrentReportData)
                .filter(data -> data.getAccountNumber() != null)
                .ifPresent(this::populateEmptyArCountersWitZero);

            // Setting user's last login.
            keycloakUsers.stream()
                .filter(keycloakUser -> Objects.equals(keycloakUser.getUrid(), taskListCurrentReportData.getUserID()))
                .findFirst()
                .ifPresent(keycloakUser -> taskListCurrentReportData.setLastSignedIn(mapper.prettyDate(keycloakUser.getLastLoginOn())));
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

    private void populateEmptyArCountersWitZero(TaskListCurrentReportData data) {
        if (data.getEnrolledARs() == null) data.setEnrolledARs(0);
        if (data.getValidatedARs() == null) data.setValidatedARs(0);
        if (data.getSuspendedARs() == null) data.setSuspendedARs(0);
        if (data.getTotalARs() == null) data.setTotalARs(0);
        if (data.getArNominations() == null) data.setArNominations(0);
    }
}
