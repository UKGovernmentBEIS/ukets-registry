package gov.uk.ets.registry.api.document.management.service;

import gov.uk.ets.registry.api.document.management.domain.Document;
import gov.uk.ets.registry.api.document.management.domain.DocumentCategory;
import gov.uk.ets.registry.api.document.management.mapper.DocumentMapper;
import gov.uk.ets.registry.api.document.management.repository.DocumentCategoryRepository;
import gov.uk.ets.registry.api.document.management.repository.DocumentRepository;
import gov.uk.ets.registry.api.document.management.web.model.DocumentCategoryDTO;
import gov.uk.ets.registry.api.document.management.web.model.DocumentDTO;
import gov.uk.ets.registry.api.document.management.web.model.SaveDocumentDTO;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DocumentService {

    private final DocumentCategoryRepository documentCategoryRepository;
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final DocumentUtils utils;

    /**
     * Return all document categories sorted by position.
     *
     * @return all document categories
     */
    public List<DocumentCategoryDTO> getDocumentCategories() {
        List<DocumentCategory> categories = documentCategoryRepository.findAllByOrderByPosition();
        return categories.stream().map(documentMapper::toDto).toList();
    }

    /**
     * Add the document category.
     *
     * @param dto category dto
     * @return id of the category
     */
    @Transactional
    public Long addDocumentCategory(DocumentCategoryDTO dto) {

        List<DocumentCategory> categories = documentCategoryRepository.findAll();

        DocumentCategory category = new DocumentCategory();
        categories.add(category);

        category.setName(dto.getName());
        validateCategoriesForDuplicates(categories);

        category.setPosition(dto.getOrder());

        Date now = new Date();
        category.setCreatedOn(now);
        category.setUpdatedOn(now);
        updateCategoriesOrder(categories);

        return documentCategoryRepository.save(category).getId();
    }

    /**
     * Update the document category.
     *
     * @param dto category dto
     * @return category dto
     */
    @Transactional
    public DocumentCategoryDTO updateDocumentCategory(DocumentCategoryDTO dto) {

        List<DocumentCategory> categories = documentCategoryRepository.findAll();

        DocumentCategory category = categories.stream()
            .filter(c -> Objects.equals(c.getId(), dto.getId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Category does not exist."));

        category.setName(dto.getName());
        validateCategoriesForDuplicates(categories);

        category.setPosition(dto.getOrder());
        category.setUpdatedOn(new Date());
        updateCategoriesOrder(categories);

        DocumentCategory saved = documentCategoryRepository.save(category);
        return documentMapper.toDto(saved);
    }

    /**
     * Delete document category.
     *
     * @param id category id
     */
    @Transactional
    public void deleteDocumentCategory(Long id) {
        List<Document> documents = documentCategoryRepository.findById(id)
            .map(DocumentCategory::getDocuments)
            .orElse(List.of());

        if (!documents.isEmpty()) {
            throw new IllegalArgumentException("Category contains documents.");
        }

        documentCategoryRepository.deleteById(id);
    }

    /**
     * Return document.
     *
     * @return document
     */
    public Document getDocument(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Document does not exist."));
    }

    /**
     * Add the document.
     *
     * @param dto document dto
     * @return document id
     * @throws IOException
     */
    @Transactional
    public Long addDocument(SaveDocumentDTO dto) throws IOException {

        validateNotNull(dto.getTitle(), "Title does not exist.");
        validateNotNull(dto.getFile(), "File does not exist.");
        validateNotNull(dto.getOrder(), "Order does not exist.");

        DocumentCategory documentCategory = documentCategoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category does not exist."));
        List<Document> documents = documentCategory.getDocuments();

        Document document = new Document();
        document.setCategory(documentCategory);
        documents.add(document);

        document.setTitle(dto.getTitle());
        document.setName(dto.getFile().getOriginalFilename());
        validateDocumentsForDuplicates(documents);

        document.setData(dto.getFile().getBytes());
        document.setPosition(dto.getOrder());

        Date now = new Date();
        document.setCreatedOn(now);
        document.setUpdatedOn(now);

        updateDocumentsOrder(documents);

        return documentRepository.save(document).getId();
    }

    /**
     * Update the document.
     *
     * @param dto document dto
     * @return document dto
     * @throws IOException
     */
    @Transactional
    public DocumentDTO updateDocument(SaveDocumentDTO dto) throws IOException {

        List<Document> documents = documentRepository.findAllInTheSameCategory(dto.getId());
        Document document = documents.stream()
                .filter(d -> d.getId().equals(dto.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Document does not exist."));

        if (Objects.nonNull(dto.getTitle())) {
            document.setTitle(dto.getTitle());
            validateDocumentsForDuplicates(documents);
        }

        if (Objects.nonNull(dto.getFile())) {
            document.setName(dto.getFile().getOriginalFilename());
            document.setData(dto.getFile().getBytes());
        }

        document.setUpdatedOn(new Date());

        if (Objects.nonNull(dto.getOrder())) {
            document.setPosition(dto.getOrder());
            updateDocumentsOrder(documents);
        }

        Document saved = documentRepository.save(document);
        return documentMapper.toDto(saved);
    }

    /**
     * Delete document.
     *
     * @param id document id
     */
    @Transactional
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }

    private void validateNotNull(Object obj, String message) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateCategoriesForDuplicates(List<DocumentCategory> categories) {
        if (utils.hasDuplicates(categories, DocumentCategory::getName)) {
            throw new IllegalArgumentException("Category name already exists.");
        }
    }

    private void validateDocumentsForDuplicates(List<Document> documents) {
        if (utils.hasDuplicates(documents, Document::getTitle)) {
            throw new IllegalArgumentException("Document title already exists.");
        }
    }

    private void updateDocumentsOrder(List<Document> list) {
        utils.updateOrdering(list, Document::getPosition, Document::getUpdatedOn, Document::setPosition);
    }

    private void updateCategoriesOrder(List<DocumentCategory> list) {
        utils.updateOrdering(list, DocumentCategory::getPosition, DocumentCategory::getUpdatedOn, DocumentCategory::setPosition);
    }
}
