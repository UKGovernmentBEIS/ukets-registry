package gov.uk.ets.registry.api.ar.infrastructure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.user.repository.UserWorkContactRepositoryImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ARWorkContactRepositoryImplTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AccessTokenResponse accessTokenResponse;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Mock
    ResponseEntity<UserWorkContact[]> response;

    private UserWorkContactRepositoryImpl personalUserInfoRepository;

    @Test
    public void testFetch() {
        // given
        UserWorkContact userInfo = new UserWorkContact();
        userInfo.setUrid("UK123123");

        List<UserWorkContact> expectedResults = createExpectedResults("UK21321", "UK22221321", "UK987655");

        Set<String> urids = expectedResults.stream().map(UserWorkContact::getUrid).collect(Collectors.toSet());
        String token = "test-token";
        personalUserInfoRepository = new UserWorkContactRepositoryImpl(
            "http://localhost:8091/auth",
            "test",
            "/uk-ets-users",
            restTemplate, authorizationService, serviceAccountAuthorizationService);
        given(authorizationService.obtainAccessToken())
            .willReturn(accessTokenResponse);
        given(accessTokenResponse.getToken()).willReturn(token);

        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(UserWorkContact[].class))).willReturn(response);
        given(response.getBody()).willReturn(expectedResults.toArray(new UserWorkContact[]{}));

        // when
        List<UserWorkContact> result = personalUserInfoRepository.fetch(urids, false);

        // then
        then(authorizationService).should(times(1)).obtainAccessToken();
        then(accessTokenResponse).should(times(1)).getToken();
        ArgumentCaptor<HttpEntity<?>> request = ArgumentCaptor.forClass(HttpEntity.class);
        then(restTemplate).should(times(1)).exchange(anyString(), eq(HttpMethod.GET), request.capture(), eq(
            UserWorkContact[].class));
        HttpEntity<?> httpEntity = request.getValue();
        assertNotNull(httpEntity);
        assertEquals(MediaType.APPLICATION_JSON_VALUE, httpEntity.getHeaders().get("Accept").get(0));
        assertEquals("bearer " + token, httpEntity.getHeaders().get("Authorization").get(0));
        assertEquals(expectedResults.get(0).getUrid(), result.get(0).getUrid());
    }

    private List<UserWorkContact> createExpectedResults(String... urids) {
        List<UserWorkContact> userWorkContacts = new ArrayList<>();
        for (String urid: urids) {
            UserWorkContact contact = new UserWorkContact();
            contact.setUrid(urid);
            userWorkContacts.add(contact);
        }
        return userWorkContacts;
    }
}