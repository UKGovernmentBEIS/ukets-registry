package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class OHAAllocationReportData extends ReportData {
    private String accountHolderName;
    private Long installationId;
    private String installationName;
    private String permitIdentifier;
    private String activityType;
    private Integer firstYear;
    private String regulator;
    private Integer allocated;
    private Integer entitled;
    private String salesContactEmail;
    private String salesContactPhone;
    private String uka1To99;
    private String uka100To999;
    private String uka1000Plus;
}
