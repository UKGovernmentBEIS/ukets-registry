package gov.uk.ets.publication.api;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import gov.uk.ets.publication.api.authz.AuthorizationService;
import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.model.SectionType;
import gov.uk.ets.publication.api.service.SectionService;
import gov.uk.ets.publication.api.sort.SortParameters;
import gov.uk.ets.publication.api.web.model.FileInfoDto;
import gov.uk.ets.publication.api.web.model.ReportFileDto;
import gov.uk.ets.publication.api.web.model.SectionDto;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api-publication")
@RequiredArgsConstructor
public class PublicationController {

    private final SectionService sectionService;
    private final AuthorizationService authorizationService;

    /**
     * Retrieves all publication sections.
     */
    @GetMapping(path = "sections.list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SectionDto>> getSections(@NotNull @RequestParam SectionType sectionType) {
        return ResponseEntity.ok(sectionService.getSections(sectionType));
    }
    
    /**
     * Retrieves info for a specific publication section.
     */
    @GetMapping(value = "sections.get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SectionDto> getSection(@RequestParam Long id) {
        return ResponseEntity.ok(sectionService.getSection(id));
    }
    
    /**
     * Updates details of a specific publication section.
     */
    @PostMapping(value = "sections.update")
    public ResponseEntity<Long> update(@RequestBody @Valid SectionDto request) {
        return ResponseEntity.ok(sectionService.updateSection(request));
    }
    
    /**
     * Retrieves files published in a specific section.
     */
    @GetMapping(value = "sections.list-files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReportFileDto>> getFiles(@RequestParam Long id, SortParameters sortParams) {
        return ResponseEntity.ok(sectionService.getFiles(id, sortParams));
    }
    
    /**
     * Uploads a file to be published in a specific section.
     */
    @PostMapping(value = "sections.upload-file", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileInfoDto> uploadFile(
            @RequestPart(name = "file") MultipartFile multipartFile, DisplayType displayType) {
        return ResponseEntity.ok(sectionService.uploadFile(multipartFile, displayType));
    }

    /**
     * Uploads a file to be published in a specific section.
     */
    @PostMapping(value = "sections.submit-file", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> submitFile(@RequestBody FileInfoDto fileInfoDto) {
        sectionService.submitFile(fileInfoDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * Sets a published file to unpublished.
     */
    @PostMapping(value = "sections.unpublish-file")
    public ResponseEntity<Long> unpublishFile(@RequestBody ReportFileDto file) {
        return ResponseEntity.ok(sectionService.unpublishFile(file));
    }
    
    /**
     * Downloads a specific published file.
     */
    @GetMapping(value = "sections.download-file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> downloadFile(@RequestParam Long id) {
        ReportFileDto file = sectionService.downloadFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION)
                .header(CONTENT_DISPOSITION,
                    ContentDisposition.builder("attachment").filename(file.getFileName()).build().toString())
                .body(file.getData());
    }

    /**
     * Adds role to the user with the selected userId.
     */
    @PostMapping(path = "roles.add")
    @ResponseStatus(HttpStatus.OK)
    public void addRole(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                        @RequestBody @Valid String urid) {
        authorizationService.userCanRequestRoleChange(token);
        authorizationService.addUserRole(token, urid);
    }

    /**
     * Removes role from the user with the selected userId.
     */
    @PostMapping(path = "roles.remove")
    @ResponseStatus(HttpStatus.OK)
    public void removeRole(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                           @RequestBody @Valid String urid) {
        authorizationService.userCanRequestRoleChange(token);
        authorizationService.removeUserRole(token, urid);
    }
}
