package gov.uk.ets.registry.api.tal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.commons.logging.Config;
import gov.uk.ets.registry.api.account.service.AccountOperatorUpdateService;
import gov.uk.ets.registry.api.account.validation.AccountValidator;
import gov.uk.ets.registry.api.authz.AuthorizationServiceImpl;
import gov.uk.ets.registry.api.tal.service.TrustedAccountListService;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.transaction.web.mapper.TransactionSearchResultMapper;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TrustedAccountListController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class TrustedAccountListControllerTest {

    @MockBean
    private BuildProperties buildProperties;

    @MockBean
    private Config config;

    @Autowired
    private MockMvc mockMvc;

    private TrustedAccountListController controller;

    @MockBean
    private TrustedAccountListService accountService;

    @MockBean
    private AuthorizationServiceImpl authorizationService;

    @MockBean
    private AccountValidator accountValidator;

    @MockBean
    private AccountOperatorUpdateService accountOperatorUpdateService;

    @MockBean
    private TransactionSearchResultMapper transactionSearchResultMapper;

    @BeforeEach
    public void setup() {
        controller = new TrustedAccountListController(accountService);
    }

    @Test
    @DisplayName("Update Trusted Account with empty description, expected to fail")
    void testUpdateTrustedAccount_MissingDescription() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/tal.update";

        TrustedAccountDTO request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(null)
                .build();

        mockMvc.perform(post(template)
                .param("accountId", "1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description("")
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Update Trusted Account with description length not between [3,256], expected to fail")
    void testUpdateTrustedAccount_BadDescriptionLength() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/tal.update";

        TrustedAccountDTO request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(RandomStringUtils.random(1))
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(RandomStringUtils.random(257))
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update Trusted Account with description length between [3,256], expected to pass")
    void testUpdateTrustedAccount_GoodDescriptionLength() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/tal.update";

        TrustedAccountDTO request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(RandomStringUtils.random(3))
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(RandomStringUtils.random(4))
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(RandomStringUtils.random(255))
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(RandomStringUtils.random(256))
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add Trusted Account with empty description, expected to fail")
    void testAddTrustedAccount_MissingDescription() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/tal.add";

        TrustedAccountDTO request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(null)
                .build();

        //Execute a valid request
        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description("")
                .build();

        //Execute a valid request
        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add Trusted Account with description length between [3,256], expected to pass")
    void testAddTrustedAccount_GoodDescriptionLength() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/tal.add";

        TrustedAccountDTO request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(null)
                .build();

        //Execute a valid request
        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description("")
                .build();

        //Execute a valid request
        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add Trusted Account with description length not between [3,256], expected to fail")
    void testAddTrustedAccount_BadDescriptionLength() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        String template = "/api-registry/tal.add";

        TrustedAccountDTO request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(RandomStringUtils.random(1))
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request = TrustedAccountDTO
                .builder()
                .accountFullIdentifier(UUID.randomUUID().toString())
                .description(RandomStringUtils.random(257))
                .build();

        mockMvc.perform(post(template)
                        .param("accountId", "1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
