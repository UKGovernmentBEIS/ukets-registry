package gov.uk.ets.registry.api.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.authz.DisabledKeycloakAuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.test.RegistryIntegrationTest;
import gov.uk.ets.registry.api.common.view.RequestDTO;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.UserGeneratorService;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import gov.uk.ets.registry.api.user.service.UserService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RegistryIntegrationTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "keycloak.enabled=false"
})
@Log4j2
class AccountControllerIntegrationTest {


    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SpyBean
    private DisabledKeycloakAuthorizationService disabledKeycloakAuthorizationService;

    @SpyBean
    private DisabledKeycloakUserAdministrationService disabledKeycloakUserAdministrationService;

    private final String accountOpeningTestDataClassPath = "classpath:/test-data/account-opening/";

    @BeforeAll
    void setup() {
//        jdbcTemplate.execute("Delete from installation_ownership;");
//        jdbcTemplate.execute("Delete from installation;");
//        jdbcTemplate.execute("Delete from task;");
    }


    @DisplayName("Propose An installation transfer on an existing installation")
    @Test
    @Disabled("issues in integration server")
    void testProposeInstallationTransfer() throws Exception {


        //given
        // the following code part sets a keycloak id for the current user
        AccessToken accessToken = new AccessToken();

        String currentUserKeycloakId = "fc4c4b91-efd8-4cc9-a96e-4efdae59b4ba";
        accessToken.setSubject(currentUserKeycloakId);
        Mockito.when(disabledKeycloakAuthorizationService.getToken()).thenReturn(accessToken);

        // create two users one is the task initiator and one is the task claimant and store them in the database
        UserDTO taskInitiatorDTO = new UserDTO();
        taskInitiatorDTO.setFirstName("Nick");
        taskInitiatorDTO.setLastName("Dorsey");
        String initiatorUrid = new UserGeneratorService().generateURID();
        taskInitiatorDTO.setUrid(initiatorUrid);
        String initiatorKeykloakId = "fc4c4b91-efd8-4cc9-a96e-4efdae59b4ba";
        taskInitiatorDTO.setKeycloakId(initiatorKeykloakId);
        userService.registerUser(taskInitiatorDTO);

//
//        // Set role for the current user, this needs to be the senior-registry-administrator in order to be able to approve the task
        RoleRepresentation admin =
            new RoleRepresentation("authorized-representative", "authorized representative", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));


        AccountDTO initialInstallationAccount = readAccountFromJson("installation.json");


        AccountDTO existingInstallationAccount = accountService.openAccount(initialInstallationAccount);


        AccountDTO installatationTranfer = readAccountFromJson("installation-transfer.json");
        installatationTranfer.getInstallationToBeTransferred()
            .setIdentifier(existingInstallationAccount.getOperator().getIdentifier());
        String installationTransferAsJson = new ObjectMapper().writeValueAsString(installatationTranfer);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/accounts.propose")
            .content(installationTransferAsJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();
        //then
        //verify new account name
        // verify new account status
        //verfiry holder
        ///verify installation transfer onwnership
        //verify task created
        String response = mvcResult.getResponse().getContentAsString();
        log.error(response);

        RequestDTO requestDTO = new ObjectMapper().readValue(response, RequestDTO.class);
        Task createdTask = entityManager
            .createQuery(
                "SELECT t FROM Task t left join fetch t.account left join fetch t.initiatedBy left join fetch t.claimedBy where t.requestId in (:requestId)",
                Task.class)
            .setParameter("requestId", requestDTO.getRequestId())
            .getSingleResult();


        assertEquals(RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST, createdTask.getType());
        assertNull(createdTask.getAccount());
        assertEquals(taskInitiatorDTO.getUrid(), createdTask.getInitiatedBy().getUrid());

    }

    private AccountDTO readAccountFromJson(String jsonFile) throws IOException {
        Resource resource = resourceLoader.getResource(accountOpeningTestDataClassPath + jsonFile);
        String installationAccount = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8.name());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AccountDTO accountDTO = objectMapper.readValue(installationAccount, AccountDTO.class);
        return accountDTO;
    }

}

