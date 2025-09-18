package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ComplianceReportData extends ReportData {

    String regulator;
    String accountHolderName;
    String accountType;
    String accountStatus;
    Long operatorId;
    String permitOrMonitoringPlanId;
    String installationName;
    String firstYearOfVerifiedEmissions;
    String lastYearOfVerifiedEmissions;

    // Emissions uploaded for each year.
    // If account excluded for a year then instead of emissions the value ‘Excluded’ is displayed
    String verifiedEmissions2021;
    String verifiedEmissions2022;
    String verifiedEmissions2023;
    String verifiedEmissions2024;
    String verifiedEmissions2025;
    String verifiedEmissions2026;
    String verifiedEmissions2027;
    String verifiedEmissions2028;
    String verifiedEmissions2029;
    String verifiedEmissions2030;

    // Total verified emissions up to the moment of the report generation
    Long cumulativeEmissions;
    // Total net surrenders up to the moment of the report generation
    Long cumulativeSurrenders;

    String staticSurrenderStatus2021;
    String staticSurrenderStatus2022;
    String staticSurrenderStatus2023;
    String staticSurrenderStatus2024;
    String staticSurrenderStatus2025;
    String staticSurrenderStatus2026;
    String staticSurrenderStatus2027;
    String staticSurrenderStatus2028;
    String staticSurrenderStatus2029;
    String staticSurrenderStatus2030;
}
