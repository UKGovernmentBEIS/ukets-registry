package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class VerifiedEmissionsSurrenderedAllowancesReportData extends ReportData {
    int year;
    String regulator;
    String accountHolderName;
    String installationName;
    Long installationIdentifier;
    String permitIdentifier;
    String mainActivityTypeCode;
    Long allocationEntitlement;
    Long allocationReturned;
    Long allocationReversed;
    Long allocationTotal;
    Object verifiedEmissions;
    String accountClosure;
    Long totalSurrenderedAllowances;
    String dynamicComplianceStatus;
    String staticComplianceStatus;
}