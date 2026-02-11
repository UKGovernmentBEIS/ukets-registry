package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AOHAAllocationReportData extends ReportData {
    private String accountHolderName;
    private Long aircraftOperatorId;
    private String monitoringPlanId;
    private Integer firstYear;
    private String regulator;
    private Integer entitled;
    private Integer allocated;
    private String salesContactEmail;
    private String salesContactPhone;
    private String uka1To99;
    private String uka100To999;
    private String uka1000Plus;
}
