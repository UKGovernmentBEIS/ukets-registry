package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import gov.uk.ets.registry.api.common.keycloak.OTPValidator;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadActionError;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadActionException;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadBusinessError;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableExcelFilenameValidator.EmissionsFilenameRegExpGroup;
import gov.uk.ets.registry.api.file.upload.error.FileUploadErrorCsvFileGenerator;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.services.FileUploadProcessor;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.file.upload.types.FileTypes;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ooxml.POIXMLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles the uploading verification and submission of the uploaded file.
 */
@Service("emissionsTableProcessor")
@RequiredArgsConstructor
public class EmissionsTableUploadProcessor implements FileUploadProcessor {

    private final FileUploadService fileUploadService;

    private final EmissionsTableExcelFileValidationService emissionsTableExcelFileValidationService;

    private final UserService userService;

    private final PersistenceService persistenceService;

    private final UploadedFilesRepository uploadedFilesRepository;
    
    private final EmissionsTableEventService emissionsTableEventService;
    
    private final OTPValidator otpValidator;

    public static final String ERROR_PROCESSING_FILE = "Error while processing the file";


    /**
     * {@inheritDoc}
     */
    @Override
    public FileHeaderDto loadAndVerifyFileIntegrity(MultipartFile file) {
        fileUploadService.scan(file);
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
        	//Validate the FileType and calculate the checksum.
            String checksum =fileUploadService.validateFileType(multiPartInputStream, FileTypes.EMISSIONS_TABLE);
            //Validate the filename and the checksum it contains.
            EmissionsTableExcelFilenameValidator filenameValidator = new EmissionsTableExcelFilenameValidator(file.getOriginalFilename());
            filenameValidator.validateMD5Checksum(checksum);
            
    		UploadedFile pendingEmissionsTableTask = uploadedFilesRepository
    		        .findFirstByFileNameContainsIgnoreCaseAndTaskStatus(
    		        		filenameValidator.getRegExpGroup(EmissionsFilenameRegExpGroup.FILENAME_PREFIX),
    		                RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);

    		 if (pendingEmissionsTableTask != null) {
                 throw EmissionsUploadActionException.create(EmissionsUploadActionError.builder()
                         .code(EmissionsUploadActionError.PENDING_EMISSIONS_FILE_UPLOAD)
                             .message("You cannot upload a new emissions table, while another emissions table is pending approval.")
                         .build());
    		 }
            
            List<EmissionsUploadBusinessError>  fileContentErrors = emissionsTableExcelFileValidationService.validateFileContent(multiPartInputStream);
            if(fileContentErrors.isEmpty()) {
                //Save the uploaded file
                UploadedFile uploadedFile = fileUploadService.saveFileInDatabase(file);
                return new FileHeaderDto(uploadedFile.getId(), uploadedFile.getFileName(), BaseType.EMISSIONS_TABLE, uploadedFile.getCreationDate().atZone(ZoneId.of("UTC")));
            } else {
            	FileUploadErrorCsvFileGenerator csvFileGenerator =
                    FileUploadErrorCsvFileGenerator.builder()
                                                   .emissionsFileContentErrors(fileContentErrors)
                                                   .build();
            	UploadedFile errorFile =
                    fileUploadService.saveFileInDatabaseStartNewTransaction(
                        csvFileGenerator.generateCSVErrorFileName(file.getOriginalFilename()),
                        csvFileGenerator.generateCSVErrorFileContent().getBytes());
            	
                throw EmissionsUploadActionException.create(EmissionsUploadActionError.builder()
                        .code(EmissionsUploadActionError.EMISSIONS_FILE_BUSINESS_ERRORS)
                            .errorFileId(errorFile.getId())
                            .errorFilename(errorFile.getFileName())
                            .componentId("emissions-errors-file")
                            .message("The uploaded emissions excel contains errors.")
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
    public Long submitUploadedFile(FileHeaderDto dto) {
    	User user = userService.getCurrentUser();
        new EmissionsTableExcelFilenameValidator(dto.getFileName());
        Task task = createEmissionsTableUploadTask(user, new Date());

        UploadedFile uploadedFile = uploadedFilesRepository.findById(dto.getId()).orElseThrow();
        uploadedFile.setTask(task);
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);

        persistenceService.save(task);
        uploadedFilesRepository.save(uploadedFile);
        emissionsTableEventService.createAndPublishEvent(task.getRequestId().toString(), dto.getFileName(),
                EventType.TASK_REQUESTED, "Emissions table uploaded");

        emissionsTableEventService.createAndPublishEmissionsUploadedAccountEvents(uploadedFile.getFileData(), dto.getFileName(),EventType.UPLOAD_ACCOUNT_EMISSIONS, "Emissions table uploaded",new HashSet<>());
        emissionsTableEventService.createAndPublishEmissionsUploadedCompliantEntityEvents(uploadedFile.getFileData(), dto.getFileName(),EventType.UPLOAD_COMPLIANT_ENTITY_EMISSIONS, "Emissions table uploaded",new HashSet<>());
        
        return task.getRequestId();
    }
    
    /**
     * Validates the OTP
     * @param otpCode
     * @return true if otp valid
     */
    public boolean validateOTP(String otpCode) {
    	return otpValidator.validate(otpCode);
    }

    private Task createEmissionsTableUploadTask(User currentUser, Date creationDate) {
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        task.setType(RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(creationDate);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        return task;
    }

    /**
     * Download the errors.
     */
	public UploadedFile getEmissionsTableErrorsFile(Long fileId) {
		return uploadedFilesRepository.findById(fileId).orElseThrow(IllegalArgumentException::new);
	}
}
