package gov.uk.ets.registry.api.file.upload.bulkar;

import static gov.uk.ets.commons.logging.RequestParamType.DTO;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.UserStatusEnrolledRule;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.services.FileUploadProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api-registry", produces = APPLICATION_JSON_VALUE)
public class BulkArUploadController {

    private final FileUploadProcessor bulkArProcessor;

    /**
     * Handles the bulk AR file upload.
     *
     * @param multipartFile the uploaded multipart file
     * @return a response entity containing the file header DTO object if successful
     */
    @Protected(UserStatusEnrolledRule.class)
    @PostMapping(path = "bulk-ar.upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileHeaderDto> validateUploadedFile(
        @RequestPart(name = "file") MultipartFile multipartFile) {
        return ResponseEntity.status(HttpStatus.OK).body(bulkArProcessor.loadAndVerifyFileIntegrity(multipartFile));
    }

    /**
     * Handles the submission of the uploaded file, based on the file name.
     *
     * @param fileHeaderDto the file header DTO object
     * @return a response entity containing the task request id if successful
     */
    @PostMapping(path = "bulk-ar.submit", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> submitValidatedFile(
        @RequestBody @MDCParam(DTO) FileHeaderDto fileHeaderDto) {
        return ResponseEntity.status(HttpStatus.OK).body(bulkArProcessor.submitUploadedFile(fileHeaderDto));
    }
}
