package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AddOrReplaceARTasksReportData extends ReportData {

    private Long taskRequestIdentifier;
    private String requestType;
    private LocalDateTime initiatedDate;
    private Long taskWeeks;
    private String accountHolderName;
    private String accountType;
    private String accountFullIdentifier;
    private String disclosedName;
    private String urid;
    private String arUserStatus;
    private String nameClaimant;
	private Long totalOpenDocRequests;
	private Long totalCompletedDocRequests;

}
