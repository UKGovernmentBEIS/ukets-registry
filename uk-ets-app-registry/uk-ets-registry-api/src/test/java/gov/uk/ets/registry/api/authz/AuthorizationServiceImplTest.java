package gov.uk.ets.registry.api.authz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakClientRepresentationRepository;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakUserRepresentationRepository;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class) 
@ContextConfiguration
@DisplayName("Testing authorization service methods")
class AuthorizationServiceImplTest {

    private AuthorizationServiceImpl authorizationService;
    @Mock
    private KeycloakUserRepresentationRepository keycloakUserRepresentationRepository;
    @Mock
    private KeycloakClientRepresentationRepository keycloakClientRepresentationRepository;
    @Mock
    private HttpServletRequest request;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        authorizationService = new AuthorizationServiceImpl(keycloakUserRepresentationRepository,
                keycloakClientRepresentationRepository,
                request);
    }    
    
    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Has senior admin client role")
    @WithMockEtsUser(roles = "senior-registry-administrator")
    void hasSeniorAdminClientRole(UserRole role) {
        if (UserRole.SENIOR_REGISTRY_ADMINISTRATOR.equals(role)) {
            assertTrue(authorizationService.hasClientRole(role));
        } else {
            assertFalse(authorizationService.hasClientRole(role));
        }
    }
    
    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Has junior admin client role")
    @WithMockEtsUser(roles = "junior-registry-administrator")
    void hasJuniorAdminClientRole(UserRole role) {
        if (UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.equals(role)) {
            assertTrue(authorizationService.hasClientRole(role));
        } else {
            assertFalse(authorizationService.hasClientRole(role));
        }
    } 
    
    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Has read only admin client role")
    @WithMockEtsUser(roles = "readonly-administrator")
    void hasReadOnlyAdminClientRole(UserRole role) {
        if (UserRole.READONLY_ADMINISTRATOR.equals(role)) {
            assertTrue(authorizationService.hasClientRole(role));
        } else {
            assertFalse(authorizationService.hasClientRole(role));
        }
    } 
    
    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Has authorized representative client role")
    @WithMockEtsUser(roles = "authorized-representative")
    void hasAuthorizedRepresentativeClientRole(UserRole role) {
        if (UserRole.AUTHORISED_REPRESENTATIVE.equals(role)) {
            assertTrue(authorizationService.hasClientRole(role));
        } else {
            assertFalse(authorizationService.hasClientRole(role));
        }
    } 
    
    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Has authority user client role")
    @WithMockEtsUser(roles = {"authority-user","ets_user"})
    void hasAuthorityUserClientRole(UserRole role) {
        if (UserRole.AUTHORITY_USER.equals(role)) {
            assertTrue(authorizationService.hasClientRole(role));
        } else {
            assertFalse(authorizationService.hasClientRole(role));
        }
    }   
    
    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Has no client role")
    @WithMockEtsUser()
    void hasNoClientRole(UserRole role) {
        assertFalse(authorizationService.hasClientRole(role));
    }
    
    @Test
    @DisplayName("User state is ENROLLED")
    @WithMockEtsUser()
    void getEnrolledUserState() {    
        assertEquals(UserStatus.ENROLLED.toString(),authorizationService.getUserState());
    }
    
    @Test
    @DisplayName("User state is ENROLLED")
    @WithMockEtsUser(state = "")
    void getUserState() {    
        assertTrue(authorizationService.getUserState().isEmpty());
    }
    
    @Test
    @DisplayName("urid is UK588332110438")
    @WithMockEtsUser(urid = "UK588332110438")
    void getUrid() {    
        assertFalse(authorizationService.getUrid().isEmpty());
        assertEquals("UK588332110438",authorizationService.getUrid());
    }
    
    @Test
    @DisplayName("Subject is 7856bfc4-adce-4b1a-8e3a-2fbdf0409e79")
    @WithMockEtsUser()
    void getClaim() {    
        assertFalse(authorizationService.getClaim(OAuth2ClaimNames.SUBJECT).isEmpty());
        assertEquals("7856bfc4-adce-4b1a-8e3a-2fbdf0409e79",authorizationService.getClaim(OAuth2ClaimNames.SUBJECT));
    }
    
    @Test
    @DisplayName("User has no assigned scopes.")
    @WithMockEtsUser()
    void getEmptyScopes() {    
        assertTrue(authorizationService.getScopes().isEmpty());
    }
    
    @Test
    @DisplayName("User has the assigned scopes.")
    @WithMockEtsUser(scopes =  {
        "urn:uk-ets-registry-api:page:account:search:authorizedRepresentativeUrid:view",
        "urn:uk-ets-registry-api:page:account:search:installationOrAircraftOperatorId:view"})
    void getScopes() {    
        assertFalse(authorizationService.getScopes().isEmpty());
        assertEquals(Set.of("urn:uk-ets-registry-api:page:account:search:authorizedRepresentativeUrid:view",
                "urn:uk-ets-registry-api:page:account:search:installationOrAircraftOperatorId:view"),authorizationService.getScopes());
    }
}
