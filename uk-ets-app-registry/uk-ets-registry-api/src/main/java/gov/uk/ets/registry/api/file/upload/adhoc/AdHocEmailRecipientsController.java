package gov.uk.ets.registry.api.file.upload.adhoc;

import gov.uk.ets.registry.api.file.upload.adhoc.dto.AdhocNotificationFileDto;
import gov.uk.ets.registry.api.file.upload.adhoc.services.AdHocEmailRecipientsProcessor;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static gov.uk.ets.registry.api.file.upload.services.FileUploadService.ERROR_WHILE_PROCESSING_THE_FILE;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class AdHocEmailRecipientsController {

    private final AdHocEmailRecipientsProcessor adHocEmailRecipientsProcessor;
    private final FileUploadService fileUploadService;

    @PostMapping(value = "adhoc.recipients.upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdhocNotificationFileDto> create(
        @RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(adHocEmailRecipientsProcessor.loadAndVerifyFileIntegrity(multipartFile));
    }

    @GetMapping(path = "adhoc.recipients.download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadFile(@RequestParam Long fileId) {
        UploadedFile file = fileUploadService.findUploadedFileById(fileId).orElseThrow(
            () -> new FileUploadException(ERROR_WHILE_PROCESSING_THE_FILE));
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(file.getFileName())
                .build().toString());
        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }

}
