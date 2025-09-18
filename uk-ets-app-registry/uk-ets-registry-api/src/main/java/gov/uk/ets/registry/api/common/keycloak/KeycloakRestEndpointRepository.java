package gov.uk.ets.registry.api.common.keycloak;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import gov.uk.ets.commons.logging.MDCWrapper;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Generic Rest endpoint repository
 * @param <T>
 */
@AllArgsConstructor
public class KeycloakRestEndpointRepository<T> {
    protected RestTemplate restTemplate;
    protected String endpoint;

    protected T fetchData(Map<String, ?> queryParamsMap, String token, Class<T> resultType) {
        UriComponentsBuilder builder = createURI(queryParamsMap);

        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        HttpEntity<T> response = restTemplate.exchange(builder.build().toUriString(), HttpMethod.GET, request, resultType);

        return response.getBody();
    }

    protected List<T> fetchDataList(Map<String, ?> queryParamsMap, String token, ParameterizedTypeReference<List<T>> requestType) {
        UriComponentsBuilder builder = createURI(queryParamsMap);
        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        return restTemplate.exchange(builder.build().toUriString(), HttpMethod.GET, request, requestType).getBody();
    }

    protected T fetchDataById(String id, String token, Class<T> resultType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);
        HttpEntity<?> request = new HttpEntity<>(generateHeaders(token));

        HttpEntity<T> response = restTemplate.exchange(builder.toUriString() + "/" + id, HttpMethod.GET, request, resultType);

        return response.getBody();
    }

    protected HttpStatusCode update(String token, Class<T> resultType, T object, String id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);
        HttpEntity<?> request = new HttpEntity<>(object, generateHeaders(token));

        ResponseEntity<T> response = restTemplate.exchange(builder.toUriString()+"/"+id, HttpMethod.PUT, request, resultType);

        return response.getStatusCode();
    }

    protected HttpHeaders generateHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "bearer " + token);
        headers.set("X-Request-ID",
                MDC.getMDCAdapter().get(MDCWrapper.Attr.INTERACTION_IDENTIFIER.name().toLowerCase()));
        return headers;
    }

    protected UriComponentsBuilder createURI(Map<String, ?> queryParamsMap) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(endpoint);
        queryParamsMap.forEach((key, value) -> {
            if (value != null) {
                if (value instanceof Collection) {
                    List<String> values = ((Collection<?>) value).stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                        .toList();
                    builder.queryParam(key, values);
                } else {
                    builder.queryParam(key, URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
                }
            }
        });
        return builder;
    }
}
