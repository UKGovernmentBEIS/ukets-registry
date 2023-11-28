package gov.uk.ets.registry.api.common.publication;

import gov.uk.ets.commons.logging.MDCWrapper;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Log4j2
public class PublicationRequestAddRemoveRoleService {

    @Value("${publication.api.endpoint}")
    private String publicationApiEndpoint;
    private final RestTemplate restTemplate;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    public void requestPublicationApiAddRole(String userId) {
        HttpEntity<String> request = createRequest(userId);
        restTemplate.postForEntity(publicationApiEndpoint + "/roles.add", request, String.class);
    }

    public void requestPublicationApiRemoveRole(String userId) {
        HttpEntity<String> request = createRequest(userId);
        restTemplate.postForEntity(publicationApiEndpoint + "/roles.remove", request, String.class);
    }

    private HttpEntity<String> createRequest(
            String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "bearer " + serviceAccountAuthorizationService.obtainAccessToken().getToken());
        headers.set("X-Request-ID",
                MDC.getMDCAdapter().get(MDCWrapper.Attr.INTERACTION_IDENTIFIER.name().toLowerCase()));

        return new HttpEntity<>(userId, headers);
    }
}
