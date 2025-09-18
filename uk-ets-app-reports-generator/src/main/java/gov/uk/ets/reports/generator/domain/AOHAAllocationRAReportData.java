package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AOHAAllocationRAReportData extends ReportData {
    private String accountHolderName;
    private String aircraftOperatorId;
    private String monitoringPlanId;
    private Integer firstYear;
    private String regulator;
    private Integer allocationYear;
    private Integer navatEntitlement;
    private Integer allowancesReceived;
    private String withheld;
    private String excluded;
    private Integer lastYear;
}
