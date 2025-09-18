package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskListCurrentReportData extends ReportData {

    private Long taskId;
    private String taskType;
    private Integer priority;
    private String initiator;
    private String initiatorUid;

    private String userInitiated;
    private String initiated;
    private Integer age;
    private String ageCohort;
    private String claimant;

    private String ownershipDate;
    private Integer openDocumentRequests;
    private Integer completedDocumentRequests;
    private String workAllocationLagDays;
    private String deadline;

    private String accountHolder;
    private String accountType;
    private String accountNumber;
    private Integer enrolledARs;
    private Integer validatedARs;

    private Integer suspendedARs;
    private Integer totalARs;
    private Integer arNominations;
    private Integer userTasks;
    private String dynamicSurrenderStatus;

    private String user;
    private String userID;
    private String status;
    private String lastSignedIn;

}
