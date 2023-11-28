package gov.uk.ets.registry.api.file.upload.requesteddocs;

import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.AccountHolderFileRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.UserFileRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsService;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

class RequestedDocsServiceTest {

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private UploadedFilesRepository uploadedFilesRepository;

    @Mock
    private AccountHolderRepository accountHolderRepository;

    @Mock
    private AccountHolderFileRepository accountHolderFileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFileRepository userFileRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private Mapper mapper;

    RequestedDocsService requestedDocsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        requestedDocsService = new RequestedDocsService(fileUploadService, uploadedFilesRepository, accountHolderRepository,
                accountHolderFileRepository, userRepository, userFileRepository, taskRepository, mapper);
    }

    @Test
    void test_submitUploadedFiles() {
        Long taskRequestId = 1004L;
        List<UploadedFile> notSubmittedFiles = new ArrayList<>();
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1L);
        uploadedFile.setCreationDate(LocalDateTime.now());
        uploadedFile.setFileStatus(FileStatus.NOT_SUBMITTED);
        uploadedFile.setFileSize("119.4 kB");
        notSubmittedFiles.add(uploadedFile);
        when(uploadedFilesRepository.findByTaskRequestId(taskRequestId)).thenReturn(notSubmittedFiles);

        Task task = new Task();
        when(taskRepository.findByRequestId(taskRequestId)).thenReturn(task);

        Assertions.assertEquals(FileStatus.NOT_SUBMITTED, notSubmittedFiles.get(0).getFileStatus());

        RequestDocumentsTaskDifference difference = new RequestDocumentsTaskDifference();
        Set<Long> documentIds = new HashSet<>();
        documentIds.add(uploadedFile.getId());
        difference.setDocumentIds(documentIds);
        List<String> documentNames = new ArrayList<>();
        documentNames.add("Test 1");
        difference.setDocumentNames(documentNames);
        requestedDocsService.submitUploadedFiles(taskRequestId, difference);

        Assertions.assertEquals(1L, notSubmittedFiles.size());
        Assertions.assertEquals(FileStatus.SUBMITTED, notSubmittedFiles.get(0).getFileStatus());
    }

    @Test
    void test_deleteOldTaskFiles() {
        Long taskRequestId = 1004L;
        List<UploadedFile> tobeDeleted = new ArrayList<>();
        UploadedFile uploadedFile1 = new UploadedFile();
        uploadedFile1.setId(1L);
        uploadedFile1.setCreationDate(LocalDateTime.now());
        uploadedFile1.setFileStatus(FileStatus.NOT_SUBMITTED);
        uploadedFile1.setFileSize("119.4 kB");
        tobeDeleted.add(uploadedFile1);
        when(uploadedFilesRepository.findByTaskRequestId(taskRequestId)).thenReturn(tobeDeleted);

        Set<Long> currentFileIds = new HashSet<>();
        currentFileIds.add(2L);

        requestedDocsService.deleteOldTaskFiles(taskRequestId, currentFileIds);

        Assertions.assertEquals(1L, tobeDeleted.size());
        Assertions.assertEquals(FileStatus.NOT_SUBMITTED, tobeDeleted.get(0).getFileStatus());
    }

    @Test
    void test_deleteTaskFilesByFileId() {
        UploadedFile uploadedFile1 = new UploadedFile();
        uploadedFile1.setId(1L);
        uploadedFile1.setCreationDate(LocalDateTime.now());
        uploadedFile1.setFileStatus(FileStatus.NOT_SUBMITTED);
        uploadedFile1.setFileSize("119.4 kB");
        when(uploadedFilesRepository.findById(1L)).thenReturn(Optional.of(uploadedFile1));

        requestedDocsService.deleteTaskFilesByFileId(1L);

        Assertions.assertEquals(1L, uploadedFile1.getId());
        Assertions.assertEquals(FileStatus.NOT_SUBMITTED, uploadedFile1.getFileStatus());
    }

    @Test
    void test_getUploadedFiles() {
        List<UploadedFile> uploadedFiles = new ArrayList<>();
        UploadedFile file = new UploadedFile();
        file.setFileName("Test file");
        file.setId(1L);
        file.setCreationDate(LocalDateTime.now());
        uploadedFiles.add(file);
        when(uploadedFilesRepository.findByTaskRequestId(1L)).thenReturn(uploadedFiles);

        List<FileHeaderDto> results = requestedDocsService.getUploadedFiles(1L);
        Assertions.assertFalse(CollectionUtils.isEmpty(results));
        Assertions.assertEquals("Test file",results.get(0).getFileName());
    }
}
