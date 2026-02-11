package gov.uk.ets.compliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.uk.ets.compliance.utils.DynamicComplianceServiceTestBase;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import gov.uk.ets.compliance.domain.ComplianceStatus;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("EA Provided Compliance Tests")
class EAComplianceTests extends DynamicComplianceServiceTestBase {

    @Autowired
    DynamicComplianceService dynamicComplianceService;
    @Autowired
    StaticComplianceService staticComplianceService;
    @Autowired
    DynamicComplianceRepository dynamicComplianceRepository;

    private final LocalDateTime beginOf2021 = LocalDateTime.of(2021,1,1,0,0);
    private final LocalDateTime beginOf2022 = LocalDateTime.of(2022,1,1,0,0);
    private final LocalDateTime beginOf2023 = LocalDateTime.of(2023,1,1,0,0);
    private final LocalDateTime beginOf2024 = LocalDateTime.of(2024,1,1,0,0);
    private final LocalDateTime beginOf2025 = LocalDateTime.of(2025,1,1,0,0);
    private final LocalDateTime beginOf2026 = LocalDateTime.of(2026,1,1,0,0);

    private final LocalDateTime tenOfFebruary2021 = LocalDateTime.of(2021,2,10,0,0);
    private final LocalDateTime tenOfFebruary2022 = LocalDateTime.of(2022,2,10,0,0);
    private final LocalDateTime tenOfFebruary2023 = LocalDateTime.of(2023,2,10,0,0);
    private final LocalDateTime firstOfMarch2023 = LocalDateTime.of(2023,3,1,0,0);
    private final LocalDateTime fifteenOfMarch2023 = LocalDateTime.of(2023,3,15,0,0);
    private final LocalDateTime fifteenOfMarch2024 = LocalDateTime.of(2024,3,15,0,0);

    private final LocalDateTime firstOfApril2021 = LocalDateTime.of(2021,4,1,0,0);
    private final LocalDateTime firstOfApril2022 = LocalDateTime.of(2022,4,1,0,0);
    private final LocalDateTime firstOfApril2023 = LocalDateTime.of(2023,4,1,0,0);
    private final LocalDateTime firstOfApril2024 = LocalDateTime.of(2024,4,1,0,0);
    private final LocalDateTime SecondOfApril2021 = LocalDateTime.of(2021,4,2,0,0);
    private final LocalDateTime thirdOfApril2022 = LocalDateTime.of(2022,4,3,0,0);
    private final LocalDateTime tenOfApril2022 = LocalDateTime.of(2022,4,10,0,0);

    private final LocalDateTime thirteenOfApril2021 = LocalDateTime.of(2021,4,13,0,0);
    private final LocalDateTime thirteenOfApril2022 = LocalDateTime.of(2022,4,13,0,0);
    private final LocalDateTime thirteenOfApril2023 = LocalDateTime.of(2023,4,13,0,0);
    private final LocalDateTime thirteenOfApril2024 = LocalDateTime.of(2024,4,13,0,0);
    private final LocalDateTime fourteenOfApril2023 = LocalDateTime.of(2023,4,14,0,0);
    private final LocalDateTime fourteenOfApril2024 = LocalDateTime.of(2024,4,14,0,0);

    private final LocalDateTime fifteenOfApril2022 = LocalDateTime.of(2022,4,15,0,0);
    private final LocalDateTime fifteenOfApril2024 = LocalDateTime.of(2024,4,15,0,0);

    private final LocalDateTime sixteenOfApril2022 = LocalDateTime.of(2022,4,16,0,0);
    private final LocalDateTime seventeenOfApril2022 = LocalDateTime.of(2022,4,16,0,0);
    private final LocalDateTime twentyOfApril2024 = LocalDateTime.of(2024,4,17,0,0);
    private final LocalDateTime twentySecondOfApril2022 = LocalDateTime.of(2022,4,22,0,0);
    private final LocalDateTime twentyEightOfApril2022 = LocalDateTime.of(2022,4,28,0,0);

    private final LocalDateTime firstOfMay2022 = LocalDateTime.of(2022,5,1,0,0);
    private final LocalDateTime firstOfMay2023 = LocalDateTime.of(2023,5,1,0,0);
    private final LocalDateTime firstOfMay2024 = LocalDateTime.of(2024,5,1,0,0);
    private final LocalDateTime firstOfMay2025 = LocalDateTime.of(2025,5,1,0,0);
    private final LocalDateTime thirteenOfMay2024 = LocalDateTime.of(2024,5,13,0,0);
    private final LocalDateTime firstOfJune2023 = LocalDateTime.of(2023,6,1,0,0);
    private final LocalDateTime firstOfJune2024 = LocalDateTime.of(2024,6,1,0,0);
    private final LocalDateTime secondOfJune2024 = LocalDateTime.of(2024,6,2,0,0);
    private final LocalDateTime fifthOfJune2023 = LocalDateTime.of(2023,6,5,0,0);
    private final LocalDateTime tenOfJune2022 = LocalDateTime.of(2022,6,10,0,0);

    private final LocalDateTime thirteenOfJune2023 = LocalDateTime.of(2023,6,13,0,0);
    private final LocalDateTime thirdOfAugust2022 = LocalDateTime.of(2022,8,3,0,0);
    private final LocalDateTime fifteenOfAugust2023 = LocalDateTime.of(2022,8,15,0,0);
    private final LocalDateTime thirteenOfOctober2023 = LocalDateTime.of(2023,10,13,0,0);
    private final LocalDateTime fifteenOfOctober2022 = LocalDateTime.of(2022,10,15,0,0);
    private final LocalDateTime elevenOfDecember2022 = LocalDateTime.of(2022,12,11,0,0);
    private final LocalDateTime seventeenOfDecember2022 = LocalDateTime.of(2022,12,17,0,0);


    @BeforeEach
    public void setup() {
        dynamicComplianceRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Scenario 1 - Happy Path - enters emissions and surrenders by the required deadlines")
    void testScenario1() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(2)
    @DisplayName("Scenario 2 - Happy Path - enters emissions and surrenders by the required deadlines for 2 years")
    void testScenario2() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(3)
    @DisplayName("Scenario 3 - Insufficient surrender of allowances against cumulative emissions")
    void testScenario3() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(50L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Test
    @Order(4)
    @DisplayName("Scenario 4 - Over surrender of allowances against cumulative emissions")
    void testScenario4() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(5)
    @DisplayName("Scenario 5 - Sufficient surrender of allowances, no emissions uploaded")
    void testScenario5() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    @Test
    @Order(6)
    @DisplayName("Scenario 6 - year 2022 emissions amended (increase)")
    void testScenario6() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 101L, fifteenOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(1L, twentySecondOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(7)
    @DisplayName("Scenario 7 - year 2021 emissions amended (decrease)")
    void testScenario7() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(50L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 50L, fifteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(8)
    @DisplayName("Scenario 8 - year 2022 emissions amended (increase) during 2023 reporting period")
    void testScenario8() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 200L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // Year 2022 emissions amended (increase) during 2023 reporting period
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 155L, fifteenOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(5L, twentyOfApril2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(9)
    @DisplayName("Scenario 9 - year 2021 not compliant, year 2022 made up deficit")
    void testScenario9() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(50L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        // Year 2021 not compliant, year 2022 made up deficit
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(10)
    @DisplayName("Scenario 10 - year 2022 compliant, year 2023 excluded, year 2024 compliant")
    void testScenario10() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2022));
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        // Update exclusion
        result = dynamicComplianceService.processEvent(exclusionEvent(2022, tenOfFebruary2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        staticComplianceState = staticComplianceService.processEvent(staticComplianceRequestEvent(firstOfMay2023));
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 200L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
    }

    @Test
    @Order(11)
    @DisplayName("Scenario 11 - year 2021 compliant, year 2022 non compliant, year 2023 non compliant")
    void testScenario11() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 500L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(500L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 500L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(450L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 500L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(500L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Test
    @Order(12)
    @DisplayName("Scenario 12 - LYVE updated to current reporting period")
    void testScenario12() {
        // 2022
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2022, beginOf2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 500L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(500L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // Update LYVE to 2022
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2022, fifteenOfApril2022));

        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 450L, sixteenOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2022));
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(450L, thirdOfAugust2022));
        assertNotNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertNull(result.getState().getLyStatus());
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2023));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
    }

    @Test
    @Order(13)
    @DisplayName("Scenario 13 - Surrender reversed before surrender deadline")
    void testScenario13() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 75L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirdOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        // Reversal of surrender
        result = dynamicComplianceService
            .processEvent(surrenderReversalEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(75L, seventeenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(14)
    @DisplayName("Scenario 14 - Year 2021 non-compliant, year 2022 excluded")
    void testScenario14() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(50L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(exclusionEvent(2022, tenOfFebruary2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 200L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());

    }

    @Disabled("Requires revisit as the roll-over has been removed")
    @Test
    @Order(15)
    @DisplayName("Scenario 15 - (exclusion status roll-over)")
    void testScenario15() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(exclusionEvent(2023, fourteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2025
        result = dynamicComplianceService.processEvent(changeYear(beginOf2025,2025));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        result = dynamicComplianceService.processEvent(exclusionReversalEvent(2025));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2026
        result = dynamicComplianceService.processEvent(changeYear(beginOf2026,2026));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    @Test
    @Order(16)
    @DisplayName("Edge case 1 - FYVE 2021, LYVE 2023 (cessation prior to 1st May of same reporting year)")
    void edgeCase1() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        assertNull(result.getState().getLyStatus());// LYVE null
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2022));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        assertNull(result.getState().getLyStatus());// LYVE null
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2023));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2024, fifteenOfMarch2024));// LYVE==Current
                                                                        // Year
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        assertNotNull(result.getState().getLyStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 200L, firstOfApril2024));
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2024, 50L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertNotNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(50L, thirteenOfMay2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2025
        result = dynamicComplianceService.processEvent(changeYear(beginOf2025,2025));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        assertNull(result.getState().getLyStatus());// LYVE<Current Year
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2025));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
    }

    @Test
    @Order(17)
    @DisplayName("Edge case 2 - FYVE amended to 2021")
    void edgeCase2() {
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2022, null, 2023, beginOf2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        assertNull(result.getState().getLyStatus());// LYVE is null
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2023));
        assertNull(staticComplianceState.getLyStatus());// LYVE is null
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 175L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(175L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        // FYVE amended to 2021
        result = dynamicComplianceService
            .processEvent(updateFirstYearOfVerifiedEmissionsEvent(2021, fifteenOfMarch2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 30L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(30L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        assertNull(result.getState().getLyStatus());// LYVE is null
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertNull(staticComplianceState.getLyStatus());// LYVE is null
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
    }

    @Test
    @Order(18)
    @DisplayName("Edge Case 3 - FYVE is same as LYVE")
    void edgeCase3() {
        // 2023
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2023, null, 2023, beginOf2023));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2023, fifteenOfMarch2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        assertNull(result.getState().getLyStatus());// LYVE is null
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertNull(staticComplianceState.getLyStatus());// LYVE is null
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
    }

    @Test
    @Order(19)
    @DisplayName("Edge case 4 - FYVE 2021, LYVE 2023 (cessation prior to 1st May of same reporting year), account closure during the LYVE prior to 01/05/LYVE")
    void edgeCase4() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 100L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2024, fifteenOfMarch2024));// LYVE=Current
                                                                        // Year
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 200L, firstOfApril2024));
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2024, 50L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());

        // 2025
        result = dynamicComplianceService.processEvent(changeYear(beginOf2025,2025));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Test
    @Order(20)
    @DisplayName("Edge case 5 - FYVE 2021, LYVE 2023 (cessation prior to 1st May of same reporting year), account closure during the LYVE prior to 01/05/LYVE")
    void edgeCase5() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 100L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2024, fifteenOfMarch2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 200L, firstOfApril2024));
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2024, 50L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(50L, thirteenOfMay2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2025
        result = dynamicComplianceService.processEvent(changeYear(beginOf2025,2025));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @Order(21)
    @DisplayName("Edge Case 6 - FYVE is same as LYVE and account is created before 1/5")
    void edgeCase6() {
        // 2023
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2023, 2023, 2023, firstOfMarch2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfJune2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertEquals(ComplianceStatus.A,
            staticComplianceState.getDynamicStatus());
    }

    @Test
    @Order(22)
    @DisplayName("Edge Case 7 - FYVE is same as LYVE and account created after 1/5")
    void edgeCase7() {
        // 2023
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2023, 2023, 2023, firstOfJune2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 150L,fifthOfJune2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfJune2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertEquals(ComplianceStatus.A,
            staticComplianceState.getDynamicStatus());
    }

    @Test
    @Order(23)
    @DisplayName("Edge Case 8 - FYVE is same as LYVE")
    void edgeCase8() {
        // 2023
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2023, null, 2023, firstOfMarch2023));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2023, fifteenOfAugust2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 150L, fifteenOfAugust2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfOctober2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertEquals(ComplianceStatus.A,
            staticComplianceState.getDynamicStatus());
    }

    @Test
    @Order(24)
    @DisplayName("Edge case 9 - FYVE 2021, LYVE 2023 (cessation prior to 1st May of same reporting year), LYVE update to a later year before 1st of May")
    void edgeCase9() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2022));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2023));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2024, fifteenOfMarch2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 200L, firstOfApril2024));
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2024, 50L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2024, null, firstOfApril2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2025, fourteenOfApril2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2025
        result = dynamicComplianceService.processEvent(changeYear(beginOf2025,2025));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2025));
        assertNotNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.C, staticComplianceState.getStatus());
    }

    @Test
    @Order(25)
    @DisplayName("Edge case 10 - FYVE 2021, LYVE 2023 (cessation prior to 1st May of same reporting year), LYVE update to a later year after 1st of May")
    void edgeCase10() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 100L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2022));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A,
            staticComplianceState.getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 150L, firstOfApril2023));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(150L, thirteenOfApril2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2023));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A,
            staticComplianceState.getDynamicStatus());

        // 2024
        result = dynamicComplianceService.processEvent(changeYear(beginOf2024,2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2024, fifteenOfMarch2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2023, 200L, firstOfApril2024));
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2024, 50L, firstOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L, thirteenOfApril2024));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2024));
        assertNotNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2024, null, firstOfJune2024));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2025, secondOfJune2024));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2025
        result = dynamicComplianceService.processEvent(changeYear(beginOf2025,2025));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2025));
        assertNotNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.C, staticComplianceState.getStatus());
    }

    // Basic Cessation scenarios
    @Test
    @Order(26)
    @DisplayName("Basic Cessation 1 - Cessation received after 1 May (most frequent cessation scenario)")
    void basicCessation1() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 500L, firstOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(500L, thirteenOfApril2022));
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2022));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A,
            staticComplianceState.getDynamicStatus());
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2022, tenOfJune2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 100L, fifteenOfOctober2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L, seventeenOfDecember2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2023));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A,
            staticComplianceState.getDynamicStatus());
    }

    @Test
    @Order(27)
    @DisplayName("Basic Cessation 2 - Cessation received before 1 May & operator completes both years before 30 April")
    void basicCessation2() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2022, tenOfFebruary2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 500L, firstOfApril2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 50L, tenOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(550L, thirteenOfApril2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2022));
        assertNotNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2023));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A,
            staticComplianceState.getDynamicStatus());
    }

    @Test
    @Order(28)
    @DisplayName("Basic Cessation 3 - Cessation received before 1 May & operator completes surrenders after 1 May")
    void basicCessation3() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE,
            result.getState().getDynamicStatus());

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2022, tenOfFebruary2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 500L, firstOfApril2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(500L, thirdOfApril2022));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2022, 50L, twentyEightOfApril2022));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        var staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2022));
        assertNotNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(50L, elevenOfDecember2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());

        // 2023
        result = dynamicComplianceService.processEvent(changeYear(beginOf2023,2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        staticComplianceState = staticComplianceService.processEvent(
            staticComplianceRequestEvent(firstOfMay2023));
        assertNull(staticComplianceState.getLyStatus());
        assertEquals(ComplianceStatus.A, staticComplianceState.getStatus());
    }

    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //These tests are not part of the suite.
    /**
     * <ol>
     * <li>2021: Account creation with LYVE=2021</li>
     * <li>2021: 0 emissions upload and account closure</li>
     * <li>2022:new year roll over</li>
     * </ol>
     */
    @Test
    @Order(200)
    @DisplayName("Service Desk UKETSSD-128 issue.")
    void serviceDesk128() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, 2021, 2021, beginOf2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021, 0L, firstOfApril2021));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        // Close account does not have an event.

        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }
    
    /**
     * <ol>
     * <li>09 Aug 2021 -> Account open -> Compliance Status Not applicable</li>
     * <li>09 Aug 2021 -> FYVE 2021 -> Compliance Status Not applicable</li>
     * <li>10 Aug 2021 -> Surrender 13 -> Compliance Status Not applicable</li>
     * <li>29 Oct 2021 12:00 -> LYVE 2021 -> Compliance Status C</li>
     * <li>29 Oct 2021 13:00 -> Emissions upload 8 -> Compliance Status A</li>
     * <li>01 Jan 2022 00:00 -> New Year -> Compliance Status A</li>
     * <li>10 Jan 2022 -> LYVE NULL -> Compliance Status A</li>
     * </ol>
     */
    @Test
    @Order(210)
    @DisplayName("Bug UKETS-6473 issue.")
    void jira6473() {
        // 2021
        var result = dynamicComplianceService
            .processEvent(compliantEntityInitializationEvent(2021, null, 2021, beginOf2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        //Update FYVE
        result = dynamicComplianceService
            .processEvent(updateFirstYearOfVerifiedEmissionsEvent(2021, tenOfFebruary2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        //Surrender 13
        result = dynamicComplianceService
            .processEvent(surrenderEvent(13, firstOfApril2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        //Update LYVE
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(2021, SecondOfApril2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        //Emissions upload 8
        result = dynamicComplianceService
            .processEvent(updateOfVerifiedEmissionsEvent(2021,8L, thirteenOfApril2021));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        
        // 2022
        result = dynamicComplianceService.processEvent(changeYear(beginOf2022,2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        //Update LYVE
        result = dynamicComplianceService
            .processEvent(updateLastYearOfVerifiedEmissionsEvent(null, tenOfFebruary2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }
}
