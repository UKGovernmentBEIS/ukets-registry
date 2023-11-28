package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionVolumesAndNumberOfTransactionsReportData extends ReportData {

    private Long numberOfUnits;

    private String transactionType;

    private Long numberOfTransactions;
}
