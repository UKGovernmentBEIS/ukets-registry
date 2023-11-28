package gov.uk.ets.registry.api.common.reports;

import gov.uk.ets.commons.logging.MDCWrapper;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.reports.model.ReportRequestingRole;
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
public class ReportRequestAddRemoveRoleService {

    @Value("${reports.api.roles.add.endpoint}")
    private String reportsApiRequestRolesAddEndpoint;
    @Value("${reports.api.roles.remove.endpoint}")
    private String reportsApiRequestRolesRemoveEndpoint;
    private final RestTemplate restTemplate;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    public void requestReportsApiAddRole(String userId) {
        HttpEntity<ReportAddRemoveRoleRequest> request = createRequest(userId);
        restTemplate.postForEntity(reportsApiRequestRolesAddEndpoint, request, String.class);
    }
    
    public void requestReportsApiRemoveRole(String userId) {
        HttpEntity<ReportAddRemoveRoleRequest> request = createRequest(userId);
        restTemplate.postForEntity(reportsApiRequestRolesRemoveEndpoint, request, String.class);
    }

    private HttpEntity<ReportAddRemoveRoleRequest> createRequest(
            String userId) {
        ReportAddRemoveRoleRequest reportAddRemoveRoleRequest = ReportAddRemoveRoleRequest.builder()
                .requestingRole(ReportRequestingRole.administrator)
                .userId(userId)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "bearer " + serviceAccountAuthorizationService.obtainAccessToken().getToken());
        headers.set("X-Request-ID",
             MDC.getMDCAdapter().get(MDCWrapper.Attr.INTERACTION_IDENTIFIER.name().toLowerCase()));

        HttpEntity<ReportAddRemoveRoleRequest> request = new HttpEntity<>(reportAddRemoveRoleRequest, headers);
        return request;
    }
}
