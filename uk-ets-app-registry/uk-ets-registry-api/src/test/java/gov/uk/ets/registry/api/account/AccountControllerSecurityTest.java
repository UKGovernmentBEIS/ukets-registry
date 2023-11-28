package gov.uk.ets.registry.api.account;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.common.AbstractSecurityIntegrationTest;
import gov.uk.ets.registry.api.helper.TestUtil;


@SpringBootTest
@ActiveProfiles("keycloak-config-test")
@Disabled
public class AccountControllerSecurityTest extends AbstractSecurityIntegrationTest {
	
	@Autowired
	private ObjectMapper mapper;
	  
	public static final String REQUEST_MAPPING_PATH = "/api-registry/account";
	//URL's under test
	public static final String CREATE_ACCOUNT_URL = API_BASE_URL+REQUEST_MAPPING_PATH;
	public static final String GET_ACCOUNT_HOLDER_BY_ID_URL = API_BASE_URL+REQUEST_MAPPING_PATH + "/holder/10098";
	public static final String GET_ACCOUNT_HOLDER_BY_NAME_URL = API_BASE_URL+REQUEST_MAPPING_PATH + "/holderByName";
	public static final String GET_ACCOUNT_HOLDER_BY_USER_URL = API_BASE_URL+REQUEST_MAPPING_PATH + "/holderByUser";
	public static final String GET_LEGAL_REPRESENTATIVE_BY_HOLDER_URL = API_BASE_URL+REQUEST_MAPPING_PATH + "/legalRepresentative/byHolder";
	public static final String SEARCH_ACCOUNTS_URL = API_BASE_URL + REQUEST_MAPPING_PATH + "/search";
	public static final String SEARCH_ACCOUNTS_FILTERS_URL = API_BASE_URL+REQUEST_MAPPING_PATH + "/search/filters";	
	
	@Test
	public void testAccessToAccountHolderByIdWithoutBearerShouldBeDenied() throws Exception {
		HttpResponse httpResponse = makeGetRequestWithoutBearer(GET_ACCOUNT_HOLDER_BY_ID_URL);
		assertUmaResponse(httpResponse);
	}

	@Test
	public void testAccessToAccountHolderByNameWithoutBearerShouldBeDenied() throws Exception {
		HttpResponse httpResponse = makeGetRequestWithoutBearer(GET_ACCOUNT_HOLDER_BY_NAME_URL);
		assertUmaResponse(httpResponse);
	}
	
	@Test
	public void testAccessToAccountHolderByUserWithoutBearerShouldBeDenied() throws Exception {
		HttpResponse httpResponse = makeGetRequestWithoutBearer(GET_ACCOUNT_HOLDER_BY_USER_URL);
		assertUmaResponse(httpResponse);
	}
	
	@Test
	public void testAccessToLegalRepresentativeByUserWithoutBearerShouldBeDenied() throws Exception {
		HttpResponse httpResponse = makeGetRequestWithoutBearer(GET_LEGAL_REPRESENTATIVE_BY_HOLDER_URL);
		assertUmaResponse(httpResponse);
	}	
	
	@ParameterizedTest(name = "{index} {0} has access to GET /api-registry/account-holder.get/[identifier] ")
	@CsvSource(value = { "uk-ets-admin,uk-ets-admin", 
			"uk-ets-senior-admin,uk-ets-senior-admin",
			"uk-ets-junior-admin,uk-ets-junior-admin", 
			"uk-ets-readonly-admin,uk-ets-readonly-admin",
			"authorized-representative_1,authorized-representative_1",
			"verifier_1,verifier_1", 
			"registered_user,registered_user", 
			"validated_user,validated_user", 
			"enrolled_user,enrolled_user" })
	public void testAccessToAccountHolder(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException {

		String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
		assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);

		// Accessing resource with regular access token
		HttpResponse response = makeGetRequest(GET_ACCOUNT_HOLDER_BY_ID_URL, accessToken);
		String ticket = assertUmaResponse(response);

		String rpt = obtainRequestingPartyToken(accessToken, ticket);
		// accessing resource with an RPT
		response = makeGetRequest(GET_ACCOUNT_HOLDER_BY_ID_URL, rpt);
		assertNotFound(response);
	}

	@ParameterizedTest(name = "{index} {0} has access to GET /api-registry/account-holder.get.by-name")
	@CsvSource(value = { "uk-ets-admin,uk-ets-admin", 
			"uk-ets-senior-admin,uk-ets-senior-admin",
			"uk-ets-junior-admin,uk-ets-junior-admin", 
			"uk-ets-readonly-admin,uk-ets-readonly-admin",
			"authorized-representative_1,authorized-representative_1",
			"verifier_1,verifier_1", 
			"registered_user,registered_user", 
			"validated_user,validated_user", 
			"enrolled_user,enrolled_user" })
	public void testAccessToAccountHolderByName(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException {

		String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
		assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
		
		//Prepare the uri for the request
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("name", "John Smith"));
		params.add(new BasicNameValuePair("type", AccountHolderType.INDIVIDUAL.toString()));
		URI uri = new URIBuilder(GET_ACCOUNT_HOLDER_BY_NAME_URL).addParameters(params).build();
		
		HttpResponse response = makeGetRequest(uri, accessToken);
		String ticket = assertUmaResponse(response);

		String rpt = obtainRequestingPartyToken(accessToken, ticket);
		// accessing resource with an RPT
		response = makeGetRequest(uri, rpt);
		assertAccessGranted(response);
	}
	
	@ParameterizedTest(name = "{index} {0} has access to GET /api-registry/account-holder.get.list")
	@CsvSource(value = { "uk-ets-admin,uk-ets-admin", 
			"uk-ets-senior-admin,uk-ets-senior-admin",
			"uk-ets-junior-admin,uk-ets-junior-admin", 
			"uk-ets-readonly-admin,uk-ets-readonly-admin",
			"authorized-representative_1,authorized-representative_1",
			"verifier_1,verifier_1", 
			"registered_user,registered_user", 
			"validated_user,validated_user", 
			"enrolled_user,enrolled_user" })
	public void testAccessToAccountHolderByUser(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException {

		String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
		assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
		
		//Prepare the uri for the request
		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("urid", "UK600543316240"));
		params.add(new BasicNameValuePair("holderType", AccountHolderType.INDIVIDUAL.toString()));
		URI uri = new URIBuilder(GET_ACCOUNT_HOLDER_BY_USER_URL).addParameters(params).build();
		
		HttpResponse response = makeGetRequest(uri, accessToken);
		String ticket = assertUmaResponse(response);

		String rpt = obtainRequestingPartyToken(accessToken, ticket);
		// accessing resource with an RPT
		response = makeGetRequest(uri, rpt);
		assertAccessGranted(response);
	}	
	
	@Test
	public void testAccessToCreateAccountWithoutBearerShouldBeDenied() throws Exception {
		HttpResponse httpResponse = makePostRequestWithoutBearer(CREATE_ACCOUNT_URL);
		assertUmaResponse(httpResponse);
	}
	
	@ParameterizedTest(name = "{index} {0} has access to Create Account (POST) /api-registry/account/ ")
	@CsvSource(value = { "uk-ets-admin,uk-ets-admin", 
			"uk-ets-senior-admin,uk-ets-senior-admin",
			"uk-ets-junior-admin,uk-ets-junior-admin",
			"authorized-representative_1,authorized-representative_1",
			"registered_user,registered_user", 
			"validated_user,validated_user", 
			"enrolled_user,enrolled_user" })
	public void testAccessAllowedToCreateAccount(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException {

		String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
		assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);

		AccountDTO account = TestUtil.sampleAccountDTO();
		
		// Accessing resource with regular access token
		HttpResponse response = makePostRequestContentTypeJSON(CREATE_ACCOUNT_URL, accessToken,
			mapper.writeValueAsString(account));
		String ticket = assertUmaResponse(response);

		try {
			String rpt = obtainRequestingPartyToken(accessToken, ticket);
			// accessing resource with an RPT
			response = makePostRequestContentTypeJSON(CREATE_ACCOUNT_URL, rpt,mapper.writeValueAsString(account));
			assertCreated(response);
		} catch(AuthorizationDeniedException ignore) {
			fail(String.format("Should succeed, user %s is supposed to have access to create account resource (scope urn:uk-ets-registry-api:page:dashboard:requestAccountMenuItem:view)",username));
		}
		
	}

	@ParameterizedTest(name = "{index} {0} does not have has access to Create Account (POST) /api-registry/account/ ")
	@CsvSource(value = {"uk-ets-readonly-admin,uk-ets-readonly-admin",
			"verifier_1,verifier_1" })
	public void testAccessNotAllowedToCreateAccount(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException {

		String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
		assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);

		AccountDTO account = TestUtil.sampleAccountDTO();
		
		// Accessing resource with regular access token
		HttpResponse response = makePostRequestContentTypeJSON(CREATE_ACCOUNT_URL, accessToken,
			mapper.writeValueAsString(account));
		String ticket = assertUmaResponse(response);

		try {
			String rpt = obtainRequestingPartyToken(accessToken, ticket);
			// accessing resource with an RPT
			response = makePostRequestContentTypeJSON(CREATE_ACCOUNT_URL, rpt,mapper.writeValueAsString(account));
			fail(String.format("Should fail, user %s is not supposed to have access to create account resource (scope urn:uk-ets-registry-api:page:dashboard:requestAccountMenuItem:view)",username));
		} catch(AuthorizationDeniedException ignore) {
		}
	}	
	
	@Test
	public void testAccessToSearchAccountsWithoutBearerShouldBeDenied() throws Exception {
		HttpResponse httpResponse = makeGetRequestWithoutBearer(SEARCH_ACCOUNTS_URL);
		assertUmaResponse(httpResponse);
	}		
	
	@ParameterizedTest(name = "{index} {0} has access to Search Account (GET) /api-registry/account/search")
	@CsvSource(value = { "uk-ets-admin,uk-ets-admin", 
			"uk-ets-senior-admin,uk-ets-senior-admin",
			"uk-ets-junior-admin,uk-ets-junior-admin", })
	public void testAdminAccessAllowedToSearchAccounts(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException, VerificationException {

		try {
			String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
			assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
			
			//Prepare the uri for the request
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("sortDirection", Direction.ASC.toString()));
			params.add(new BasicNameValuePair("sortField", "requestId"));
			URI uri = new URIBuilder(SEARCH_ACCOUNTS_URL).addParameters(params).build();
			
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
			assertFalse(scopes.contains(gov.uk.ets.registry.api.authz.Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN.getScopeName()));
		} catch(AuthorizationDeniedException ignore) {
			fail(String.format("Should succeed, user %s is supposed to have access to search account resource.",username));
		}
		
	}	
	
	@ParameterizedTest(name = "{index} {0} has access to Search Account (GET) /api-registry/account/search")
	@CsvSource(value = {
			"authorized-representative_1,authorized-representative_1",
			"enrolled_user,enrolled_user" })
	public void testEnrolledAccessAllowedToSearchAccounts(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException, VerificationException {

		try {
			String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
			assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
			
			//Prepare the uri for the request
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("sortDirection", Direction.ASC.toString()));
			params.add(new BasicNameValuePair("sortField", "requestId"));
			URI uri = new URIBuilder(SEARCH_ACCOUNTS_URL).addParameters(params).build();
			
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
			assertTrue(scopes.contains(gov.uk.ets.registry.api.authz.Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN.getScopeName()));
		} catch(AuthorizationDeniedException ignore) {
			fail(String.format("Should succeed, user %s is supposed to have access to search account resource.",username));
		}
		
	}	
	
	
	@ParameterizedTest(name = "{index} {0} does not have access to Search Account (GET) /api-registry/account/search")
	@CsvSource(value = {"registered_user,registered_user", 
			"validated_user,validated_user"})
	public void testAccessNotAllowedToSearchAccounts(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException {

		try {
			
			String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
			assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
			
			//Prepare the uri for the request
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("sortDirection", Direction.ASC.toString()));
			params.add(new BasicNameValuePair("sortField", "requestId"));
			URI uri = new URIBuilder(SEARCH_ACCOUNTS_URL).addParameters(params).build();
			
			HttpResponse response = makeGetRequest(uri, accessToken);
			String ticket = assertUmaResponse(response);			
			
			String rpt = obtainRequestingPartyToken(accessToken, ticket);
			// accessing resource with an RPT
			response = makeGetRequest(uri, rpt);
			fail(String.format("Should fail, user %s is not supposed to have access to search account resource",username));
		} catch(AuthorizationDeniedException ignore) {
		}
	}		
	
	
	@Test
	public void testAccessToSearcAccountsFilterWithoutBearerShouldBeDenied() throws Exception {
		HttpResponse httpResponse = makeGetRequestWithoutBearer(SEARCH_ACCOUNTS_FILTERS_URL);
		assertUmaResponse(httpResponse);
	}
	
	@ParameterizedTest(name = "{index} {0} has access to Search Account Filters (GET) /api-registry/account/search/filters")
	@CsvSource(value = { "uk-ets-admin,uk-ets-admin", 
			"uk-ets-senior-admin,uk-ets-senior-admin",
			"uk-ets-junior-admin,uk-ets-junior-admin", })
	public void testAdminAccessAllowedToSearchAccountFilters(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException, VerificationException {

		try {
			String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
			assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
			
			//Prepare the uri for the request
			URI uri = new URIBuilder(SEARCH_ACCOUNTS_FILTERS_URL).build();
			
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
			assertTrue(scopes.contains("urn:uk-ets-registry-api:page:account:search:authorizedRepresentativeUrid:view"));
			assertTrue(scopes.contains("urn:uk-ets-registry-api:page:account:search:excludedForYear:view"));
			assertFalse(scopes.contains(gov.uk.ets.registry.api.authz.Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN.getScopeName()));
		} catch(AuthorizationDeniedException ignore) {
			fail(String.format("Should succeed, user %s is supposed to have access to search account resource.",username));
		}
		
	}
	
	@ParameterizedTest(name = "{index} {0} has access to Search Account Filters (GET) /api-registry/account/search/filters")
	@CsvSource(value = {
			"authorized-representative_1,authorized-representative_1",
			"enrolled_user,enrolled_user" })
	public void testEnrolledAccessAllowedToSearchAccountFilters(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException, VerificationException {

		try {
			String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
			assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
			
			//Prepare the uri for the request
			URI uri = new URIBuilder(SEARCH_ACCOUNTS_FILTERS_URL).build();
			
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
			assertFalse(scopes.contains("urn:uk-ets-registry-api:page:account:search:authorizedRepresentativeUrid:view"));
			assertFalse(scopes.contains("urn:uk-ets-registry-api:page:account:search:excludedForYear:view"));
			assertTrue(scopes.contains(gov.uk.ets.registry.api.authz.Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN.getScopeName()));
		} catch(AuthorizationDeniedException ignore) {
			fail(String.format("Should succeed, user %s is supposed to have access to search account resource.",username));
		}
		
	}	
	
	@ParameterizedTest(name = "{index} {0} does not have access to Search Account (GET) /api-registry/account/search/filters")
	@CsvSource(value = {"registered_user,registered_user", 
			"validated_user,validated_user"})
	public void testAccessNotAllowedToSearchAccountFilters(String username, String password)
			throws IOException, InvocationTargetException, IllegalAccessException, URISyntaxException {

		try {
			
			String accessToken = obtainAccessToken(username, password, KEYCLOAK_WEB_CLIENT_ID);
			assertNotNull(String.format("Error empty Access token: %s", accessToken), accessToken);
			
			//Prepare the uri for the request
			URI uri = new URIBuilder(SEARCH_ACCOUNTS_FILTERS_URL).build();
			
			HttpResponse response = makeGetRequest(uri, accessToken);
			String ticket = assertUmaResponse(response);			
			
			String rpt = obtainRequestingPartyToken(accessToken, ticket);
			// accessing resource with an RPT
			response = makeGetRequest(uri, rpt);
			fail(String.format("Should fail, user %s is not supposed to have access to search account resource",username));
		} catch(AuthorizationDeniedException ignore) {
		}
	}	
}
