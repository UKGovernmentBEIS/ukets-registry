package gov.uk.ets.registry.api.file.upload.services;

import gov.uk.ets.file.upload.services.ClamavService;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class FileUploadServiceTest {

    @Mock
    private UploadedFilesRepository uploadedFilesRepository;

    @Mock
    private ConversionService conversionService;

    @Mock
    private ClamavService clamavService;

    @Mock
    private EventService eventService;

    @Mock
    private Mapper mapper;

    private final LocalDateTime now = LocalDateTime.of(LocalDate.of(2021, 4, 8), LocalTime.of(13, 25));

    FileUploadService fileUploadService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        fileUploadService = new FileUploadService(uploadedFilesRepository, conversionService, clamavService, eventService, mapper);
        ReflectionTestUtils.setField(fileUploadService,"deleteSubmittedDocumentsOlderThanXDays",60L);
        ReflectionTestUtils.setField(fileUploadService,"deleteNotSubmittedDocumentsOlderThanXDays",1L);
    }

    @DisplayName("Delete submitted files with parents successfully.")
    @Test
    void test_deleteSubmittedFilesWithParents() {

        List<UploadedFile> filesWithParentTask = new ArrayList<>();

        Task parentTask = new Task();
        parentTask.setRequestId(100000L);
        parentTask.setType(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST);
        parentTask.setCompletedDate(Timestamp.valueOf(now.minusDays(61)));
        parentTask.setStatus(RequestStateEnum.APPROVED);

        Task taskWithParent = new Task();
        taskWithParent.setRequestId(100002L);
        taskWithParent.setType(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
        taskWithParent.setStatus(RequestStateEnum.APPROVED);
        taskWithParent.setCompletedDate(Timestamp.valueOf(now.minusDays(65)));
        taskWithParent.setParentTask(parentTask);

        UploadedFile uploadedFile2 = new UploadedFile();
        uploadedFile2.setId(2L);
        uploadedFile2.setTask(taskWithParent);
        uploadedFile2.setFileName("Test Document Name 2");
        uploadedFile2.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile2.setCreationDate(now.minusDays(65));
        filesWithParentTask.add(uploadedFile2);

        when(uploadedFilesRepository.findFilesForCompletedTasksWithParent(any(), any()))
                .thenReturn(filesWithParentTask);

        fileUploadService.deleteSubmittedFiles();

        then(uploadedFilesRepository).should(times(1)).deleteAll(filesWithParentTask);

        verify(eventService, times(1))
	        .createAndPublishEvent("100002", null,
	                "Test Document Name 2 with ID 2",
	                EventType.TASK_DELETE_SUBMITTED_DOCUMENT, "Document automatically deleted");
    }
    
	@DisplayName("Files with no parents should not be deleted automatically.")
	@Test
	void test_doNotDeleteSubmittedFilesWithNoParents() {
		
		fileUploadService.deleteSubmittedFiles();
		
		verify(uploadedFilesRepository, times(0)).findFilesForCompletedTasksWithNoParent(any(), any());
		
        verify(eventService, times(0))
	        .createAndPublishEvent(any(), any(),
	        		any(),
	        		eq(EventType.TASK_DELETE_SUBMITTED_DOCUMENT), eq("Document automatically deleted"));
	}

    @DisplayName("Files that are not submitted will be deleted automatically.")
    @Test
    void test_deleteNotSubmittedFiles() {

        // given
        String difference = "difference";
        String expectedDifference = "Result";
        Set<Long> documentIds = Stream.of(11L, 22L).collect(Collectors.toSet());
        HashMap<String, Long> uploadedFileNameIdMap = new HashMap<>() {{
            put("111", 11L);
            put("222", 22L);
        }};

        Task task = new Task();
        task.setType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        task.setRequestId(123456L);
        task.setDifference(difference);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(11L);
        uploadedFile.setFileStatus(FileStatus.NOT_SUBMITTED);
        uploadedFile.setTask(task);

        List<UploadedFile> uploadedFiles = List.of(uploadedFile);

        when(uploadedFilesRepository.findByFileStatusEqualsAndCreationDateIsBefore(eq(FileStatus.NOT_SUBMITTED), any(LocalDateTime.class)))
            .thenReturn(uploadedFiles);

        RequestDocumentsTaskDifference requestDocumentsTaskDifference = new RequestDocumentsTaskDifference();
        requestDocumentsTaskDifference.setDocumentIds(documentIds);
        requestDocumentsTaskDifference.setUploadedFileNameIdMap(uploadedFileNameIdMap);

        when(mapper.convertToPojo(difference, RequestDocumentsTaskDifference.class))
            .thenReturn(requestDocumentsTaskDifference);

        when(mapper.convertToJson(any())).thenReturn(expectedDifference);

        // when
        fileUploadService.deleteNotSubmittedFiles();

        // then
        verify(uploadedFilesRepository, times(1)).deleteAll(uploadedFiles);
        assertEquals(expectedDifference, task.getDifference());
    }
}
