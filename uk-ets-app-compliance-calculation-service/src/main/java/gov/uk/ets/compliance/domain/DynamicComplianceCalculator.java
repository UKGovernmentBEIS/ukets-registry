package gov.uk.ets.compliance.domain;


public class DynamicComplianceCalculator {
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

        if (complianceState.totalEmissionsExceptCurrentYear() > complianceState.surrendersSoFar()) {
            return ComplianceStatus.B;
        }

        return ComplianceStatus.A;
    }
}
