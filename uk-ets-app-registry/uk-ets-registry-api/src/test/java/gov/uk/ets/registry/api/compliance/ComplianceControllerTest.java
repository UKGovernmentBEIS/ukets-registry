package gov.uk.ets.registry.api.compliance;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.compliance.web.model.OperatorEmissionsExclusionStatusChangeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


public class ComplianceControllerTest {

    private MockMvc mockMvc;

    private ComplianceController complianceController;

    @Mock
    private ComplianceService complianceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        complianceController =
            new ComplianceController(complianceService);
        mockMvc = MockMvcBuilders.standaloneSetup(complianceController)
                .build();
    }
    
    @Test
    @DisplayName("Update exclusion status for the account/operator, expected to pass")
    void updateAccountExclusionStatus() throws Exception {

        OperatorEmissionsExclusionStatusChangeDTO request = new OperatorEmissionsExclusionStatusChangeDTO();
        request.setExcluded(true);
        request.setYear(2021L);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(request));
        String template = "/api-registry/compliance.update.exclusion-status";

        mockMvc.perform(patch(template)
            .param("accountIdentifier", "1000")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("GET VerifiedEmissions for the operator, expected to pass")
    void getVerifiedEmissions() throws Exception {

        String template = "/api-registry/compliance.get.emissions";

        mockMvc.perform(get(template)
            .param("compliantEntityId", "1000")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("GET ComplianceOverview for the operator, expected to pass")
    void getComplianceOverview() throws Exception {

        String template = "/api-registry/compliance.get.overview";

        mockMvc.perform(get(template)
            .param("accountIdentifier", "1000")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @DisplayName("GET Compliance Status History for the operator, expected to pass")
    void getComplianceStatusHistory() throws Exception {

        String template = "/api-registry/compliance.get.status.history";

        mockMvc.perform(get(template)
            .param("compliantEntityId", "1000")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("GET Compliance Events History for the operator, expected to pass")
    void getComplianceEventsHistory() throws Exception {

        String template = "/api-registry/compliance.get.events.history";

        mockMvc.perform(get(template)
            .param("compliantEntityId", "1000")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    
}
