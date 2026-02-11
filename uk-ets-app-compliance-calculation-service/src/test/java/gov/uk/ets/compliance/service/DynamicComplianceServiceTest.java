package gov.uk.ets.compliance.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.uk.ets.compliance.domain.ComplianceStatus;
import gov.uk.ets.compliance.domain.DynamicCompliance;
import gov.uk.ets.compliance.domain.events.CompliantEntityInitializationEvent;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;
import gov.uk.ets.compliance.utils.DynamicComplianceServiceTestBase;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DynamicComplianceServiceTest extends DynamicComplianceServiceTestBase {

    @Autowired
    DynamicComplianceService dynamicComplianceService;
    @Autowired
    DynamicComplianceRepository dynamicComplianceRepository;


    @BeforeEach
    private void setup() {
        dynamicComplianceRepository.deleteAll();
    }

    @Test
    @DisplayName("Compliant Entity Initialization event first year of verified emissions > current year")
    void testComplianceCalculationAccountCreation() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2023, null, 2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Compliant Entity Initialization event when first year of verified emissions < current year")
    void testComplianceCalculationAccountCreationFirstYearAfter() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2020, null, 2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    //FYVE = Current = LYVE → Surrender status will be NOT_APPLICABLE. It will be C, B, Excluded or A on 1/1/FYVE+1
    @Test
    @DisplayName("Compliant Entity Initialization event when FYVE=LYVE=currentYEar")
    void testComplianceCalculationAccountCreationAmendment1() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2020, 2020, 2020));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(2021));
        assertNotEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
    }

    /**
     * FYVE < Current = LYVE → Surrender status will be C, B, Excluded or A and the algorithm will 'wait' for emissions to be uploaded up to the current year.
     * → If for all past years emissions have been reported but for the current no emissions have been reported → Surrender status = C, and if they have been reported Total emissions will be the sum of the emissions up to current year and based on that we will calculate the balance to see if Total emissions > Total surrenders
     */
    @Test
    @DisplayName("Compliant Entity Initialization event when FYVE< Current = LYVE")
    void testComplianceCalculationAccountCreationAmendment2() {
        var result = dynamicComplianceService.processEvent(CompliantEntityInitializationEvent
            .builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(1000L)
            .dateTriggered(LocalDateTime.now())
            .firstYearOfVerifiedEmissions(2020)
            .lastYearOfVerifiedEmissions(2021)
            .currentYear(2021)
        .build());
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Compliant Entity Initialization event when FYVE < Current < LYVE or LYVE undefined")
    void testComplianceCalculationAccountCreationAmendment3() {
        var result = dynamicComplianceService.processEvent(CompliantEntityInitializationEvent
            .builder()
            .eventId(UUID.randomUUID())
            .compliantEntityId(1000L)
            .dateTriggered(LocalDateTime.now())
            .firstYearOfVerifiedEmissions(2020)
            .lastYearOfVerifiedEmissions(2021)
            .currentYear(2021)
            .build());
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }


    @Test
    @DisplayName("Change year event")
    void testEveryFirstDayOfTheYearEvent() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2023, 2023, 2021));
        dynamicComplianceService.processEvent(changeYear(2022));
        var result = dynamicComplianceService.processEvent(changeYear(2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    /**
     * There is a case when the RA updates the First year of verified emissions and sets it in the past.
     * So upon approval of the Update installation / aircraft operator details if the First Year of
     * Verified emissions submission is updated, then the dynamic surrender status algorithm will be executed.
     */
    @Test
    @DisplayName("Update first year of verified submissions")
    void testUpdateFirstYearOFVerifiedEmissions() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2019));
        var result = dynamicComplianceService.processEvent(updateFirstYearOfVerifiedEmissionsEvent(UUID.randomUUID(), 
            2018));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    /**
     * There is a case when the RA updates the Last year of verified emissions.
     * So upon approval of the Update installation / aircraft operator details
     * if the Last Year of Verified emissions submission is updated,
     * then the dynamic surrender status algorithm will be executed.
     */
    @Test
    @DisplayName("Update last year of verified submissions and surrender")
    void testUpdateLastYearOFVerifiedEmissions() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2011, null, 2011));
        dynamicComplianceService.processEvent(changeYear(2012));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2011, 200L));
        dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(2015));
        dynamicComplianceService.processEvent(changeYear(2013));
        dynamicComplianceService.processEvent(changeYear(2014));
        dynamicComplianceService.processEvent(changeYear(2015));
        dynamicComplianceService.processEvent(changeYear(2016));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2012, 200L));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2013, 200L));
        dynamicComplianceService.processEvent(exclusionEvent(2014));
        dynamicComplianceService.processEvent(exclusionEvent(2015));
        dynamicComplianceService.processEvent(exclusionReversalEvent(2014));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2014, 200L));
        var result = dynamicComplianceService.processEvent(surrenderEvent(2000L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }


    /**
     * Upon emissions upload for an (aircraft) operator for a year, the surrender status will be recalculated.
     * Any emission amount will trigger the recalculation apart from the cases when the same emission amount is uploaded;
     * in such cases the surrender status will not be updated.
     */
    @Test
    @DisplayName("Test emissions Reported through excel file, surrender and surrender reversal")
    void testReportEmissionsWithExcelFile() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2018, null, 2019));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2018, 200L));
        dynamicComplianceService.processEvent(surrenderEvent(200L));
        var result = dynamicComplianceService.processEvent(surrenderReversalEvent(20L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }


    /**
     * Updating the exclusion status for a specific year for an (aircraft) operator,
     * will trigger the surrender status recalculation algorithm.
     */
    @Test
    @DisplayName("Test Yearly exclusions and reversals")
    void testExclusions() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2018, null, 2019));
        var result = dynamicComplianceService.processEvent(exclusionEvent(2018));
        assertEquals(ComplianceStatus.EXCLUDED, result.getState().getDynamicStatus());
    }

    /**
     * Indicates the verified emissions for this installation/aircraft operator for the selected year
     * It is a positive integer or 0 - decimals cannot be accepted in the emissions table
     * It might be 0 for a year (zero emissions)
     * It might be empty for a year, if emissions are not expected to be reported for an account.
     * Reporting empty emissions for a year corresponds to not report at all for a year.
     * In principle, RAs will need to upload empty emissions for a year for an account in order to retrospectively correct a wrong emission submitted.
     */
    @Test
    @DisplayName("Test uploading emissions with null and empty values ")
    void testUploadingEmissionsWithZeroNullValues() {

        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2018, null, 2019));
        var result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2018, 200L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2018, null));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }
    

    @Test
    @DisplayName("Last Year Status lyStatus should serialize correctly.")
    void testSerializationLastYearStatus() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 200L));
        dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(2021));
        var result = dynamicComplianceService.processEvent(surrenderEvent(2000L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        assertEquals(ComplianceStatus.A, result.getState().getLyStatus());
        String serializedDynamicCompliance = dynamicComplianceService.serializeDynamicCompliance(result);
        DynamicCompliance deserializedDynamicCompliance = dynamicComplianceService.deserializeToDynamicCompliance(serializedDynamicCompliance);
        assertEquals(ComplianceStatus.A, deserializedDynamicCompliance.getState().getLyStatus());
    }

    /**
     * This test contains the flow that was resulting to status EXCLUDED,\ before
     * fixing the update event FYVE/LYVE logic for ReportingPeriod endYear.
     */
    @Test
    @DisplayName("Update First Year of Verified Emissions updates Reporting Period.")
    void testUpdateFYVEReportingPeriod() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        dynamicComplianceService.processEvent(updateFirstYearOfVerifiedEmissionsEvent(2020, LocalDateTime.now()));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2020, 2L));
        dynamicComplianceService.processEvent(surrenderEvent(2L));
        var result = dynamicComplianceService.processEvent(exclusionEvent(2021));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        assertEquals(2020, result.getState().getReportingPeriod().getEndYear());
        assertEquals(2020, result.getState().getReportingPeriod().getStartYear());
    }

    @Test
    @DisplayName("Update Last Year of Verified Emissions updates Reporting Period.")
    void testUpdateLYVEReportingPeriod() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2025, 2021));
        var result = dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(null));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        assertEquals(2021, result.getState().getReportingPeriod().getEndYear());
        assertEquals(2021, result.getState().getReportingPeriod().getStartYear());
    }

    /**
     * Excluded status is returned only when all years are excluded.
     */
    @Test
    @DisplayName("Status excluded is expected only when every year is excluded")
    void testAllYearExcluded() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2011, null, 2011));
        dynamicComplianceService.processEvent(changeYear(2012));
        dynamicComplianceService.processEvent(exclusionEvent(2011));
        dynamicComplianceService.processEvent(exclusionEvent(2012));
        DynamicCompliance result = dynamicComplianceService.processEvent(changeYear(2013));
        assertEquals(ComplianceStatus.EXCLUDED, result.getState().getDynamicStatus());
    }
    
    @Test
    @DisplayName("UKETS-7052 Status A is expected when only last year is excluded and last status was A")
    void testLastYearExcludedWithStatus_A() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        result = dynamicComplianceService.processEvent(changeYear(2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(exclusionEvent(2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }
    
    @Test
    @DisplayName("UKETS-7052 Status B is expected when only last year is excluded and last status was B")
    void testLastYearExcludedWithStatus_B() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(surrenderEvent(50L));
        result = dynamicComplianceService.processEvent(changeYear(2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(exclusionEvent(2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }
    
    @Test
    @DisplayName("UKETS-7052 Status A is expected when only one year is excluded and last status was A")
    void testOneYearExcludedWithStatus_A() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        //2022
        result = dynamicComplianceService.processEvent(changeYear(2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        //Set 2022 as excluded
        result = dynamicComplianceService.processEvent(exclusionEvent(2022));
        //2023
        result = dynamicComplianceService.processEvent(changeYear(2022));
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("When LYVE is the same as current year")
    void testLastYearOfVerifiedEmissionsSameAsCurrentYear() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, 2023, 2021));
        dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        //2022
        result = dynamicComplianceService.processEvent(changeYear(2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        //Set 2022 as excluded
        dynamicComplianceService.processEvent(exclusionEvent(2022));
        //2023
        dynamicComplianceService.processEvent(changeYear(2022));
        result = dynamicComplianceService.processEvent(exclusionEvent(2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("When update of verified emissions for the current year")
    void testUpdateOfVerifiedEmissionsForCurrentYear() {
        int currentYear = Year.now().getValue();
        dynamicComplianceService
                .processEvent(compliantEntityInitializationEvent(2021, null, currentYear));

        //Set emissions for current year
        assertDoesNotThrow(() ->
                dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(currentYear, 100L)));
    }

    @Test
    @DisplayName("When update of verified emissions for the future year")
    void testUpdateOfVerifiedEmissionsForFutureYear() {
        int currentYear = Year.now().getValue();
        dynamicComplianceService
                .processEvent(compliantEntityInitializationEvent(2021, null, currentYear));

        //Set emissions for future year
        assertThrows(DynamicComplianceException.class, () ->
                dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(currentYear + 1, 100L)));
    }
}
