package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountTransactionsReportData extends ReportData {

    private String accountName;
    private String accountHolderName;
    private LocalDateTime completionDate;
    private String transactionIdentifier;
    private String otherAccountIdentifier;
    private String transactionType;
    private String unitType;
    private Long unitQuantityIncoming;
    private Long unitQuantityOutgoing;
    private String projectId;
    private Long runningBalance;
    private String reportRegistryAccountType;
    private String reportKyotoAccountType;
    private String otherRegistryAccountType;
    private String otherKyotoAccountType;
    private String otherAccountName;
    private String reversesIdentifier;
    private String reversedByIdentifier;
}
