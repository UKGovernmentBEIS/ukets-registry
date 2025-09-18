package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AllocationPreparationReportData extends ReportData {

    String regulator;
    String accountHolderName;
    String accountNumber;
    String accountName;
    String accountType;
    String accountStatus;
    Long operatorId;
    String permitOrMonitoringPlanId;
    String installationName;
    int year;
    String allocationType;
    Long entitlement;
    Long totalEntitlementForSchemeYear;
    Long allocated;
    Long toBeReturned;
    Long toBeDelivered;
    String excluded;
    String withheld;
}
