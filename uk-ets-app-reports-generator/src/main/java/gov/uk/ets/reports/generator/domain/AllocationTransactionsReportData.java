package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AllocationTransactionsReportData extends ReportData {
    Transaction transaction;
    Account transferringAccount;
    Account acquiringAccount;
    int allocationYear;
    String allocationType;
}
