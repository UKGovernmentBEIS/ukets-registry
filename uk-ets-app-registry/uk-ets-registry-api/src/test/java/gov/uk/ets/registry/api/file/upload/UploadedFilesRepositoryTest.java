package gov.uk.ets.registry.api.file.upload;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableExcelFilenameValidator;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableExcelFilenameValidator.EmissionsFilenameRegExpGroup;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import static org.junit.Assert.assertNotNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
class UploadedFilesRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    UploadedFilesRepository uploadedFilesRepository;

    private final LocalDateTime now = LocalDateTime.of(LocalDate.of(2021, 4, 8), LocalTime.of(13, 25));

    @BeforeEach
    void setUp() {
        /* No Parent Task case */
        Task taskNoParent = new Task();
        taskNoParent.setRequestId(100001L);
        taskNoParent.setType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD);
        taskNoParent.setStatus(RequestStateEnum.APPROVED);
        taskNoParent.setCompletedDate(Timestamp.valueOf(now.minusDays(10)));
        entityManager.persist(taskNoParent);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setTask(taskNoParent);
        uploadedFile.setFileName("Test Document Name 1");
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile.setCreationDate(now.minusDays(10));
        entityManager.persist(uploadedFile);

        UploadedFile uploadedFile2 = new UploadedFile();
        uploadedFile2.setTask(taskNoParent);
        uploadedFile2.setFileName("Test Document Name 2");
        uploadedFile2.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile2.setCreationDate(now.minusDays(10));
        entityManager.persist(uploadedFile2);

        /* Parent Task case */
        Task parentTask = new Task();
        parentTask.setRequestId(100000L);
        parentTask.setType(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST);
        parentTask.setCompletedDate(Timestamp.valueOf(now.minusDays(30)));
        parentTask.setStatus(RequestStateEnum.APPROVED);
        entityManager.persist(parentTask);

        Task taskWithParent = new Task();
        taskWithParent.setRequestId(100002L);
        taskWithParent.setType(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
        taskWithParent.setStatus(RequestStateEnum.APPROVED);
        taskWithParent.setCompletedDate(Timestamp.valueOf(now.minusDays(40)));
        taskWithParent.setParentTask(parentTask);
        entityManager.persist(taskWithParent);

        UploadedFile uploadedFile3 = new UploadedFile();
        uploadedFile3.setTask(taskWithParent);
        uploadedFile3.setFileName("Test Document Name 3");
        uploadedFile3.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile3.setCreationDate(now.minusDays(50));
        entityManager.persist(uploadedFile3);

        UploadedFile uploadedFile4 = new UploadedFile();
        uploadedFile4.setTask(taskWithParent);
        uploadedFile4.setFileName("Test Document Name 4");
        uploadedFile4.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile4.setCreationDate(now.minusDays(70));
        entityManager.persist(uploadedFile4);
    }

    @ParameterizedTest(name = "#{index} - {0} - {1}")
    @MethodSource("getArgumentsNoParentTask")
    void test_findFilesForCompletedTasksWithNoParent(int deleteSubmittedDocumentsOlderThanXDays, List<String> filesDeleted) {

        List<UploadedFile> files = uploadedFilesRepository.findFilesForCompletedTasksWithNoParent(
                List.of(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD, RequestType.AH_REQUESTED_DOCUMENT_UPLOAD), Timestamp.valueOf(now.minusDays(deleteSubmittedDocumentsOlderThanXDays)));
        Assert.assertEquals(filesDeleted.size(), files.size());
        files.forEach(file -> Assert.assertTrue(filesDeleted.contains(file.getFileName())));

    }

    static Stream<Arguments> getArgumentsNoParentTask() {
        return Stream.of(
                Arguments.of(60, List.of()),
                Arguments.of(10, List.of("Test Document Name 1", "Test Document Name 2")),
                Arguments.of(9, List.of("Test Document Name 1", "Test Document Name 2")),
                Arguments.of(8, List.of("Test Document Name 1", "Test Document Name 2"))
        );
    }

    @ParameterizedTest(name = "#{index} - {0} - {1}")
    @MethodSource("getArgumentsWithParentTask")
    void test_findFilesForCompletedTasksWithParent(int deleteSubmittedDocumentsOlderThanXDays, List<String> filesDeleted) {

        List<UploadedFile> files = uploadedFilesRepository.findFilesForCompletedTasksWithParent(
                List.of(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD, RequestType.AH_REQUESTED_DOCUMENT_UPLOAD), Timestamp.valueOf(now.minusDays(deleteSubmittedDocumentsOlderThanXDays)));
        Assert.assertEquals(filesDeleted.size(), files.size());
        files.forEach(file -> Assert.assertTrue(filesDeleted.contains(file.getFileName())));

    }

    static Stream<Arguments> getArgumentsWithParentTask() {
        return Stream.of(
                Arguments.of(60, List.of()),
                Arguments.of(50, List.of()),
                Arguments.of(40, List.of()),
                Arguments.of(30, List.of("Test Document Name 3", "Test Document Name 4")),
                Arguments.of(20, List.of("Test Document Name 3", "Test Document Name 4"))
        );
    }

    @Test
    void findFirstByFileNameContainsAndTaskStatus() {
    	String fileName = "UK_Emissions_28062021_SEPA_7C5015EF631195403A14EB0C875972CB.xlsx";
        Task emissionsUploadTask = new Task();
        emissionsUploadTask.setRequestId(110001L);
        emissionsUploadTask.setType(RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST);
        emissionsUploadTask.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        emissionsUploadTask.setInitiatedDate(Timestamp.valueOf(now));
        entityManager.persist(emissionsUploadTask);
    	
        UploadedFile emissionsUploadedFile = new UploadedFile();
        emissionsUploadedFile.setTask(emissionsUploadTask);
        emissionsUploadedFile.setFileName(fileName);
        emissionsUploadedFile.setFileStatus(FileStatus.SUBMITTED);
        emissionsUploadedFile.setCreationDate(now.minusSeconds(7));
        entityManager.persist(emissionsUploadedFile);
        
    	EmissionsTableExcelFilenameValidator filenameValidator = new EmissionsTableExcelFilenameValidator(fileName);
        UploadedFile file = uploadedFilesRepository.findFirstByFileNameContainsIgnoreCaseAndTaskStatus(filenameValidator.getRegExpGroup(EmissionsFilenameRegExpGroup.FILENAME_PREFIX),RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        assertNotNull(file);

    }
}
