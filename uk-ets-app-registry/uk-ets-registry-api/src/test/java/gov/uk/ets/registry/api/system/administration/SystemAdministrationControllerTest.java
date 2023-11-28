package gov.uk.ets.registry.api.system.administration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.uk.ets.registry.api.system.administration.service.SystemAdministrationService;
import gov.uk.ets.registry.api.system.administration.web.SystemAdminActionExceptionControllerAdvice;


public class SystemAdministrationControllerTest {
    
    @Mock
    private SystemAdministrationService systemAdministrationService;
    
    private MockMvc mockMvc;

    private SystemAdministrationController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new SystemAdministrationController(systemAdministrationService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new SystemAdminActionExceptionControllerAdvice()).build();
    }
    
    @Test
    public void reset() throws Exception {
        mockMvc.perform(post("/api-registry/system-administration.reset")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
