package gov.uk.ets.compliance.domain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Set;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
public class ComplianceState {
    // for performance reason use common objectMapper instance
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private int currentYear;
    /** It is the DCS that does not perform any last-year specific calculation in its main algorithm. */
    private ComplianceStatus status;
    /** It is a DCS that takes into account the closing year emissions of the compliant entity in its calculation algorithm. */
    private ComplianceStatus lyStatus;
    private Date calculatedOn;
    private long surrendersSoFar;
    private YearlyEmissions yearlyEmissions;
    private Integer firstYearOfVerifiedEmissions;
    private Integer lastYearOfVerifiedEmissions;
    private ReportingPeriod reportingPeriod;
    // we need to save this date in the state to be able to compare with static compliance request date
    private LocalDateTime dateRequested;

    /**
     * Initialize state is triggered by the {@link gov.uk.ets.compliance.domain.events.AccountCreationEvent}.
     *
     * @param currentYear                  the current year
     * @param firstYearOfVerifiedEmissions the first year of verified emissions
     * @param lastYearOfVerifiedEmissions  the last year of verified emissions
     * @return the initial ComplianceState. At this point the state does not hold any status.
     */
    public static ComplianceState initialize(int currentYear, Integer firstYearOfVerifiedEmissions,
                                             Integer lastYearOfVerifiedEmissions) {
        ComplianceState complianceState = new ComplianceState();
        complianceState.currentYear = currentYear;

        complianceState.calculatedOn = new Date();
        complianceState.surrendersSoFar = 0L;
        complianceState.lyStatus = null;
        complianceState.firstYearOfVerifiedEmissions = firstYearOfVerifiedEmissions;
        complianceState.lastYearOfVerifiedEmissions = lastYearOfVerifiedEmissions;
        int reportingPeriodEnd = calculateEndYear(firstYearOfVerifiedEmissions, lastYearOfVerifiedEmissions, currentYear);
        complianceState.reportingPeriod = new ReportingPeriod(firstYearOfVerifiedEmissions, reportingPeriodEnd);
        complianceState.yearlyEmissions = new YearlyEmissions();
        return complianceState;
    }

    private static int calculateEndYear(int startYear, Integer endYear, int currentYear) {
        int reportingPeriodEnd = endYear != null ? endYear : currentYear - 1;
        return Math.max(startYear, reportingPeriodEnd);
    }

    /**
     * Creates a clone of the compliance State.
     */
    @SneakyThrows
    public ComplianceState deepCopy() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(objectMapper.writeValueAsString(this), ComplianceState.class);
    }

    /**
     * Validates the new state after an event is received.
     *
     * @return true if the state is valid
     */
    public boolean isValid() {

        boolean excludedYearWithNonZeroEmissions = yearlyEmissions.getExcludedYearsSet()
                .stream()
                .anyMatch(year -> yearlyEmissions.hasNonZeroEmissionsForYear(year));

        boolean emissionsBeforeFirstYearOfVerification = yearlyEmissions.getReportedEmissionsMap().keySet()
                .stream()
                .filter(year -> yearlyEmissions.hasNonZeroEmissionsForYear(year))
                .anyMatch(year -> year < reportingPeriod.getStartYear());

        boolean emissionsAfterLastYearOfVerification = yearlyEmissions.getReportedEmissionsMap().keySet()
                .stream()
                .filter(year -> yearlyEmissions.hasNonZeroEmissionsForYear(year))
                .anyMatch(year -> year > reportingPeriod.getEndYear());

        return !(excludedYearWithNonZeroEmissions
                || emissionsBeforeFirstYearOfVerification
                || emissionsAfterLastYearOfVerification
                || status == null);
    }

    public void status(ComplianceStatus status) {
        this.status = status;
    }

    protected void lastYearStatus(ComplianceStatus lyStatus) {
        this.lyStatus = lyStatus;
    }

    /**
     * If the last year status exists return then return the last year status 
     * otherwise return the typical status.
     * @return the DCS that takes into account the closing year emissions of the compliant entity in its calculation algorithm.
     */
    public ComplianceStatus getDynamicStatus() {
        return getLyStatus() != null ? getLyStatus() : getStatus();
    }    
    
    /**
     * Updates the reported emissions. If the verifiedEmissions is null then
     * it resets the reported emissions
     *
     * @param year              the reported emissions year
     * @param verifiedEmissions the amount of emissions reported.
     */
    public void updateVerifiedEmissions(int year, Long verifiedEmissions) {
        if (verifiedEmissions == null) {
            yearlyEmissions.resetEmissionsForYear(year);
        } else {
            yearlyEmissions.updateEmissions(year, verifiedEmissions);
        }
    }
    
    public Long totalEmissions() {
        return yearlyEmissions.countEmissions();
    }
    
    public Long totalEmissionsExceptCurrentYear() {
        return yearlyEmissions.countEmissionsExceptYear(currentYear);
    }
    
    public boolean hasEmissionsForCurrentYear() {
        return yearlyEmissions.hasNonZeroEmissionsForYear(currentYear) || yearlyEmissions.hasZeroEmissionsForYear(currentYear);
    }
    
    public boolean isMissingEmissionsForNonExcludedYears() {
        List<Integer> yearsWithMissingEmissions = yearsWithMissingEmissions();
        return yearsWithMissingEmissions.stream().anyMatch(year -> !isExcludedForYear(year));
    }
    
    public boolean isInLastYearOfVerifiedEmissions() {
        return Optional.ofNullable(lastYearOfVerifiedEmissions).isPresent() && currentYear == lastYearOfVerifiedEmissions;
    }

    private List<Integer> yearsWithMissingEmissions() {
        int startObligationYear = reportingPeriod.startYear;

        int endObligationYear = Math.min(this.currentYear - 1, reportingPeriod.getEndYear());
        return yearlyEmissions.getYearsWithMissingEmissions(
                startObligationYear,
                endObligationYear
        );
    }

    public long surrendersSoFar() {
        return surrendersSoFar;
    }

    public boolean isExcludedUntilLastObligation() {
        Set<Integer> excludedYears = yearlyEmissions.getExcludedYearsSet();
        if (excludedYears.isEmpty()) {
            return false;
        }

        int lastObligationYear = Math.min(this.currentYear - 1, reportingPeriod.getEndYear());
        Set<Integer> yearsUntilEndObligation = reportingPeriod.getReportingYearsUntil(lastObligationYear);
        return excludedYears.containsAll(yearsUntilEndObligation);
    }

    public boolean isExcludedForYear(int year) {
        return yearlyEmissions.getExcludedYearsSet().contains(year);
    }

    public boolean operatorDoesNotHaveReportingObligationsYet() {
        return currentYear < reportingPeriod.startYear ||
                (currentYear == reportingPeriod.startYear &&
                        (lastYearOfVerifiedEmissions == null || currentYear != reportingPeriod.getEndYear()));

    }

    public void nextYear() {
        currentYear++;

    }

    /**
     * Update the first year of the verified emissions. it should also set the reporting period start year
     * There is always the hard constrain that the reporting period start year cannot be later
     * that the reporting period end year.
     *
     * @param firstYearOfVerifiedEmissions the first year of verified emissions.
     */
    public void updateFirstYearOfVerifierEmissions(Integer firstYearOfVerifiedEmissions) {
        this.firstYearOfVerifiedEmissions = firstYearOfVerifiedEmissions;
        updateReportingPeriod();
    }

    /**
     * Update the last year of the verified emissions. it should also set the reporting period end year
     * if the provided value is not null. There is always the hard constrain that the
     * reporting period end cannot be earlier that the reporting period start
     *
     * @param lastYearOfVerifiedEmissions the last year of verified emissions
     */
    public void updateLastYearOfVerifierEmissions(Integer lastYearOfVerifiedEmissions) {
        this.lastYearOfVerifiedEmissions = lastYearOfVerifiedEmissions;
        updateReportingPeriod();
    }

    private void updateReportingPeriod() {
        this.reportingPeriod.startYear = firstYearOfVerifiedEmissions;
        this.reportingPeriod.endYear = calculateEndYear(firstYearOfVerifiedEmissions, lastYearOfVerifiedEmissions, currentYear);
    }

    /**
     * Sets the reporting period end
     * It cannot be previously than the startYear.
     *
     * @param reportingPeriodEnd the reporting period end value.
     */
    public void updateReportingPeriodEnd(int reportingPeriodEnd) {
        reportingPeriod.endYear = Math.max(reportingPeriod.startYear, reportingPeriodEnd);
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public void surrender(long amount) {
        surrendersSoFar += amount;
    }

    public void surrenderReversal(long amount) {
        surrendersSoFar -= amount;
    }

    public void exclude(int year) {
        yearlyEmissions.getExcludedYearsSet().add(year);
    }

    public void excludeReverse(int year) {
        yearlyEmissions.getExcludedYearsSet().remove(year);
    }

    public int previousYear() {
        return this.currentYear - 1;
    }

    public void saveRequestDate(LocalDateTime dateRequested) {
        this.dateRequested = dateRequested;
    }
}
