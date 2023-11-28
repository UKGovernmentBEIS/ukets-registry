package gov.uk.ets.compliance.domain;

/**
 * Calculates the Last Year Compliance status , which is different from the Typical in case 
 * the current year equals the LYVE.
 * 
 * @author P35036
 *
 */
public class LastYearDynamicComplianceCalculator {

    ComplianceStatus calculate(ComplianceState complianceState) {

        if (complianceState.operatorDoesNotHaveReportingObligationsYet()) {
            return ComplianceStatus.NOT_APPLICABLE;
        }

        if (complianceState.isExcludedUntilLastObligation()) {
            return ComplianceStatus.EXCLUDED;
        }
        
        if (complianceState.isMissingEmissionsForNonExcludedYears()) {
            return ComplianceStatus.C;
        }

        // If the LYVE and current year are equal, we need to check current year as it was part of the reporting period.
        if (complianceState.isInLastYearOfVerifiedEmissions() && isMissingEmissionsForCurrentYear(complianceState)) {
            return ComplianceStatus.C;
        }

        if (complianceState.totalEmissions() > complianceState.surrendersSoFar()) {
            return ComplianceStatus.B;
        }

        return ComplianceStatus.A;
    }

    private boolean isMissingEmissionsForCurrentYear(ComplianceState complianceState) {
        int currentYear = complianceState.getCurrentYear();
        if (complianceState.isExcludedForYear(currentYear)) {
            return false;
        }
        return !complianceState.hasEmissionsForCurrentYear();
    }
    
}
