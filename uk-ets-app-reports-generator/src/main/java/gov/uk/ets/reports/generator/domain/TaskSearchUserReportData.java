package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskSearchUserReportData extends ReportData {
    private Task task;
    private Account account;
    private AccountHolder accountHolder;
}
