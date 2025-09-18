package gov.uk.ets.registry.api.document.management.controller;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.document.management.domain.Document;
import gov.uk.ets.registry.api.document.management.service.DocumentService;
import gov.uk.ets.registry.api.document.management.web.model.DocumentCategoryDTO;
import gov.uk.ets.registry.api.document.management.web.model.DocumentDTO;
import gov.uk.ets.registry.api.document.management.web.model.SaveDocumentDTO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api-registry")
@AllArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping(path = "/document-categories.get")
    public List<DocumentCategoryDTO> getDocumentCategories() {
        return documentService.getDocumentCategories();
    }

    @Protected({
        SeniorAdminRule.class
    })
    @PostMapping(path = "/document-categories.add")
    public Long addDocumentCategory(@RequestBody @Valid DocumentCategoryDTO dto) {
        return documentService.addDocumentCategory(dto);
    }

    @Protected({
        SeniorAdminRule.class
    })
    @PutMapping(path = "/document-categories.update")
    public DocumentCategoryDTO updateDocumentCategory(@RequestBody @Valid DocumentCategoryDTO dto) {
        return documentService.updateDocumentCategory(dto);
    }

    @Protected({
        SeniorAdminRule.class
    })
    @DeleteMapping(path = "/document-categories.delete/{categoryId}")
    public void deleteDocumentCategory(@PathVariable Long categoryId) {
        documentService.deleteDocumentCategory(categoryId);
    }

    @GetMapping(path = "/document.get/{documentId}")
    public ResponseEntity<byte[]> getDocument(@PathVariable Long documentId) {

        Document document = documentService.getDocument(documentId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(document.getName()).build().toString());

        return new ResponseEntity<>(document.getData(), headers, HttpStatus.OK);
    }

    @Protected({
        SeniorAdminRule.class
    })
    @PostMapping(path = "/document.add", consumes = MULTIPART_FORM_DATA_VALUE)
    public Long addDocument(@Valid SaveDocumentDTO dto) throws IOException {
        return documentService.addDocument(dto);
    }

    @Protected({
        SeniorAdminRule.class
    })
    @PatchMapping(path = "/document.update", consumes = MULTIPART_FORM_DATA_VALUE)
    public DocumentDTO updateDocument(@Valid SaveDocumentDTO dto) throws IOException {
        return documentService.updateDocument(dto);
    }

    @Protected({
        SeniorAdminRule.class
    })
    @DeleteMapping(path = "/document.delete/{documentId}")
    public void deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
    }
}
