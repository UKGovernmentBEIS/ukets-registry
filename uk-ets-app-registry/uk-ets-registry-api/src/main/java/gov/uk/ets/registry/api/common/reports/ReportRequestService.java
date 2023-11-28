package gov.uk.ets.registry.api.common.reports;

import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.reports.model.ReportQueryInfo;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportType;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Log4j2
public class ReportRequestService {

    @Value("${reports.api.request.endpoint}")
    private String reportsApiRequestEndpoint;
    private final RestTemplate restTemplate;
    private final UserService userService;

    public Long requestReport(ReportType reportType, ReportRequestingRole reportRequestingRole, 
    		ReportQueryInfo reportQueryInfo, String bearerToken) {
        ReportCreationRequest reportCreationRequest = ReportCreationRequest.builder()
            .requesterUrid(userService.getCurrentUser().getUrid())
            .type(reportType)
            .requestingRole(reportRequestingRole)
            .queryInfo(reportQueryInfo)
            .build();

        HttpHeaders headers = new HttpHeaders();
        // TODO for the moment endpoint is public
        if (bearerToken == null) {
            bearerToken = "";
        }
        log.info(bearerToken.replaceAll("[\r\n]", ""));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ReportCreationRequest> request = new HttpEntity<>(reportCreationRequest);

        ResponseEntity<ReportCreationResponse> response =
            restTemplate.postForEntity(reportsApiRequestEndpoint, request, ReportCreationResponse.class);
        return Objects.requireNonNull(response.getBody()).getReportId();
    }
}
