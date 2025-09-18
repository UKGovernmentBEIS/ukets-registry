package gov.uk.ets.registry.api.file.upload.requesteddocs;

import static gov.uk.ets.commons.logging.RequestParamType.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsService;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api-registry", produces = APPLICATION_JSON_VALUE)
public class RequestedDocsController {

    private final RequestedDocsService requestedDocsService;

    /**
     * Handles the actual requested document file upload.
     *
     * @param requestedDocsParams the requested document file parameters
     * @return a response entity containing the file header DTO object if successful
     */
    @PostMapping(path = "requested-document.upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> validateUploadedFile(@MDCParam(DTO) RequestedDocsParams requestedDocsParams) {
        return new ResponseEntity<>(requestedDocsService.loadAndVerifyFileIntegrity(requestedDocsParams), HttpStatus.OK);
    }

    @DeleteMapping(path = "requested-document.delete")
    public ResponseEntity deleteUploadedFile(@QueryParam("fileId") @MDCParam(FILE_ID) Long fileId,
                                             @QueryParam("requestId") @MDCParam(TASK_REQUEST_ID) Long taskRequestId,
                                             @QueryParam("totalFileUploads") String totalFileUploads,
                                             @QueryParam("accountHolderIdentifier") String accountHolderIdentifier,
                                             @QueryParam("userUrid") String userUrid) {
        requestedDocsService.deleteFileFromTask(fileId, taskRequestId, totalFileUploads, accountHolderIdentifier, userUrid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
