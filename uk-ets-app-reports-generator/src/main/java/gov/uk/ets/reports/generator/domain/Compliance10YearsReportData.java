package gov.uk.ets.reports.generator.domain;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Compliance10YearsReportData extends ReportData {

    String regulator;
    String accountHolderName;
    String accountName;
    String accountType;
    String installationName;
    Long installationIdentifier;
    String activityTypeCode;
    String permitOrMonitoringPlanId;
    String firstYearOfVerifiedEmissions;
    String lastYearOfVerifiedEmissions;
    Integer reportingYears;
    Integer reportingMonths;
    List<String> annualEmissionsReported;
    List<String> surrenderedEmissions;
    List<String> staticSurrenderStatus;
    List<String> cumulativeVerifiedEmissions;
    List<String> cumulativeSurrenders;
    List<String> cumulativeAnnualEmissionsReported;
    List<String> freeAllocations;

}
