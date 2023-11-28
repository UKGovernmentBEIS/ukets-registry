package gov.uk.ets.registry.api.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.keycloak.TokenVerifier;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

import gov.uk.ets.registry.api.common.AbstractSecurityIntegrationTest;

@SpringBootTest
@ActiveProfiles("keycloak-config-test")
@Disabled
public class TaskControllerSecurityTest extends AbstractSecurityIntegrationTest {

	public static final String REQUEST_MAPPING_PATH = "/api-registry/tasks";
	// URL's under test
	public static final String SEARCH_TASKS_URL = API_BASE_URL + REQUEST_MAPPING_PATH;// + "/search";
	
	@Test
	public void testAccessToSearchTasksWithoutBearerShouldBeDenied() throws Exception {
		HttpResponse httpResponse = makeGetRequestWithoutBearer(SEARCH_TASKS_URL);
		assertUmaResponse(httpResponse);
	}		
	
	@ParameterizedTest(name = "{index} {0} has access to Search Account (GET) /api-registry/tasks/search")
	@CsvSource(value = { "uk-ets-admin,uk-ets-admin", 
			"uk-ets-senior-admin,uk-ets-senior-admin",
			"uk-ets-junior-admin,uk-ets-junior-admin", })
	public void testAdminAccessAllowedToSearchTasks(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException, VerificationException {

		try {
			String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
			assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
			
			//Prepare the uri for the request
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("sortDirection", Direction.ASC.toString()));
			params.add(new BasicNameValuePair("sortField", "requestId"));
			URI uri = new URIBuilder(SEARCH_TASKS_URL).addParameters(params).build();
			
			HttpResponse response = makeGetRequest(uri, accessToken);
			String ticket = assertUmaResponse(response);						
			String rpt = obtainRequestingPartyToken(accessToken, ticket);
			// accessing resource with an RPT
			response = makeGetRequest(uri, rpt);
			assertAccessGranted(response);
			// scopes should be valid.
			AccessToken token = TokenVerifier.create(rpt,AccessToken.class).getToken();
			Set<String> scopes = token.getAuthorization().getPermissions().stream().map(p->p.getScopes()).flatMap(Collection::stream).collect(Collectors.toSet());
			assertTrue(scopes.contains(gov.uk.ets.registry.api.authz.Scope.SCOPE_ACTION_ANY_ADMIN.getScopeName()));
			assertFalse(scopes.contains(gov.uk.ets.registry.api.authz.Scope.SCOPE_ACTION_NON_ADMIN.getScopeName()));
		} catch(AuthorizationDeniedException ignore) {
			fail(String.format("Should succeed, user %s is supposed to have access to search account resource.",username));
		}
		
	}	
	
	@ParameterizedTest(name = "{index} {0} has access to Search Account (GET) /api-registry/tasks/search")
	@CsvSource(value = {
			"authorized-representative_1,authorized-representative_1",
			"enrolled_user,enrolled_user",
			"registered_user,registered_user", 
			"validated_user,validated_user"})
	public void testEnrolledAccessAllowedToSearchTasks(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException, VerificationException {

		try {
			String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
			assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
			
			//Prepare the uri for the request
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("sortDirection", Direction.ASC.toString()));
			params.add(new BasicNameValuePair("sortField", "requestId"));
			URI uri = new URIBuilder(SEARCH_TASKS_URL).addParameters(params).build();
			
			HttpResponse response = makeGetRequest(uri, accessToken);
			String ticket = assertUmaResponse(response);						
			String rpt = obtainRequestingPartyToken(accessToken, ticket);
			// accessing resource with an RPT
			response = makeGetRequest(uri, rpt);
			assertAccessGranted(response);
			// scopes should be valid.
			AccessToken token = TokenVerifier.create(rpt,AccessToken.class).getToken();
			Set<String> scopes = token.getAuthorization().getPermissions().stream().map(p->p.getScopes()).flatMap(Collection::stream).collect(Collectors.toSet());
			assertFalse(scopes.contains(gov.uk.ets.registry.api.authz.Scope.SCOPE_ACTION_ANY_ADMIN.getScopeName()));
			assertTrue(scopes.contains(gov.uk.ets.registry.api.authz.Scope.SCOPE_ACTION_NON_ADMIN.getScopeName()));
		} catch(AuthorizationDeniedException ignore) {
			fail(String.format("Should succeed, user %s is supposed to have access to search account resource.",username));
		}
		
	}	
}
