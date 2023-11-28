package gov.uk.ets.registry.api.file.upload.allocationtable.services;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.allocationtable.AllocationTableUploadDetails;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableActionError;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableUploadActionException;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableUploadBusinessError;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.error.FileUploadErrorCsvFileGenerator;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.services.FileUploadProcessor;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.file.upload.types.FileTypes;
import gov.uk.ets.registry.api.file.upload.wrappers.AllocationTableContentValidationWrapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ooxml.POIXMLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles the uploading verification and submission of the uploaded file.
 */
@Service("allocationTableProcessor")
@RequiredArgsConstructor
public class AllocationTableUploadProcessor implements FileUploadProcessor {

    private final FileUploadService fileUploadService;

    private final AllocationTableExcelFileValidationService allocationTableExcelFileValidationService;

    private final UserService userService;

    private final PersistenceService persistenceService;

    private final UploadedFilesRepository uploadedFilesRepository;

    private final EventService eventService;

    private final TransactionPersistenceService transactionPersistenceService;

    private final AccountRepository accountRepository;

    private final TaskEventService taskEventService;

    private final Mapper mapper;

    private static final String ERROR_PROCESSING_FILE = "Error while processing the file";


    /**
     * {@inheritDoc}
     */
    @Override
    public FileHeaderDto loadAndVerifyFileIntegrity(MultipartFile file) {
        fileUploadService.scan(file);
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
            AllocationTableContentValidationWrapper wrapper = new AllocationTableContentValidationWrapper();
            String checksum =
                fileUploadService.validateFileType(multiPartInputStream, FileTypes.ALLOCATION_TABLE);
            String[] fileNameArray =
                allocationTableExcelFileValidationService.validateFileName(file.getOriginalFilename());
            wrapper.setChecksum(checksum);
            wrapper.setFileNameArray(fileNameArray);
            List<AllocationTableUploadBusinessError> uploadBusinessErrors =
                allocationTableExcelFileValidationService.validateFileContent(multiPartInputStream, wrapper);
            if (uploadBusinessErrors.isEmpty()) {
                UploadedFile uploadedFile = fileUploadService.saveFileInDatabase(file);
                return new FileHeaderDto(uploadedFile.getId(), uploadedFile.getFileName(), BaseType.ALLOCATION_TABLE, uploadedFile.getCreationDate().atZone(ZoneId.of("UTC")));
            } else {
                uploadBusinessErrors.sort(Comparator.comparing(AllocationTableUploadBusinessError::getRowNumber));
                FileUploadErrorCsvFileGenerator csvFileGenerator =
                    FileUploadErrorCsvFileGenerator.builder()
                                                   .allocationFileContentErrors(uploadBusinessErrors)
                                                   .build();
                UploadedFile errorFile =
                    fileUploadService.saveFileInDatabaseStartNewTransaction(
                        csvFileGenerator.generateCSVErrorFileName(file.getOriginalFilename()),
                        csvFileGenerator.generateCSVErrorFileContent().getBytes());

                throw AllocationTableUploadActionException.create(
                    AllocationTableActionError.builder()
                                              .code(AllocationTableActionError.ALLOCATION_TABLE_FILE_BUSINESS_ERRORS)
                                              .errorFileId(errorFile.getId())
                                              .errorFilename(errorFile.getFileName())
                                              .componentId("allocation-table-errors-file")
                                              .message("The selected file contains errors.")
                                              .build());
            }

        } catch (POIXMLException exception) {
            throw new FileUploadException("Strict OOXML is not supported");
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new FileUploadException(ERROR_PROCESSING_FILE, exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.UPLOAD_ALLOCATION_TABLE_REQUESTED)
    public Long submitUploadedFile(FileHeaderDto dto) {

        Date creationDate = new Date();
        User user = userService.getCurrentUser();
        String[] fileNameArray = allocationTableExcelFileValidationService.validateFileName(dto.getFileName());
        Task task = createAllocationTableUploadTask(user, creationDate);
        UploadedFile uploadedFile = uploadedFilesRepository.findById(dto.getId()).orElseThrow();
        uploadedFile.setTask(task);
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);

        task.setDifference(createAllocationTableDetails(fileNameArray[1]));

        persistenceService.save(task);
        uploadedFilesRepository.save(uploadedFile);
        eventService.createAndPublishEvent(task.getRequestId().toString(), user.getUrid(), dto.getFileName(),
                                           EventType.UPLOAD_ALLOCATION_TABLE, "Upload allocation table");
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, user.getUrid());
        return task.getRequestId();
    }

    private String createAllocationTableDetails(String allocationType) {
        AllocationCategory allocationCategory = AllocationType.parse(allocationType).getCategory();
        AllocationTableUploadDetails allocationTableUploadDetails = new AllocationTableUploadDetails();
        allocationTableUploadDetails.setAllocationCategory(allocationCategory);
        return mapper.convertToJson(allocationTableUploadDetails);
    }

    private Task createAllocationTableUploadTask(User currentUser, Date creationDate) {

        TransactionType type = TransactionType.IssueAllowances;
        AccountSummary accountSummary =
            transactionPersistenceService.getAccount(AccountType.UK_TOTAL_QUANTITY_ACCOUNT.getRegistryType(),
                                                     AccountType.UK_TOTAL_QUANTITY_ACCOUNT.getKyotoType(),
                                                     List.of(AccountStatus.OPEN, AccountStatus.SOME_TRANSACTIONS_RESTRICTED),
                                                     type.getPredefinedAccountCommitmentPeriod());
        Account account = accountRepository.findByIdentifier(accountSummary.getIdentifier()).orElseThrow(
            () -> new IllegalArgumentException("No account exists with identifier " + accountSummary.getIdentifier()));
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        task.setAccount(account);
        task.setType(RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(creationDate);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        return task;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UploadedFile getUploadedFileErrors(Long fileId) {
        return uploadedFilesRepository.findById(fileId).orElseThrow(IllegalArgumentException::new);
    }
}
