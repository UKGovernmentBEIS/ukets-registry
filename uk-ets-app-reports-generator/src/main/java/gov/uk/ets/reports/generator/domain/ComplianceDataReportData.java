package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.sql.Date;

@SuperBuilder
@Data
@EqualsAndHashCode()
public class ComplianceDataReportData extends ReportData {
    Date activeDate;
    Long oHADssError;
    Long oHADssNotApplicable;
    Long oHADssExempt;
    Long oHADssC;
    Long oHADssB;
    Long oHADssA;
    Long totalOHA;
    Long aOHADssError;
    Long aOHADssNotApplicable;
    Long aOHADssExempt;
    Long aOHADssC;
    Long aOHADssB;
    Long aOHADssA;
    Long totalAOHA;
    Long mOHADssError;
    Long mOHADssNotApplicable;
    Long mOHADssExempt;
    Long mOHADssC;
    Long mOHADssB;
    Long mOHADssA;
    Long totalMOHA;
    BigDecimal oHACumulativeEmissions;
    BigDecimal oHACumulativeSurrenderedAllowances;
    BigDecimal oHASurrenderBalance;
    BigDecimal aOHACumulativeEmissions;
    BigDecimal aOHACumulativeSurrenderedAllowances;
    BigDecimal aOHASurrenderBalance;
    BigDecimal mOHACumulativeEmissions;
    BigDecimal mOHACumulativeSurrenderedAllowances;
    BigDecimal mOHASurrenderBalance;
    BigDecimal totalCumulativeEmissions;
    BigDecimal totalCumulativeSurrenderedAllowances;
    BigDecimal totalSurrenderBalance;
    Long ohaExemptMostRecentApplicable;
    Long ohaLiveMostRecentApplicable;
    Long aOhaExemptMostRecentApplicable;
    Long aOhaLiveMostRecentApplicable;
    Long mOhaExemptMostRecentApplicable;
    Long mOhaLiveMostRecentApplicable;
}
