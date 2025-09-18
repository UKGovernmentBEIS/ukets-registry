package gov.uk.ets.registry.api.task;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import freemarker.template.Configuration;
import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.authz.DisabledKeycloakAuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.test.RegistryIntegrationTest;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.UserGeneratorService;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RegistryIntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "keycloak.enabled=false",
    "spring.datasource.hikari.auto-commit=false",
    "registry.file.max.errors.size=10"
})
@Log4j2
class TaskControllerIntegrationTest {

    @Autowired
    TaskRepository taskRepository;

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    Configuration freemarkerConfiguration;

    @SpyBean
    private DisabledKeycloakAuthorizationService disabledKeycloakAuthorizationService;

    @SpyBean
    private DisabledKeycloakUserAdministrationService disabledKeycloakUserAdministrationService;

    String emailTemplatesPath = "/templates/email";

    @BeforeEach
    public void setup() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);
    }

    @Test
    void testCompleteTask() throws Exception {

        // the following code part sets a keycloak id for the current user
        String currentUserKeycloakId = "fc4c4b91-efd8-4cc9-a96e-4efdae59b4bc";
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);

        // create two users one is the task initiator and one is the task claimant and store them in the database
        String initiatorKeykloakId = "fc4c4b91-efd8-4cc9-a96e-4efdae59b4ba";
        String initiatorUrid = new UserGeneratorService().generateURID();
        User initiator = new User();
        initiator.setUrid(initiatorUrid);
        initiator.setState(UserStatus.ENROLLED);
        initiator.setIamIdentifier(initiatorKeykloakId);
        initiator.setFirstName("Nick");
        initiator.setLastName("Dorsey");
        initiator.setDisclosedName(Utils.concat(" ", "Nick", "Dorsey"));
        userRepository.save(initiator);

        String taskClaimantDTOUrid = new UserGeneratorService().generateURID();
        User claimant = new User();
        claimant.setUrid(taskClaimantDTOUrid);
        claimant.setState(UserStatus.ENROLLED);
        claimant.setIamIdentifier(currentUserKeycloakId);
        claimant.setFirstName("John");
        claimant.setLastName("Sinclair");
        claimant.setDisclosedName(Utils.concat(" ", "John", "Sinclair"));
        userRepository.save(claimant);

        // Set role for the current user, this needs to be the senior-registry-administrator in order to be able to approve the task
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));
        //The initiator may be different than the user whom we are trying to change the email.
        String oldEmail = "NickDorsey@trasys.g";
        UserRepresentation userRepresentation =  new UserRepresentation();
        userRepresentation.setId(initiatorKeykloakId);
        userRepresentation.setFirstName(taskClaimantDTOUrid);
        userRepresentation.setLastName(taskClaimantDTOUrid);
        userRepresentation.setEmail(oldEmail);
        userRepresentation.setAttributes(Map.of());
        Mockito.when(disabledKeycloakUserAdministrationService.findByEmail(oldEmail)).thenReturn(
            Optional.of(userRepresentation));
        Mockito.when(disabledKeycloakUserAdministrationService.findByIamId(initiatorKeykloakId)).thenReturn(userRepresentation);
        
        // create task
        Task testTask = new Task();
        testTask.setRequestId(10L);
        testTask.setInitiatedBy(initiator);
        testTask.setClaimedBy(claimant);
        testTask.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        testTask.setType(RequestType.REQUESTED_EMAIL_CHANGE);
        testTask.setDifference("NickDorsey2@trasys.gr");
        testTask.setUser(claimant);
        taskRepository.save(testTask);

        // Send a complete task http request and make assertions
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/tasks.complete")
            .content("test")
            .param("requestId", "10")
            .param("taskOutcome", "APPROVED")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.taskDetailsDTO.taskStatus").value("COMPLETED"))
            .andReturn();
    }
}
