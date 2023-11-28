package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionDetailsReportData extends ReportData {

    private String identifier;
    private String status;
    private String type;
    private LocalDateTime lastUpdateDate;
    private String transferringAccountHolder;
    private String transferringFullIdentifier;
    private String acquiringAccountHolder;
    private String acquiringFullIdentifier;
    private Long quantity;
    private String applicablePeriod;
    private String projectNumber;
    private String reference;
    private String unitType;
}
