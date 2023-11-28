package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class KPAccountInformationReportData  extends ReportData {
    private String accountName;
    private String accountNumber;
    private String accountType;
    private Integer commitmentPeriod;
    private String accountHolderName;
    private Integer numberOfARs;
}
