package uk.gov.ets.registration.user.configuration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ets.registration.user.UserRegistrationController;
import uk.gov.ets.registration.user.configuration.domain.ApplicationPropertyEnum;
import uk.gov.ets.registration.user.configuration.service.BusinessConfigurationService;

class BusinessConfigurationControllerTest {

    @Value("${user.registration.keycloak.verifyEmailRedirectUrl}")
    private String verifyEmailRedirectUrl;

    private MockMvc mockMvc;

    private UserRegistrationController controller;

    @Mock
    private BusinessConfigurationService businessConfigurationService;

    private static final String SEARCH_URL = "/api-registration/configuration";

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        System.out.print(verifyEmailRedirectUrl);
        controller = new UserRegistrationController(businessConfigurationService);
        mockMvc =
            MockMvcBuilders.standaloneSetup(controller)
                .addPlaceholderValue("user.registration.keycloak.verifyEmailRedirectUrl", "https://localhost:4200")
                .build();
    }

    @Test
    void test_specific_application_property_retrieval_success() throws Exception {
        Map<String, String> dto = new HashMap<>();
        dto.put(ApplicationPropertyEnum.REGISTRATION_KEYCLOAK_EMAIL_FROM.getKey(), "no-reply@ukets.net");
        Mockito.when(businessConfigurationService
            .getApplicationPropertyByKey(ApplicationPropertyEnum.REGISTRATION_KEYCLOAK_EMAIL_FROM.getKey()))
            .thenReturn(dto);

        MvcResult mvcResult = mockMvc
            .perform(get(SEARCH_URL).param("key", ApplicationPropertyEnum.REGISTRATION_KEYCLOAK_EMAIL_FROM.getKey())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        assert mvcResult.getResponse().getContentAsString()
            .contains(dto.get(ApplicationPropertyEnum.REGISTRATION_KEYCLOAK_EMAIL_FROM.getKey()));
    }

    @Test
    void test_application_properties_retrieval_success() throws Exception {
        Map<String, String> dto = new HashMap<>();
        dto.put(ApplicationPropertyEnum.REGISTRATION_KEYCLOAK_EMAIL_FROM.getKey(), "no-reply@ukets.net");
        Mockito.when(businessConfigurationService.getApplicationProperties()).thenReturn(dto);

        MvcResult mvcResult = mockMvc.perform(get(SEARCH_URL)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        assert mvcResult.getResponse().getContentAsString()
            .contains(dto.get(ApplicationPropertyEnum.REGISTRATION_KEYCLOAK_EMAIL_FROM.getKey()));
    }
}
