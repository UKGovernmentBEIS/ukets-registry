package gov.uk.ets.registry.api.file.upload.requesteddocs;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.UserStatusEnrolledRule;
import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestDTO;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestDocumentsWizardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api-registry", produces = APPLICATION_JSON_VALUE)
public class RequestDocumentsWizardController {
    private final RequestDocumentsWizardService service;

    @Protected({
        UserStatusEnrolledRule.class
    })
    @PostMapping(path = "documents-request.submit", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> submitDocumentsRequest(@RequestBody DocumentsRequestDTO body) {
        Long taskId = service.submitDocumentsRequest(body);
        return new ResponseEntity<>(taskId, HttpStatus.OK);
    }
}
