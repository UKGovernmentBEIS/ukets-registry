package gov.uk.ets.registry.api.note;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.authz.DisabledKeycloakAuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.repositories.ContactRepository;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.test.RegistryIntegrationTest;
import gov.uk.ets.registry.api.note.domain.Note;
import gov.uk.ets.registry.api.note.domain.NoteDomainType;
import gov.uk.ets.registry.api.note.repository.NoteRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.UserGeneratorService;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RegistryIntegrationTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "keycloak.enabled=false"
})
@Log4j2
class NoteIntegrationTest {

    private static final String currentUserKeycloakId = UUID.randomUUID().toString();;
    private static User currentUser;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @SpyBean
    private DisabledKeycloakAuthorizationService disabledKeycloakAuthorizationService;

    @SpyBean
    private DisabledKeycloakUserAdministrationService disabledKeycloakUserAdministrationService;

    @BeforeAll
    public void setup() throws NoSuchAlgorithmException {
        String urid = new UserGeneratorService().generateURID();
        User user = new User();
        user.setUrid(urid);
        user.setState(UserStatus.ENROLLED);
        user.setIamIdentifier(currentUserKeycloakId);
        user.setFirstName("Tony");
        user.setLastName("Montana");
        currentUser = userRepository.save(user);

        Contact contact = new Contact();
        contact.setEmailAddress("email@tt.t");
        contactRepository.save(contact);

        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setName("AH name");
        accountHolder.setIdentifier(11111111L);
        accountHolder.setContact(contact);
        accountHolder.setType(AccountHolderType.ORGANISATION);
        accountHolderRepository.save(accountHolder);

        createAccount(123456789L, accountHolder);
        createAccount(222222L, accountHolder);
        createAccount(333333L, accountHolder);
    }

    private void createAccount(Long identifier, AccountHolder accountHolder) {
        Account account = new Account();
        account.setAccountName("AC name");
        account.setIdentifier(identifier);
        account.setAccountHolder(accountHolder);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setStatus(Status.ACTIVE);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setOpeningDate(new Date());
        account.setCommitmentPeriodCode(0);
        account.setCheckDigits(27);
        account.setFullIdentifier("UK" + identifier);
        account.setAccountType("ETS - Trading account");
        accountRepository.save(account);
    }

    @Test
    void testGetNotesForAccount_NoResults() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);
        
        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/api-registry/notes.get")
            .param("domainId", "10")
            .param("domainType", "ACCOUNT")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json("[]"))
            .andReturn();
    }

    @Test
    void testGetNotesForAccount_WithResults() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);
        
        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        Instant now = Instant.now();

        Note accountNote = new Note();
        accountNote.setDomainId("123456789");
        accountNote.setDomainType(NoteDomainType.ACCOUNT);
        accountNote.setUser(currentUser);
        accountNote.setDescription("Note for Account");
        accountNote.setCreationDate(Date.from(now));
        noteRepository.save(accountNote);

        Note accountHolderNote = new Note();
        accountHolderNote.setDomainId("11111111");
        accountHolderNote.setDomainType(NoteDomainType.ACCOUNT_HOLDER);
        accountHolderNote.setUser(currentUser);
        accountHolderNote.setDescription("Note for Account Holder");
        accountNote.setCreationDate(Date.from(now.plusSeconds(1L)));
        noteRepository.save(accountHolderNote);

        // when
        ResultActions accountResult = mockMvc.perform(MockMvcRequestBuilders
            .get("/api-registry/notes.get")
            .param("domainId", "123456789")
            .param("domainType", "ACCOUNT")
            .accept(MediaType.APPLICATION_JSON));

        ResultActions accountHolderResult = mockMvc.perform(MockMvcRequestBuilders
            .get("/api-registry/notes.get")
            .param("domainId", "11111111")
            .param("domainType", "ACCOUNT_HOLDER")
            .accept(MediaType.APPLICATION_JSON));


        // then
        accountResult.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("[0].id").value(accountNote.getId()))
            .andReturn();

        accountHolderResult.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("[0].id").value(accountHolderNote.getId()))
            .andReturn();
    }

    @Test
    void testAddNote() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);
        
        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        Mockito.when(disabledKeycloakAuthorizationService.getUrid()).thenReturn(currentUser.getUrid());

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/notes.add")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"description\":\"Description\",\"domainType\":\"ACCOUNT\",\"domainId\":\"222222\"}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Description"))
            .andReturn();
    }

    @Test
    void testAddNote_WithInvalidDomainId() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);
        
        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));


        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/notes.add")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"description\":\"Description\",\"domainType\":\"ACCOUNT\",\"domainId\":\"99999999999\"}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorDetails", hasSize(1)))
            .andExpect(jsonPath("$.errorDetails[0].message", is("Domain Type ACCOUNT with Domain Id: 99999999999 does not exists.")))
            .andReturn();
    }

    @Test
    void testDeleteNote() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);
        
        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        Note note = new Note();
        note.setDomainId("333333");
        note.setDomainType(NoteDomainType.ACCOUNT);
        note.setUser(currentUser);
        note.setDescription("Note for Account");
        noteRepository.save(note);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .delete("/api-registry/notes.delete/" + note.getId())
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
            .andReturn();
    }
}
