package uk.gov.ets.registration.user.configuration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class BusinessConfigurationServiceTest {

    MockEnvironment env = new MockEnvironment();

    BusinessConfigurationService businessConfigurationService;

    @BeforeEach
    public void setup() {
        env.setProperty("user.registration.keycloak.emailFrom", "no-reply@ukets.net");
        businessConfigurationService = new BusinessConfigurationServiceImpl(env);
    }

    @Test
    void test_application_properties_retrieval_success() throws Exception {
        Map<String, String> applicationProperties = businessConfigurationService.getApplicationProperties();

        assertTrue(applicationProperties != null && !applicationProperties.isEmpty());
        assertEquals("no-reply@ukets.net", applicationProperties.get("user.registration.keycloak.emailFrom"));
    }
}
