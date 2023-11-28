package gov.uk.ets.registry.api.business.configuration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.Map;

class BusinessConfigurationServiceTest {

    MockEnvironment env = new MockEnvironment();

    BusinessConfigurationService businessConfigurationService;

    @BeforeEach
    public void setup() {
        env.setProperty("business.property.transaction.allocation-year", "2021");
        env.setProperty("google.tracking.id", "UA-189174351-1");
        businessConfigurationService = new BusinessConfigurationServiceImpl(env);
    }

    @Test
    void test_application_properties_retrieval_success() throws Exception {
        Map<String, String> applicationProperties = businessConfigurationService.getApplicationProperties();

        assertTrue(applicationProperties != null && !applicationProperties.isEmpty());
        assertEquals("2021", applicationProperties.get("business.property.transaction.allocation-year"));
        assertEquals("UA-189174351-1", applicationProperties.get("google.tracking.id"));
    }
}
