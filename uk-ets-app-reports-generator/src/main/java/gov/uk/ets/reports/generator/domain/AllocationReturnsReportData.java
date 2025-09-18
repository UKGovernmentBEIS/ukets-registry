package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AllocationReturnsReportData extends ReportData {

    String regulator;
    String accountHolderName;
    String permitOrMonitoringPlanId;
    Long operatorId;
    String installationName;
    String allocationType;
    Integer year;
    Long quantity;
    String transactionId;
    LocalDateTime executionDate;
}
