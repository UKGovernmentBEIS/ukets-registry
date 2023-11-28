package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionsBalanceReportData extends ReportData {

    private String accountType;

    private Long numberOfUnits;

    /**
     * This report is grouped in sub-groups with names "A", "B"
     */
    private String groupName;
}
