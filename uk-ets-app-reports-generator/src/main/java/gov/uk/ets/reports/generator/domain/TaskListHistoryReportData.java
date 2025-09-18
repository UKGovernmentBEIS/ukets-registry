package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskListHistoryReportData extends ReportData {


    private Long taskId;
    private String taskType;
    private String initiator;
    private String initiatorUid;
    private String userInitiated;
    private String initiated;
    private Integer age;
    private String ageCohort;
    private String claimant;
    private String ownershipDate;
    private Integer completedDocumentRequests;
    private Integer workInitiationLag;
    private String workProcessor;
    private String deadline;
    private String taskCompletionDate;
    private String taskStatus;
    private String taskOutcome;
    private String taskOutcomeComment;
    private String accountHolder;
    private String accountType;
    private String accountNumber;
    private String user;
    private String userUid;
}
