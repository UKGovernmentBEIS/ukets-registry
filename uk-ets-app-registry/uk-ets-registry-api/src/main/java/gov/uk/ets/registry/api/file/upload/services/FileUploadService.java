package gov.uk.ets.registry.api.file.upload.services;

import gov.uk.ets.file.upload.services.ClamavService;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.error.FileTypeNotValidException;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.file.upload.types.FileTypes;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Provides methods for the various file validations as well as for the file persist & deletion to/from the database.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FileUploadService {

    public static final String ERROR_WHILE_PROCESSING_THE_FILE = "Error while processing the file";
    private static final List<RequestType> REQUESTED_DOCUMENT_UPLOAD_TYPES =
        Arrays.asList(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD, RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
    private final UploadedFilesRepository uploadedFilesRepository;

    private final ConversionService conversionService;

    private final ClamavService clamavService;
    private final EventService eventService;
    private final Mapper mapper;

    @Value("${registry.file.delete.submit.document.older.than.x.days}")
    private Long deleteSubmittedDocumentsOlderThanXDays;

    @Value("${registry.file.delete.not.submit.document.older.than.x.days}")
    private Long deleteNotSubmittedDocumentsOlderThanXDays;

    /**
     * Method for the validation of the file type. <br>
     * Throws @link {@link gov.uk.ets.registry.api.file.upload.error.FileTypeNotValidException} if not valid.
     *
     * @param inputStream the input stream of the uploaded file
     * @param fileTypes   the uploaded file type
     * @return the MD5 checksum as String
     * @throws NoSuchAlgorithmException if the requested cryptographic algorithm is not available
     * @throws IOException              if I/O interruption occurs
     */
    public String validateFileType(InputStream inputStream, FileTypes fileTypes)
            throws NoSuchAlgorithmException, IOException {
        Detector detector = new DefaultDetector();
        Metadata metadata = new Metadata();
        MediaType mediaType = detector.detect(inputStream, metadata);
        String mediaTypeStr = mediaType.toString();

        if (fileTypes.isTheTypeOf(mediaTypeStr)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(inputStream.readAllBytes());
            byte[] digest = md.digest();
            inputStream.reset();
            return DatatypeConverter.printHexBinary(digest);
        }
        throw new FileTypeNotValidException(fileTypes.getError());
    }

    /**
     * Scan stream for viruses.
     *
     * @param file the uploaded multipart file
     */
    public void scan(MultipartFile file) {
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
            clamavService.scan(multiPartInputStream);
        } catch (IOException ioException) {
            throw new FileUploadException(ERROR_WHILE_PROCESSING_THE_FILE);
        }
    }

    /**
     * Method for persisting the file in the database.
     *
     * @param file the uploaded multipart file.
     * @return an UploadedFile entity.
     * @throws FileUploadException if I/O interruption occurs.
     */
    @Transactional
    public UploadedFile saveFileInDatabase(MultipartFile file) {

        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
            byte[] fileData = multiPartInputStream.readAllBytes();
            
            return saveFileInDatabase(file.getOriginalFilename(),fileData);

        } catch (IOException ioException) {
            throw new FileUploadException(ERROR_WHILE_PROCESSING_THE_FILE);
        }
    }

    @Transactional
    public UploadedFile saveFileInDatabase(String filename, byte[] fileData) {

            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setFileName(filename);
            uploadedFile.setFileData(fileData);
            uploadedFile.setFileStatus(FileStatus.NOT_SUBMITTED);
            uploadedFile.setFileSize(conversionService.convertByteAmountToHumanReadable(fileData.length));
            uploadedFile.setCreationDate(LocalDateTime.now());

            return uploadedFilesRepository.save(uploadedFile);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UploadedFile saveFileInDatabaseStartNewTransaction(String filename, byte[] fileData) {

    	return saveFileInDatabase(filename, fileData);
    }
    
    /**
     * Method for updating the file in the database.
     *
     * @param file      the uploaded multipart file.
     * @param fileId    the file id to update.
     * @param requestId the task request id of the file to update.
     * @return an UploadedFile entity.
     * @throws FileUploadException if I/O interruption occurs.
     */
    @Transactional
    public UploadedFile updateFileInDatabase(MultipartFile file, Long fileId, Long requestId) {
        Optional<UploadedFile> currentFile = uploadedFilesRepository.findByTaskRequestIdAndId(requestId, fileId);
        return currentFile.map(current -> {
            try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
                byte[] fileData = multiPartInputStream.readAllBytes();
                current.setFileName(file.getOriginalFilename());
                current.setFileData(fileData);
                current.setFileStatus(FileStatus.NOT_SUBMITTED);
                current.setFileSize(conversionService.convertByteAmountToHumanReadable(fileData.length));
                current.setCreationDate(LocalDateTime.now());

                return uploadedFilesRepository.save(current);

            } catch (IOException ioException) {
                throw new FileUploadException(ERROR_WHILE_PROCESSING_THE_FILE);
            }
        }).orElseThrow(() -> new FileUploadException("Error while fetching the file"));
    }

    /**
     * Method for deleting the persisted files that are not yet submitted.
     */
    @Transactional
    public void deleteNotSubmittedFiles() {
        List<UploadedFile> uploadedFiles =
                uploadedFilesRepository.findByFileStatusEqualsAndCreationDateIsBefore(
                        FileStatus.NOT_SUBMITTED,
                        LocalDateTime.now().minusDays(deleteNotSubmittedDocumentsOlderThanXDays));
        log.info("{} non-submitted files were cleaned up", uploadedFiles.size());

        Set<Task> requestDocumentUploadTasks = uploadedFiles.stream()
            .map(UploadedFile::getTask)
            .filter(Objects::nonNull)
            .filter(task -> REQUESTED_DOCUMENT_UPLOAD_TYPES.contains(task.getType()))
            .collect(Collectors.toSet());

        // We need to clear also the difference column.
        for (Task task : requestDocumentUploadTasks) {
            RequestDocumentsTaskDifference requestDocumentsTaskDifference =
                mapper.convertToPojo(task.getDifference(), RequestDocumentsTaskDifference.class);

            List<Long> documentIds = uploadedFiles.stream()
                .filter(uploadedFile -> Objects.equals(uploadedFile.getTask(), task))
                .map(UploadedFile::getId)
                .toList();
            
            documentIds.forEach(requestDocumentsTaskDifference.getDocumentIds()::remove);
            requestDocumentsTaskDifference.getUploadedFileNameIdMap()
                .replaceAll((key, value) -> documentIds.contains(value) ? null : value);

            task.setDifference(mapper.convertToJson(requestDocumentsTaskDifference));
        }

        uploadedFilesRepository.deleteAll(uploadedFiles);
    }

    /**
     * Method for deleting the persisted files that are submitted and older than a configurable
     * number of days.
     */
    @Transactional
    public void deleteSubmittedFiles() {
        List<UploadedFile> fileTasksWithParent = uploadedFilesRepository
                .findFilesForCompletedTasksWithParent(List.of(RequestType.AR_REQUESTED_DOCUMENT_UPLOAD, RequestType.AH_REQUESTED_DOCUMENT_UPLOAD),
                        Timestamp.valueOf(LocalDateTime.now().minusDays(deleteSubmittedDocumentsOlderThanXDays)));
        deleteUploadedFiles(fileTasksWithParent);
    }

    private void deleteUploadedFiles(List<UploadedFile> uploadedFiles) {
        if (!CollectionUtils.isEmpty(uploadedFiles)) {
            uploadedFiles.forEach(file -> eventService.createAndPublishEvent(file.getTask().getRequestId().toString(),
                    null, file.getFileName() + " with ID " + file.getId(),
                    EventType.TASK_DELETE_SUBMITTED_DOCUMENT, "Document automatically deleted"));
            log.info("{} submitted files were cleaned up", uploadedFiles.size());
            uploadedFilesRepository.deleteAll(uploadedFiles);
        }
    }

    public Optional<UploadedFile> findUploadedFileById(Long fileId) {
        return uploadedFilesRepository.findById(fileId);
    }
}
