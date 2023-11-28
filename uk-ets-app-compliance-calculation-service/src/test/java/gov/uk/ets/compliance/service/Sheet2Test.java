package gov.uk.ets.compliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.compliance.domain.ComplianceStatus;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;
import gov.uk.ets.compliance.utils.DynamicComplianceServiceTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("CurrentYear=FYVE && LYVE is null")
public class Sheet2Test extends DynamicComplianceServiceTestBase {

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
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(2021));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 2")
    void testScenario2() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear());
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderReversalEvent(50L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 3")
    void testScenario3() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(2025));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear());
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

    @Test
    @DisplayName("Scenario 4")
    void testScenario4() {
        var result = dynamicComplianceService.processEvent(compliantEntityInitializationEvent(2021, null, 2021));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2021, 100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateLastYearOfVerifiedEmissionsEvent(2025));
        assertEquals(ComplianceStatus.NOT_APPLICABLE, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear());
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(changeYear());
        assertEquals(ComplianceStatus.C, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(updateOfVerifiedEmissionsEvent(2022, 100L));
        assertEquals(ComplianceStatus.B, result.getState().getDynamicStatus());
        result = dynamicComplianceService.processEvent(surrenderEvent(100L));
        assertEquals(ComplianceStatus.A, result.getState().getDynamicStatus());
    }

}
