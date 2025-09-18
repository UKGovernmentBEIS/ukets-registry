package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class OHAAllocationRAReportData extends ReportData {
    private String accountHolderName;
    private String installationId;
    private String installationName;
    private String permitIdentifier;
    private String activityType;
    private Integer firstYear;
    private String regulator;
    private Integer allocationYear;
    private Integer natEntitlement;
    private Integer nerEntitlement;
    private Integer totalEntitlement;
    private Integer natAllowancesReceived;
    private Integer nerAllowancesReceived;
    private Integer totalAllowancesReceived;
    private String withheld;
    private String excluded;
    private Integer lastYear;
}
