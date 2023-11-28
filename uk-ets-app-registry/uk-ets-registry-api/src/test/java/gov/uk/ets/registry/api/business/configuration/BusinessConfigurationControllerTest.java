package gov.uk.ets.registry.api.business.configuration;

import gov.uk.ets.registry.api.business.configuration.domain.ApplicationPropertyEnum;
import gov.uk.ets.registry.api.business.configuration.service.BusinessConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BusinessConfigurationControllerTest {

    private MockMvc mockMvc;

    private BusinessConfigurationController controller;

    @Mock
    private BusinessConfigurationService businessConfigurationService;

    private static final String SEARCH_URL="/api-registry/configuration";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new BusinessConfigurationController(businessConfigurationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void test_specific_application_property_retrieval_success() throws Exception {
        Map<String,String> dto = new HashMap<>();
        dto.put(ApplicationPropertyEnum.MAX_NUMBER_OF_ARS.getKey(),"8");
        Mockito.when(businessConfigurationService.getApplicationPropertyByKey(ApplicationPropertyEnum.MAX_NUMBER_OF_ARS.getKey()))
                .thenReturn(dto);

        MvcResult mvcResult = mockMvc.perform(get(SEARCH_URL).param("key", ApplicationPropertyEnum.MAX_NUMBER_OF_ARS.getKey())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assert mvcResult.getResponse().getContentAsString().contains(dto.get(ApplicationPropertyEnum.MAX_NUMBER_OF_ARS.getKey()));
    }

    @Test
    public void test_application_properties_retrieval_success() throws Exception {
        Map<String,String> dto = new HashMap<>();
        dto.put(ApplicationPropertyEnum.MAX_NUMBER_OF_ARS.getKey(),"8");
        Mockito.when(businessConfigurationService.getApplicationProperties()).thenReturn(dto);

        MvcResult mvcResult = mockMvc.perform(get(SEARCH_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assert mvcResult.getResponse().getContentAsString().contains(dto.get(ApplicationPropertyEnum.MAX_NUMBER_OF_ARS.getKey()));
    }
}
