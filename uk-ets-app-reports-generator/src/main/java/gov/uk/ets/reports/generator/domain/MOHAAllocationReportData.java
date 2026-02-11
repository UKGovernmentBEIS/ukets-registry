package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class MOHAAllocationReportData extends ReportData {
    private String accountHolderName;
    private String maritimeOperatorId;
    private String imo;
    private String maritimeMonitoringPlanId;
    private String excludedForSchemeYear;
    private Integer firstYear;
    private Integer lastYear;
    private String regulator;
    private String salesContactEmail;
    private String salesContactPhone;
    private String uka1To99;
    private String uka100To999;
    private String uka1000Plus;
}
