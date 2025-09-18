package gov.uk.ets.registry.api.document.management;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.Lists;
import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.authz.DisabledKeycloakAuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.test.RegistryIntegrationTest;
import gov.uk.ets.registry.api.document.management.domain.Document;
import gov.uk.ets.registry.api.document.management.domain.DocumentCategory;
import gov.uk.ets.registry.api.document.management.repository.DocumentCategoryRepository;
import gov.uk.ets.registry.api.document.management.repository.DocumentRepository;
import gov.uk.ets.registry.api.user.UserGeneratorService;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
class DocumentIntegrationTest {

    private final String currentUserKeycloakId = UUID.randomUUID().toString();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentCategoryRepository documentCategoryRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @SpyBean
    private DisabledKeycloakAuthorizationService disabledKeycloakAuthorizationService;

    @SpyBean
    private DisabledKeycloakUserAdministrationService disabledKeycloakUserAdministrationService;

    private DocumentCategory categoryOne;


    @BeforeAll
    public void setup() throws NoSuchAlgorithmException {
        String urid = new UserGeneratorService().generateURID();
        User user = new User();
        user.setUrid(urid);
        user.setState(UserStatus.ENROLLED);
        user.setIamIdentifier(currentUserKeycloakId);
        user.setFirstName("Tony");
        user.setLastName("Montana");
        userRepository.save(user);

        Instant now = Instant.now().minusSeconds(1L);
        Instant yesterday = now.minus(1, ChronoUnit.DAYS);
        Instant twoDaysAgo = now.minus(2, ChronoUnit.DAYS);
        Instant threeDaysAgo = now.minus(3, ChronoUnit.DAYS);

        categoryOne = buildCategory("Category 1", 1, threeDaysAgo);
        DocumentCategory categoryTwo = buildCategory("Category 2", 2, twoDaysAgo);
        DocumentCategory categoryThree = buildCategory("Category 3", 3, yesterday);
        DocumentCategory categoryFour = buildCategory("Category 4", 4, now);

        documentCategoryRepository.saveAll(List.of(categoryOne, categoryTwo, categoryThree, categoryFour));

        Document documentOne = buildDocument("Document 1", 1, threeDaysAgo, categoryOne);
        Document documentTwo = buildDocument("Document 2", 2, twoDaysAgo, categoryOne);
        Document documentThree = buildDocument("Document 3", 1, yesterday, categoryTwo);
        Document documentFour = buildDocument("Document 4", 1, now, categoryThree);
        Document documentFive = buildDocument("Document 5", 1, now, categoryFour);

        documentRepository.saveAll(List.of(documentOne, documentTwo, documentThree, documentFour, documentFive));

    }

    @Test
    void testGetCategories() throws Exception {
        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/api-registry/document-categories.get")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("[0].name").value("Category 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("[0].documents[0].title").value("Document 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("[0].documents[0].name").value("Document 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("[0].documents[1].title").value("Document 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("[0].documents[1].name").value("Document 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("[1].name").value("Category 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("[1].documents[0].title").value("Document 3"))
            .andExpect(MockMvcResultMatchers.jsonPath("[1].documents[0].name").value("Document 3"))
            .andExpect(MockMvcResultMatchers.jsonPath("[2].name").value("Category 3"))
            .andExpect(MockMvcResultMatchers.jsonPath("[2].documents[0].title").value("Document 4"))
            .andExpect(MockMvcResultMatchers.jsonPath("[2].documents[0].name").value("Document 4"))
            .andExpect(MockMvcResultMatchers.jsonPath("[3].name").value("Category 4"))
            .andExpect(MockMvcResultMatchers.jsonPath("[3].documents[0].title").value("Document 5"))
            .andExpect(MockMvcResultMatchers.jsonPath("[3].documents[0].name").value("Document 5"))
            .andReturn();
    }

    @Test
    void testAddCategory() throws Exception {
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
            .post("/api-registry/document-categories.add")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"New Category\",\"order\":5}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk()).andReturn();
    }

    @Test
    void testUpdateCategory() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);

        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        DocumentCategory categoryToBeUpdated = buildCategory("Category to be updated", 5, Instant.now());
        documentCategoryRepository.save(categoryToBeUpdated);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .put("/api-registry/document-categories.update")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"id\":" + categoryToBeUpdated.getId() + ",\"name\":\"Updated Category\",\"order\":5}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Category"))
            .andReturn();
    }

    @Test
    void testDeleteCategory() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);

        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        DocumentCategory categoryToBeDeleted = buildCategory("Category to be deleted", 5, Instant.now());
        documentCategoryRepository.save(categoryToBeDeleted);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .delete("/api-registry/document-categories.delete/" + categoryToBeDeleted.getId())
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk()).andReturn();
    }

    @Test
    void testGetDocument() throws Exception {
        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .get("/api-registry/document.get/1"));

        // then
        result.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().bytes(new byte[0]))
            .andReturn();
    }

    @Test
    void testAddDocument() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);

        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        MockMultipartFile request = new MockMultipartFile("file","newDocument.txt",null, new byte[0]);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .multipart("/api-registry/document.add")
            .file(request)
            .param("title", "Title")
            .param("categoryId", "1")
            .param("order", "1"));

        // then
        result.andExpect(status().isOk()).andReturn();
    }

    @Test
    void testUpdateDocument() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);

        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        Document documentToBeUpdated = buildDocument("Document to be Updated", 3, Instant.now(), categoryOne);
        documentToBeUpdated = documentRepository.save(documentToBeUpdated);

        MockMultipartFile request = new MockMultipartFile("file","updatedDocument.txt",null, new byte[0]);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .multipart(HttpMethod.PATCH, "/api-registry/document.update")
            .file(request)
            .param("id", documentToBeUpdated.getId().toString())
            .param("order", "1"));

        // then
        result.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("updatedDocument.txt"))
            .andReturn();
    }

    @Test
    void testDeleteDocument() throws Exception {
        // given

        // the following code part sets a keycloak id for the current user
        Mockito.when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);

        // Set role for the current user, this needs to be the senior-registry-administrator
        RoleRepresentation admin =
            new RoleRepresentation("senior-registry-administrator", "senior role description", true);
        Mockito.when(disabledKeycloakAuthorizationService.getClientLevelRoles(currentUserKeycloakId)).thenReturn(
            Lists.newArrayList(admin));

        Document documentToBeDeleted = buildDocument("Document to be deleted", 3, Instant.now(), categoryOne);
        documentRepository.save(documentToBeDeleted);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .delete("/api-registry/document.delete/" + documentToBeDeleted.getId())
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk()).andReturn();
    }

    private DocumentCategory buildCategory(String name, int order, Instant date) {
        Date creationDate = Date.from(date);

        DocumentCategory category = new DocumentCategory();
        category.setName(name);
        category.setPosition(order);
        category.setCreatedOn(creationDate);
        category.setUpdatedOn(creationDate);

        return category;
    }

    private Document buildDocument(String name, int order, Instant date, DocumentCategory category) {
        Date creationDate = Date.from(date);

        Document document = new Document();
        document.setTitle(name);
        document.setName(name);
        document.setData(new byte[0]);
        document.setPosition(order);
        document.setCreatedOn(creationDate);
        document.setUpdatedOn(creationDate);

        document.setCategory(category);
        category.getDocuments().add(document);

        return document;
    }
}
