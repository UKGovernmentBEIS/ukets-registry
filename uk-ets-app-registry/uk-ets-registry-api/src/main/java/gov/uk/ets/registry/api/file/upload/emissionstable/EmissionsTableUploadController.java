package gov.uk.ets.registry.api.file.upload.emissionstable;

import static gov.uk.ets.commons.logging.RequestParamType.DTO;
import static gov.uk.ets.commons.logging.RequestParamType.FILE_ID;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import java.util.List;

import gov.uk.ets.commons.logging.MDCParam;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.uk.ets.registry.api.auditevent.service.AuditEventService;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadActionError;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadActionException;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsTableRequestDto;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableUploadProcessor;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import lombok.RequiredArgsConstructor;

/**
 * The controller responsible for handling the upload and submit requests of the emissions table files.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api-registry", produces = APPLICATION_JSON_VALUE)
public class EmissionsTableUploadController {

    private final EmissionsTableUploadProcessor emissionsTableProcessor;
    private final AuditEventService auditEventService;

    /**
     * Handles the emissions table file upload.
     *
     * @param multipartFile the uploaded multipart file
     * @return a response entity containing the file header DTO object if successful
     */
    @PostMapping(path = "emissions-table.upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileHeaderDto> validateUploadedFile(
        @RequestPart(name = "file") MultipartFile multipartFile) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(emissionsTableProcessor.loadAndVerifyFileIntegrity(multipartFile));
    }

    /**
     * Handles the submission of the uploaded file, based on the file name.
     *
     * @param emissionsTableRequest the emission table request DTO object
     * @return a response entity containing the task request id if successful
     */
    @PostMapping(path = "emissions-table.submit", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> submitValidatedFile(
        @RequestBody @MDCParam(DTO) EmissionsTableRequestDto emissionsTableRequest) {
    	if(emissionsTableProcessor.validateOTP(emissionsTableRequest.getOtp())) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(emissionsTableProcessor.submitUploadedFile(emissionsTableRequest.getFileHeader()));
    	} else {                
    		throw EmissionsUploadActionException.create(EmissionsUploadActionError.builder()
                .code(EmissionsUploadActionError.INVALID_OTP_CODE)
                .componentId("otpCode")
                .message("Invalid OTP code.")
            .build());
    	}
    }

    /**
     * Retrieves all the audit events for emissions table.
     *
     * @return A list of events
     */
    @GetMapping(path = "/emissions-table.get.history", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditEventDTO>> getEmissionsTableHistory() {
        return new ResponseEntity<>(
            auditEventService.getAuditEventsByDomainTypeAndRequestTypeOrderByCreationDate(
                EventType.UPLOAD_EMISSIONS_TABLE.getClazz().getName(),RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST), HttpStatus.OK);
    }
    

    @GetMapping(path = "/emissions-table.get.errors", produces = APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getEmissionsTableErrors(@RequestParam(required = false, value = "fileId") @MDCParam(FILE_ID) Long fileId) {
        HttpHeaders headers = new HttpHeaders();
        UploadedFile file = emissionsTableProcessor.getEmissionsTableErrorsFile(fileId);
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(file.getFileName())
                .build().toString());
        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }
}
