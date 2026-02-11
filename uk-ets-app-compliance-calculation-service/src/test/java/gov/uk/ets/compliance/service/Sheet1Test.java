package gov.uk.ets.compliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.compliance.domain.ComplianceStatus;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;
import gov.uk.ets.compliance.utils.DynamicComplianceServiceTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("CurrentYear=FYVE=LYVE")
public class Sheet1Test extends DynamicComplianceServiceTestBase {

    @Autowired
    DynamicComplianceService dynamicComplianceService;
    @Autowired
    DynamicComplianceRepository dynamicComplianceRepository;
    
    @BeforeEach
    private void setup() {
        dynamicComplianceRepository.deleteAll();
    }

    @Order(1)
    @Test
    @DisplayName("Scenario 1")
    void testScenario1() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        var result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Order(2)
    @Test
    @DisplayName("Scenario 2")
    void testScenario2() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        var result = dynamicComplianceService.processEvent(surrenderEvent(200L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Order(3)
    @Test
    @DisplayName("Scenario 3")
    void testScenario3() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        var result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Order(4)
    @Test
    @DisplayName("Scenario 4")
    void testScenario4() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        var result = dynamicComplianceService.processEvent(surrenderEvent(50L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Order(5)
    @Test
    @DisplayName("Scenario 5")
    void testScenario5() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        var result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    @Order(6)
    @Test
    @DisplayName("Scenario 6")
    void testScenario6() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        dynamicComplianceService.processEvent(surrenderEvent(100L));
        var result = dynamicComplianceService.processEvent(surrenderReversalEvent(100L));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    @Order(7)
    @Test
    @DisplayName("Scenario 7")
    void testScenario7() {
        dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, null));
        dynamicComplianceService.processEvent(surrenderEvent(100L));
        var result = dynamicComplianceService.processEvent(exclusionEvent(2021));
        assertEquals(ComplianceStatus.EXCLUDED, result.getState().getDynamicStatus());
    }

    @Order(8)
    @Test
    @DisplayName("Scenario 8")
    void testScenario8() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(50L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderReversalEvent(50L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Order(9)
    @Test
    @DisplayName("Scenario 9")
    void testScenario9() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 0L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(exclusionEvent(2021));
        assertEquals(ComplianceStatus.EXCLUDED, result.getState().getDynamicStatus());
    }

    @Order(10)
    @Test
    @DisplayName("Scenario 10")
    void testScenario10() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, null));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(exclusionEvent(2021));
        assertEquals(ComplianceStatus.EXCLUDED, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(exclusionReversalEvent(2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }

    @Order(11)
    @Test
    @DisplayName("Scenario 11")
    void testScenario11() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(2025));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear(2022));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Order(12)
    @Test
    @DisplayName("Scenario 12")
    void testScenario12() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, 2021, 2021));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderReversalEvent(100L));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 150L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }
}
