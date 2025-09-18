package gov.uk.ets.registry.api.document.management.service;

import static org.mockito.ArgumentMatchers.any;

import gov.uk.ets.registry.api.document.management.domain.Document;
import gov.uk.ets.registry.api.document.management.domain.DocumentCategory;
import gov.uk.ets.registry.api.document.management.mapper.DocumentMapper;
import gov.uk.ets.registry.api.document.management.repository.DocumentCategoryRepository;
import gov.uk.ets.registry.api.document.management.repository.DocumentRepository;
import gov.uk.ets.registry.api.document.management.web.model.DocumentCategoryDTO;
import gov.uk.ets.registry.api.document.management.web.model.DocumentDTO;
import gov.uk.ets.registry.api.document.management.web.model.SaveDocumentDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

class DocumentServiceTest {

    @Mock
    private DocumentCategoryRepository documentCategoryRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private DocumentMapper documentMapper;
    @Mock
    private DocumentUtils utils;

    private DocumentService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new DocumentService(documentCategoryRepository, documentRepository, documentMapper, utils);
    }

    @Test
    void testGetDocumentCategories() {
        // given
        DocumentCategory domain = new DocumentCategory();
        DocumentCategoryDTO dto = new DocumentCategoryDTO();
        Mockito.when(documentCategoryRepository.findAllByOrderByPosition()).thenReturn(List.of(domain));
        Mockito.when(documentMapper.toDto(domain)).thenReturn(dto);

        // when
        List<DocumentCategoryDTO> result = service.getDocumentCategories();

        // then
        Assertions.assertEquals(List.of(dto), result);
    }

    @Test
    void testAddDocumentCategories() {
        // given
        DocumentCategoryDTO dto = new DocumentCategoryDTO();

        Mockito.when(documentCategoryRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(utils.hasDuplicates(any(), any())).thenReturn(false);
        DocumentCategory category = new DocumentCategory();
        category.setId(1L);
        Mockito.when(documentCategoryRepository.save(any())).thenReturn(category);

        // when
        Long result = service.addDocumentCategory(dto);

        // then
        Assertions.assertEquals(1L, result);
        Mockito.verify(utils, Mockito.times(1)).updateOrdering(any(), any(), any(), any());
    }

    @Test
    void testAddDocumentCategoriesWithDuplicates() {
        // given
        DocumentCategoryDTO dto = new DocumentCategoryDTO();
        Mockito.when(documentCategoryRepository.findAll()).thenReturn(Stream.of(new DocumentCategory()).collect(Collectors.toList()));
        Mockito.when(utils.hasDuplicates(any(), any())).thenReturn(true);

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addDocumentCategory(dto), "Category name already exists.");
    }

    @Test
    void testUpdateDocumentCategories() {
        // given
        DocumentCategoryDTO dto = new DocumentCategoryDTO();
        dto.setId(1L);

        DocumentCategory category = new DocumentCategory();
        category.setId(1L);
        List<DocumentCategory> categories = Stream.of(category).collect(Collectors.toList());

        Mockito.when(documentCategoryRepository.findAll()).thenReturn(categories);
        Mockito.when(utils.hasDuplicates(any(), any())).thenReturn(false);
        Mockito.when(documentCategoryRepository.save(category)).thenReturn(category);
        Mockito.when(documentMapper.toDto(category)).thenReturn(dto);

        // when
        DocumentCategoryDTO result = service.updateDocumentCategory(dto);

        // then
        Assertions.assertEquals(dto, result);
        Mockito.verify(utils, Mockito.times(1)).updateOrdering(any(), any(), any(), any());
    }

    @Test
    void testUpdateDocumentCategoriesWithDuplicates() {
        // given
        DocumentCategoryDTO dto = new DocumentCategoryDTO();
        dto.setId(1L);

        DocumentCategory category = new DocumentCategory();
        category.setId(1L);
        List<DocumentCategory> categories = Stream.of(category).collect(Collectors.toList());

        Mockito.when(documentCategoryRepository.findAll()).thenReturn(categories);
        Mockito.when(utils.hasDuplicates(any(), any())).thenReturn(true);

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateDocumentCategory(dto), "Category name already exists.");
    }

    @Test
    void testUpdateDocumentCategoriesNotExists() {
        // given
        DocumentCategoryDTO dto = new DocumentCategoryDTO();
        dto.setId(1L);

        List<DocumentCategory> categories = Stream.of(new DocumentCategory()).collect(Collectors.toList());

        Mockito.when(documentCategoryRepository.findAll()).thenReturn(categories);

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateDocumentCategory(dto), "Category does not exist.");
    }

    @Test
    void testDeleteDocumentCategory() {
        // given
        Mockito.when(documentCategoryRepository.findById(1L)).thenReturn(Optional.of(new DocumentCategory()));

        // when
        service.deleteDocumentCategory(1L);

        // then
        Mockito.verify(documentCategoryRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDocumentCategoryWithDocuments() {
        // given
        DocumentCategory category = new DocumentCategory();
        category.setDocuments(List.of(new Document()));
        Mockito.when(documentCategoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteDocumentCategory(1L), "Category contains documents.");
    }

    @Test
    void testGetDocument() {
        // given
        Document document = new Document();
        Mockito.when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        // when
        Document result = service.getDocument(1L);

        // then
        Assertions.assertEquals(document, result);
    }

    @Test
    void testGetDocumentNotExists() {
        // given
        Mockito.when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.getDocument(1L), "Document does not exist.");
    }

    @Test
    void testAddDocument() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setOrder(1);
        dto.setTitle("Title");
        dto.setFile(new MockMultipartFile("FileName", new byte[0]));
        dto.setCategoryId(1L);

        Mockito.when(documentCategoryRepository.findById(1L)).thenReturn(Optional.of(new DocumentCategory()));
        Mockito.when(utils.hasDuplicates(any(), any())).thenReturn(false);
        Document document = new Document();
        document.setId(1L);
        Mockito.when(documentRepository.save(any())).thenReturn(document);

        // when
        Long result = service.addDocument(dto);

        // then
        Assertions.assertEquals(1L, result);
        Mockito.verify(utils, Mockito.times(1)).updateOrdering(any(), any(), any(), any());
    }

    @Test
    void testAddDocumentWithoutTitle() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addDocument(dto), "Title does not exist.");
    }

    @Test
    void testAddDocumentWithoutFile() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setTitle("title");

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addDocument(dto), "File does not exist.");
    }

    @Test
    void testAddDocumentWithoutOrder() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setTitle("title");
        dto.setFile(new MockMultipartFile("FileName", new byte[0]));

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addDocument(dto), "Order does not exist.");
    }

    @Test
    void testAddDocumentCategoryNotExists() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setOrder(1);
        dto.setFile(new MockMultipartFile("FileName", new byte[0]));
        dto.setCategoryId(1L);

        Mockito.when(documentCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addDocument(dto), "Category does not exist.");
    }

    @Test
    void testAddDocumentWithDuplicates() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setOrder(1);
        dto.setFile(new MockMultipartFile("FileName", new byte[0]));
        dto.setCategoryId(1L);

        Mockito.when(documentCategoryRepository.findById(1L)).thenReturn(Optional.of(new DocumentCategory()));
        Mockito.when(utils.hasDuplicates(any(), any())).thenReturn(true);

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.addDocument(dto), "Document name already exists.");
    }

    @Test
    void testUpdateDocumentWithNewValues() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setOrder(1);
        dto.setFile(new MockMultipartFile("FileName", new byte[0]));
        dto.setId(1L);

        Document document = new Document();
        document.setId(1L);
        Mockito.when(documentRepository.findAllInTheSameCategory(1L)).thenReturn(Stream.of(document).collect(Collectors.toList()));
        Mockito.when(utils.hasDuplicates(any(), any())).thenReturn(false);

        Mockito.when(documentRepository.save(any())).thenReturn(document);
        DocumentDTO expected = new DocumentDTO();
        Mockito.when(documentMapper.toDto(document)).thenReturn(expected);

        // when
        DocumentDTO result = service.updateDocument(dto);

        // then
        Assertions.assertEquals(expected, result);
        Mockito.verify(utils, Mockito.times(1)).updateOrdering(any(), any(), any(), any());
    }

    @Test
    void testUpdateDocumentWithoutNewValues() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setId(1L);

        Document document = new Document();
        document.setId(1L);
        Mockito.when(documentRepository.findAllInTheSameCategory(1L)).thenReturn(Stream.of(document).collect(Collectors.toList()));

        Mockito.when(documentRepository.save(any())).thenReturn(document);
        DocumentDTO expected = new DocumentDTO();
        Mockito.when(documentMapper.toDto(document)).thenReturn(expected);

        // when
        DocumentDTO result = service.updateDocument(dto);

        // then
        Assertions.assertEquals(expected, result);
        Mockito.verify(utils, Mockito.times(0)).updateOrdering(any(), any(), any(), any());
        Mockito.verify(utils, Mockito.times(0)).hasDuplicates(any(), any());
    }

    @Test
    void testUpdateDocumentWithDuplicates() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setTitle("title");
        dto.setFile(new MockMultipartFile("FileName", new byte[0]));
        dto.setId(1L);

        Document document = new Document();
        document.setId(1L);
        Mockito.when(documentRepository.findAllInTheSameCategory(1L)).thenReturn(Stream.of(document).collect(Collectors.toList()));
        Mockito.when(utils.hasDuplicates(any(), any())).thenReturn(true);

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateDocument(dto), "Document title already exists.");
    }

    @Test
    void testUpdateDocumentNotExists() throws IOException {
        // given
        SaveDocumentDTO dto = new SaveDocumentDTO();
        dto.setId(1L);

        Document document = new Document();
        document.setId(2L);
        Mockito.when(documentRepository.findAllInTheSameCategory(1L)).thenReturn(Stream.of(document).collect(Collectors.toList()));

        // when
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateDocument(dto), "Document does not exist.");
    }

    @Test
    void testDeleteDocument() throws IOException {
        // when
        service.deleteDocument(1L);

        // then
        Mockito.verify(documentRepository, Mockito.times(1)).deleteById(1L);
    }
}
