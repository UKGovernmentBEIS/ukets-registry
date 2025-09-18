package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ComplianceManagementReportData extends ComplianceReportData {

    String accountFullIdentifier;
    Long accountHolderId;
    String accountName;
    String dynamicComplianceStatus;
    String accountOpeningDate;
    String accountClosureDate;
    Long surrenderBalance;
    String currentSurrenderStatus;
    Long validatedARs;
    Long enrolledARs;
    Long suspendedARs;
    Long totalARs;
    String totalArsUris;
    Long arChangesInProgress;
    String arNominationTaskIds;
    String arNominationTaskClaimants;
    String mostRecentSignIn;
    Long totalAvailableQuantity;
    Long totalReservedQuantity;
    Boolean surrenderTransactionPendingApproval;
    Boolean receiptOfAllowancesScheduled;
    Boolean fourEyesTalTransactions;
    Boolean transactionsOffTheTalAllowed;
    Boolean fourEyesSurrenderReturnOfExcessAllocation;
}

