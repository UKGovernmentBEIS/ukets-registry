package gov.uk.ets.reports.api;

import static gov.uk.ets.commons.logging.RequestParamType.*;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.RequestParamType;
import gov.uk.ets.reports.api.authz.AuthorizationService;
import gov.uk.ets.reports.api.roleaccess.service.ReportTypesPerRoleService;
import gov.uk.ets.reports.api.web.model.ReportAddRemoveRoleRequest;
import gov.uk.ets.reports.api.web.model.ReportCreationRequest;
import gov.uk.ets.reports.api.web.model.ReportCreationResponse;
import gov.uk.ets.reports.api.web.model.ReportDto;
import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportType;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-reports")
@RequiredArgsConstructor
public class ReportController {

    private final AuthorizationService authorizationService;

    public static final String SPREADSHEET_MEDIA_TYPE =
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ReportService reportService;
    private final ReportTypesPerRoleService reportTypesPerRoleService;

    /**
     * Generates a report request on behalf of the current user.
     */
    @PostMapping(path = "reports.request")
    public ResponseEntity<ReportCreationResponse> requestReport(@RequestBody @Valid @MDCParam(DTO) ReportCreationRequest request) {
        authorizationService.checkUserAccessToReports(request.getRequestingRole(), request.getType());
        ReportCreationResponse reportCreationResponse =
                reportService.requestReport(request, authorizationService.getCurrentUserUrid());
        return ResponseEntity.ok(reportCreationResponse);
    }

    @PostMapping(path = "reports.request.from.client")
    public ResponseEntity<ReportCreationResponse> requestReportFromClient(
        @RequestBody @Valid ReportCreationRequest request) {
        ReportCreationResponse reportCreationResponse =
            reportService.requestReport(request, request.getRequesterUrid());
        return ResponseEntity.ok(reportCreationResponse);
    }

    /**
     * Retrieves all reports metadata.
     */
    @GetMapping(path = "reports.list")
    public ResponseEntity<List<ReportDto>> getReports(@RequestParam @MDCParam(RequestParamType.URID) String urid,
            @RequestParam(required = false) ReportRequestingRole role) {
        authorizationService.checkUserAccessToReports(role, null);
        return ResponseEntity.ok(reportService.getReports(urid, role));
    }

    /**
     * Retrieves specific report metadata and its content.
     */
    @GetMapping(path = "reports.download", produces = SPREADSHEET_MEDIA_TYPE)
    public ResponseEntity<byte[]> downloadReport(@QueryParam("reportId") @MDCParam(RequestParamType.REPORT_ID) Long reportId) {
        ReportDto report = reportService.downloadReport(reportId);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION)
            .header(CONTENT_DISPOSITION,
                ContentDisposition.builder("attachment").filename(report.getFileName()).build().toString())
            .body(report.getData());
    }
    
    /**
     * Retrieves all report types that are available to the requesting role.
     */
    @GetMapping(path = "reports.list.eligible-types")
    public ResponseEntity<List<ReportType>> getReportTypes(@RequestParam ReportRequestingRole role) {
        authorizationService.checkUserAccessToReports(role, null);
        return ResponseEntity.ok(reportTypesPerRoleService.getReportTypes(role));
    }

    /**
     * Adds role to the user with the selected userId.
     */
    @PostMapping(path = "roles.add")
    @ResponseStatus(HttpStatus.OK)
    public void addRole(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, 
            @RequestBody @Valid @MDCParam(DTO) ReportAddRemoveRoleRequest request) {
        authorizationService.userCanRequestRoleChange(token, request.getRequestingRole());
        authorizationService.addUserRole(token, request.getUserId());
    }
    
    /**
     * Removes role from the user with the selected userId.
     */
    @PostMapping(path = "roles.remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeRole(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, 
            @RequestBody @Valid @MDCParam(DTO) ReportAddRemoveRoleRequest request) {
        authorizationService.userCanRequestRoleChange(token, request.getRequestingRole());
        authorizationService.removeUserRole(token, request.getUserId());
    }
}
