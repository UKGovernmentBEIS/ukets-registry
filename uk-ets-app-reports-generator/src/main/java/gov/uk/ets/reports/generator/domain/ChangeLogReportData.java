package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeLogReportData extends ReportData {

    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private String entity;
    private String accountNumber;
    private Long operatorId;
    private String updatedBy;
    private LocalDateTime updatedAt;

}