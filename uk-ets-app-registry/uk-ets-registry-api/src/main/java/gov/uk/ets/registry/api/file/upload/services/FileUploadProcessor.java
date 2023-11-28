package gov.uk.ets.registry.api.file.upload.services;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadProcessor {

    /**
     * Responsible for the verification of the uploaded file.
     * The following actions take place, each one after the other if successful: <br>
     * 1. A validation of the file type is executed. <br>
     * 2. A validation of the name of the file follows up. (only for allocation table scenario) <br>
     * 3. A validation of the file content based on the business requirements. <br>
     * 4. The uploaded file is persisted in the database. (only for allocation table scenario)
     *
     * @param file the uploaded multipart file
     * @return the file header DTO if successful
     */
    FileHeaderDto loadAndVerifyFileIntegrity(MultipartFile file);

    /**
     * Responsible for the submission of the persisted task related file.
     *
     * @param dto the file header DTO
     * @return the task request id if successful
     */
    Long submitUploadedFile(FileHeaderDto dto);

    /**
     * Download the errors.
     */
    default UploadedFile getUploadedFileErrors(Long fileId) {
        return new UploadedFile();
    }
}
