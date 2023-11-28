package gov.uk.ets.registry.api.ar;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class AuthorizedRepresentativeControllerTest {

    private MockMvc mockMvc;

    private AuthorizedRepresentativeController controller;

    @Mock
    private AuthorizedRepresentativeService authorizedRepresentativeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new AuthorizedRepresentativeController(authorizedRepresentativeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test()
    @DisplayName("Test retrieval of authorise representatives by user. ")
    void getAuthoriseRepresentativesForAccountByUser() throws Exception {
        String urid = "UK123456";
        List<AuthorisedRepresentativeDTO> results = new ArrayList<>();
        AuthorisedRepresentativeDTO dto1 = new AuthorisedRepresentativeDTO();
        dto1.setUrid(urid);
        dto1.setAccountHolderName("Account Holder Name 1");
        dto1.setAccountName("Account Name 1");
        dto1.setState(AccountAccessState.ACTIVE);
        dto1.setRight(AccountAccessRight.APPROVE);
        dto1.setAccountIdentifier(10001L);
        results.add(dto1);
        Mockito.when(authorizedRepresentativeService.getARsByUser(urid)).thenReturn(results);
        mockMvc.perform(get("/api-registry/authorised-representatives.get.by-user/"+urid)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].right", is(AccountAccessRight.APPROVE.name())))
            .andExpect(jsonPath("$.[0].urid", is(urid)))
            .andExpect(jsonPath("$.[0].accountName", is("Account Name 1")))
            .andExpect(jsonPath("$.[0].accountHolderName", is("Account Holder Name 1")))
            .andExpect(jsonPath("$.[0].state", is(AccountAccessState.ACTIVE.name())));

    }
}
