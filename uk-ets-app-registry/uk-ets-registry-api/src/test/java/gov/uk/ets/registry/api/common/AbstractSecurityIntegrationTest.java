/**
 * 
 */
package gov.uk.ets.registry.api.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.keycloak.OAuth2Constants;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.representations.AccessTokenResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author P35036
 */
public abstract class AbstractSecurityIntegrationTest {

	//Keycloak related props
	public static final String KEYCLOAK_BASE_URL = "http://localhost:8091/auth";
	public static final String KEYCLOAK_REALM_UNDER_TEST = "uk-ets";
	public static final String KEYCLOAK_WEB_CLIENT_ID = "uk-ets-web-app";
	public static final String KEYCLOAK_REGISTRY_API_CLIENT_ID = "uk-ets-registry-api";
	public static final String KEYCLOAK_TOKEN_ENDPOINT_URL = KEYCLOAK_BASE_URL + "/realms/" + KEYCLOAK_REALM_UNDER_TEST + "/protocol/openid-connect/token";
	//API related props
	public static final String API_BASE_URL = "http://localhost:8080";
	public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
	
	public HttpResponse makeGetRequest(String uri, String token) throws IOException {
		
		return Request.Get(uri).
				addHeader("Authorization", "Bearer " + token).				
				execute().
				returnResponse();
	}

	public HttpResponse makeGetRequest(URI uri, String token) throws IOException, URISyntaxException {
				
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(uri);
		request.addHeader("Authorization", "Bearer " + token);
		HttpResponse response = client.execute(request);
		
		return response;
	}
	
	public HttpResponse makeGetRequestWithoutBearer(String uri) throws IOException {
		return Request.Get(uri).
				execute().
				returnResponse();
	}

	public HttpResponse makePostRequestWithoutBearer(String uri) throws IOException {
		return Request.Post(uri).
				execute().
				returnResponse();
	}
	
	public HttpResponse makePostRequestContentTypeJSON(String uri, String token,String json) throws IOException {

		return Request.Post(uri).addHeader("Content-type", "application/json").
				addHeader("Authorization", "Bearer " + token).
				body(new StringEntity(json)).
				execute().
				returnResponse();
	}

	public String obtainAccessToken(String userName, String password, String clientId) throws ClientProtocolException, IOException {

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD));
		params.add(new BasicNameValuePair(OAuth2Constants.CLIENT_ID, clientId));
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("password", password));
		
		HttpResponse response = Request.Post(KEYCLOAK_TOKEN_ENDPOINT_URL).
				bodyForm(params).
				execute().
				returnResponse();
		assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode(),String.format("Error requesting Access Token : %s", response.getStatusLine()));
		ObjectMapper mapper = new ObjectMapper();
		AccessTokenResponse accessTokenResponse = mapper.readValue(response.getEntity().getContent(),AccessTokenResponse.class);
		assertNotNull(accessTokenResponse.getToken());

		return accessTokenResponse.getToken();

	}

	/**
	 * Obtain a RPT from a Keycloak server. The RPT is a result of the evaluation of
	 * all policies associated with the resources being requested as defined by
	 * <code>permissionRequest</code>.
	 *
	 * @param permissionRequest the permission request
	 * @param accessToken       an OAuth2 access token previously issued by Keycloak
	 * @return requesting party token (RPT) as a String
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String obtainRequestingPartyToken(String accessToken, String ticket) throws ClientProtocolException, IOException {

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair(OAuth2Constants.GRANT_TYPE, OAuth2Constants.UMA_GRANT_TYPE));
		params.add(new BasicNameValuePair(OAuth2Constants.AUDIENCE, KEYCLOAK_REGISTRY_API_CLIENT_ID));
		params.add(new BasicNameValuePair("ticket", ticket));
		
		HttpResponse response = Request.Post(KEYCLOAK_TOKEN_ENDPOINT_URL).
				addHeader("Authorization", "Bearer " + accessToken).
				bodyForm(params).
				execute().
				returnResponse();
		
		if(HttpStatus.SC_UNAUTHORIZED == response.getStatusLine().getStatusCode() || HttpStatus.SC_FORBIDDEN == response.getStatusLine().getStatusCode()) {
			throw new AuthorizationDeniedException(ticket,null);
		}
		assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode(),String.format("Error requesting RPT : %s", response.getStatusLine()));
		ObjectMapper mapper = new ObjectMapper();
		AccessTokenResponse rptResponse = mapper.readValue(response.getEntity().getContent(),AccessTokenResponse.class);

		return rptResponse.getToken();
	}

	public void assertAccessGranted(HttpResponse response) throws IOException {
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	public void assertNotFound(HttpResponse response) throws IOException {
		assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
	}

	public void assertCreated(HttpResponse response) throws IOException {
		assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
	}
	
	/**
	 * Verify that the response is UMA 2.0 compliant.
	 */
	public String assertUmaResponse(HttpResponse httpResponse) throws IOException {

		assertEquals(HttpStatus.SC_UNAUTHORIZED, httpResponse.getStatusLine().getStatusCode());
		assertEquals(1, httpResponse.getHeaders(WWW_AUTHENTICATE).length);
		assertTrue(httpResponse.getHeaders(WWW_AUTHENTICATE)[0].getValue().contains("UMA realm=\"" + KEYCLOAK_REALM_UNDER_TEST + "\""));
		assertTrue(httpResponse.getHeaders(WWW_AUTHENTICATE)[0].getValue().contains("as_uri=\"" + KEYCLOAK_BASE_URL + "/realms/" + KEYCLOAK_REALM_UNDER_TEST + "\""));
		assertTrue(httpResponse.getHeaders(WWW_AUTHENTICATE)[0].getValue().contains("ticket="));
		String ticket = extractTicket(httpResponse);
		assertNotNull(String.format("No ticket in response"), ticket);

		return ticket;
	}

	private String extractTicket(HttpResponse response) {
		String ticketValue = response.getHeaders(WWW_AUTHENTICATE)[0].getValue().split(",")[2].split("=")[1];
		return ticketValue.substring(1, ticketValue.length() - 1);
	}
}
