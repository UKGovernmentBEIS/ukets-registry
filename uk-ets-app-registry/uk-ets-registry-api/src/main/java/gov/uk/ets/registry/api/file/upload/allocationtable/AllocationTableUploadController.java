package gov.uk.ets.registry.api.file.upload.allocationtable;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.RequestParamType;
import gov.uk.ets.registry.api.auditevent.service.AuditEventService;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.services.FileUploadProcessor;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * The controller responsible for handling the upload and submit requests of the allocation table files.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
public class AllocationTableUploadController {

    private final FileUploadProcessor allocationTableProcessor;
    private final AuditEventService auditEventService;

    /**
     * Handles the allocation table file upload.
     *
     * @param multipartFile the uploaded multipart file
     * @return a response entity containing the file header DTO object if successful
     */
    @PostMapping(path = "allocation-table.upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileHeaderDto> validateUploadedFile(
        @RequestPart(name = "file") MultipartFile multipartFile) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(allocationTableProcessor.loadAndVerifyFileIntegrity(multipartFile));
    }

    /**
     * Handles the submission of the uploaded file, based on the file name.
     *
     * @param fileHeaderDto the file header DTO object
     * @return a response entity containing the task request id if successful
     */
    @PostMapping(path = "allocation-table.submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> submitValidatedFile(
        @RequestBody @MDCParam(RequestParamType.DTO) FileHeaderDto fileHeaderDto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(allocationTableProcessor.submitUploadedFile(fileHeaderDto));
    }

    /**
     * Retrieves all the audit events for allocation table.
     *
     * @return A list of events
     */
    @GetMapping(path = "/allocation-table.get.history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditEventDTO>> getAllocationTableHistory() {
        List<RequestType> requestTypes =
            List.of(RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST, RequestType.ALLOCATION_REQUEST);
        return new ResponseEntity<>(
            auditEventService.getAuditEventsByDomainTypeAndRequestTypesAndOptionalUserDetailsOrderByCreationDate(
                EventType.UPLOAD_ALLOCATION_TABLE.getClazz().getName(), requestTypes), HttpStatus.OK);
    }

    @GetMapping(path = "/allocation-table.get.errors", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getAllocationTableErrors(@QueryParam("reportId") @MDCParam(RequestParamType.FILE_ID)
                                                                   Long fileId) {
        HttpHeaders headers = new HttpHeaders();
        UploadedFile file = allocationTableProcessor.getUploadedFileErrors(fileId);
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
                    ContentDisposition.builder("attachment").filename(file.getFileName())
                                      .build().toString());
        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }
}
