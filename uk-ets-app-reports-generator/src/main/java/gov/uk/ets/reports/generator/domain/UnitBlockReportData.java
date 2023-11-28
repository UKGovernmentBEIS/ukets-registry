package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class UnitBlockReportData extends ReportData {

    Long unitBlockId;
    Long startBlock;
    Long endBlock;
    Long quantity;
    String unitType;
    String originatingCountryCode;
    Long accountNumber;
    String accountType;
    String originalCp;
    String applicableCp;
    String projectNumber;
    String projectTrack;
    String lastTransactionId;
    String sop;
    String environmentalActivity;
    String reservedForTransaction;
    String replaced;
    String reservedForReplacement;
}
