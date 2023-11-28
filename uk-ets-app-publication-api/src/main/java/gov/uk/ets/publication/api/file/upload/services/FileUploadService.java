package gov.uk.ets.publication.api.file.upload.services;

import static gov.uk.ets.publication.api.Utils.convertByteAmountToHumanReadable;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import gov.uk.ets.file.upload.services.ClamavService;
import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.file.upload.error.FileNameNotValidException;
import gov.uk.ets.publication.api.file.upload.error.FileTypeNotValidException;
import gov.uk.ets.publication.api.file.upload.error.FileUploadException;
import gov.uk.ets.publication.api.file.upload.repository.ReportFilesRepository;
import gov.uk.ets.publication.api.file.upload.types.FileTypes;
import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Provides methods for the various file validations as well as for the file persist & deletion to/from the database.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FileUploadService {

    public static final String ERROR_WHILE_PROCESSING_THE_FILE = "Error while processing the file";
    private static final Pattern MANY_FILES_NAME_PATTERN =
            Pattern.compile("\\w+_(20[0-9]{2})_([0-3])_([0-9])*$");

    private final ReportFilesRepository reportFilesRepository;
    private final ClamavService clamavService;

    /**
     * Method for the validation of the file type. <br>
     * Throws @link {@link FileTypeNotValidException} if not valid.
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
     * @return an ReportFile entity.
     * @throws FileUploadException if I/O interruption occurs.
     */
    @Transactional
    public ReportFile saveFileInDatabase(MultipartFile file) {

        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
            byte[] fileData = multiPartInputStream.readAllBytes();
            
            return saveFileInDatabase(file.getOriginalFilename(), fileData);

        } catch (IOException ioException) {
            throw new FileUploadException(ERROR_WHILE_PROCESSING_THE_FILE);
        }
    }

    private ReportFile saveFileInDatabase(String filename, byte[] fileData) {

        ReportFile uploadedFile = new ReportFile();
        uploadedFile.setFileName(filename);
        uploadedFile.setFileData(fileData);
        uploadedFile.setFileStatus(ReportPublicationStatus.NOT_SUBMITTED);
        uploadedFile.setFileSize(convertByteAmountToHumanReadable(fileData.length));
        uploadedFile.setGeneratedOn(LocalDateTime.now());

        return reportFilesRepository.save(uploadedFile);
    }

    /**
     * Method for deleting the persisted files that are not yet submitted.
     */
    @Transactional
    public void deleteNotSubmittedFiles() {
        List<ReportFile> uploadedFiles =
                reportFilesRepository.findByFileStatusEqualsAndGeneratedOnIsBefore(
                        ReportPublicationStatus.NOT_SUBMITTED,
                    LocalDateTime.now().minusDays(1));
        log.info("{} non-submitted files were cleaned up", uploadedFiles.size());
        reportFilesRepository.deleteAll(uploadedFiles);
    }

    /**
     * Method for validating file names (when display type is MANY_FILES).
     */
    public void validateFileName(String filename) {
        Matcher matcher = MANY_FILES_NAME_PATTERN.matcher(
                FilenameUtils.getBaseName(filename.toUpperCase()));
        if (!matcher.matches()) {
            throw new FileNameNotValidException(
                    "File name should follow the pattern <NAME>_<YEAR>_<CP>_<VERSION>.xlsx");
        }
    }
}
