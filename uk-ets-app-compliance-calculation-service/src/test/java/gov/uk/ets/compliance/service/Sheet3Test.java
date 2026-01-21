package gov.uk.ets.compliance.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.uk.ets.compliance.domain.ComplianceStatus;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;
import gov.uk.ets.compliance.utils.DynamicComplianceServiceTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("FYVE=2021 && LYVE is null && CurrentYear=2024")
public class Sheet3Test extends DynamicComplianceServiceTestBase {

    @Autowired
    DynamicComplianceService dynamicComplianceService;

    @Autowired
    DynamicComplianceRepository dynamicComplianceRepository;

    @BeforeEach
    private void setup() {
        dynamicComplianceRepository.deleteAll();
    }

    @Test
    @DisplayName("Scenario 1")
    void testScenario1() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2022, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2023, 100L));
        result = dynamicComplianceService.processEvent(surrenderEvent(300L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 2")
    void testScenario2() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2022, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2023, 100L));
        result = dynamicComplianceService.processEvent(surrenderEvent(500L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 3")
    void testScenario3() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(exclusionEvent(2022));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2023, 100L));
        result = dynamicComplianceService.processEvent(surrenderEvent(200L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 4")
    void testScenario4() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(exclusionEvent(2022));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2023, 100L));
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 5")
    void testScenario5() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(exclusionEvent(2022));
        result = dynamicComplianceService.processEvent(exclusionEvent(2023));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(surrenderEvent(50L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 6")
    void testScenario6() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2022, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2023, 100L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 7")
    void testScenario7() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(exclusionEvent(2022));
        result = dynamicComplianceService.processEvent(exclusionEvent(2023));

        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 8")
    void testScenario8() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(exclusionEvent(2021));
        result = dynamicComplianceService.processEvent(exclusionEvent(2022));
        result = dynamicComplianceService.processEvent(exclusionEvent(2023));
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        result = dynamicComplianceService.processEvent(surrenderReversalEvent(100L));
        assertEquals(ComplianceStatus.EXCLUDED, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 9")
    void testScenario9() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, null));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2022, 100L));
        result = dynamicComplianceService.processEvent(exclusionEvent(2023));
        result = dynamicComplianceService.processEvent(surrenderReversalEvent(100L));
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
    }
    
    @Test
    @DisplayName("Scenario 10")
    void testScenario10() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2022, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2023, 100L));
        DynamicComplianceException dynamicComplianceException = assertThrows(DynamicComplianceException.class, () ->
            dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2024, 100L)));
        assertEquals("Event not used by compliance calculation engine. Ignoring event.", dynamicComplianceException.getMessage());
        result = dynamicComplianceService.processEvent(surrenderEvent(300L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }
    
    @Test
    @DisplayName("Scenario 11")
    void testScenario11() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2024));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2022, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2023, 100L));
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2024, 0L));
        result = dynamicComplianceService.processEvent(exclusionEvent(2024));
        result = dynamicComplianceService.processEvent(surrenderEvent(300L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }
    
    @Test
    @DisplayName("Scenario 12")
    void testScenario12() {
        //2021
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        
        //2022
        result = dynamicComplianceService.processEvent(changeYear());
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(2022));   
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2022, 100L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(200L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        
        //2023
        result = dynamicComplianceService.processEvent(changeYear());
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(2023));  
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2023, 0L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(exclusionEvent(2023));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }
}
