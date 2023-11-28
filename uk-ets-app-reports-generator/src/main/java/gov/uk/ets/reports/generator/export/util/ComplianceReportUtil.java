package gov.uk.ets.reports.generator.export.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ComplianceReportUtil {

    /**
     * Method for calculating the compliant entity's reporting years
     *
     * @param startYear the compliant entity start year
     * @param endYear the compliant entity end year
     * @param referenceYear the year until data are
     * @return the year by which the report will contain data
     **/
    public Integer calculateReportingYears(int startYear, int endYear, int referenceYear) {
        if (startYear <= referenceYear) {
            if (endYear != 0 && endYear <= referenceYear) {
                return endYear - startYear + 1;
            } else {
                return referenceYear - startYear + 1;
            }
        } else {
            return null;
        }
    }

    /**
     * Method for calculating  the compliant entity's reporting months
     *
     * @param startYear the compliant entity start year
     * @param endYear the compliant entity end year
     * @param referenceYear the year until data are
     * @return the year by which the report will contain data
     **/
    public Integer calculateReportingMonths(int startYear, int endYear, int referenceYear) {
        Integer reportingYears = calculateReportingYears(startYear, endYear, referenceYear);
        return reportingYears != null ? reportingYears * 12 : null;
    }

}
