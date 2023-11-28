package gov.uk.ets.publication.api.file.upload.services;

import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.file.upload.error.FileUploadException;
import gov.uk.ets.publication.api.file.upload.types.FileTypes;
import gov.uk.ets.publication.api.model.DisplayType;
import gov.uk.ets.publication.api.web.model.BaseType;
import gov.uk.ets.publication.api.web.model.FileInfoDto;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ooxml.POIXMLException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PublicationUploadProcessor {

    private final FileUploadService fileUploadService;

    private static final String ERROR_PROCESSING_FILE = "Error while processing the file";

    /**
     * Responsible for the verification of the uploaded file.
     * The following actions take place, each one after the other if successful: <br>
     * 1. The file is scanned with clamav. <br>
     * 2. A validation of the file type is executed. <br>
     * 3. (optional) A validation of the file name is executed if the the type is MANY_FILES. <br>
     * 4. The uploaded file is persisted in the database.
     *
     * @param file the uploaded multipart file
     * @param displayType the display type of the section
     * @return the file header DTO if successful
     */
    public FileInfoDto loadAndVerifyFileIntegrity(MultipartFile file, DisplayType displayType) {
        fileUploadService.scan(file);
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
            fileUploadService.validateFileType(multiPartInputStream, FileTypes.REPORT_PUBLICATION);
            if (DisplayType.MANY_FILES.equals(displayType)) {
                fileUploadService.validateFileName(file.getOriginalFilename());
            }
            ReportFile reportFile = fileUploadService.saveFileInDatabase(file);
            return new FileInfoDto(reportFile.getId(), reportFile.getFileName(), null,  BaseType.PUBLICATION_REPORT, null);
        } catch (POIXMLException exception) {
            throw new FileUploadException("Strict OOXML is not supported");
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new FileUploadException(ERROR_PROCESSING_FILE, exception);
        }
    }
}
