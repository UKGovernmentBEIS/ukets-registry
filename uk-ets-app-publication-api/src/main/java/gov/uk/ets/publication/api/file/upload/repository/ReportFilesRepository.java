package gov.uk.ets.publication.api.file.upload.repository;


import gov.uk.ets.publication.api.domain.ReportFile;
import gov.uk.ets.publication.api.model.ReportPublicationStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the uploaded files.
 */
public interface ReportFilesRepository extends JpaRepository<ReportFile, Long> {

    /**
     * Retrieve the report file entities based on their file status and creation date.
     *
     * @param status   the file status
     * @param dateTime the creation date time
     * @return a list of report file entities.
     */
    List<ReportFile> findByFileStatusEqualsAndGeneratedOnIsBefore(ReportPublicationStatus status, LocalDateTime dateTime);

    /**
     * Retrieve the report files for a specific section.
     *
     * @param sectionId   the sectionId
     * @return a list of report file entities.
     */
    List<ReportFile> findBySectionId(Long sectionId);
    
    /**
     * Retrieve the report files for a specific section, sorted according to the request.
     *
     * @param sectionId   the sectionId
     * @param sort        the sorting params
     * @param status      the file status
     * @return a list of report file entities.
     */
    List<ReportFile> findBySectionIdAndFileStatus(Long sectionId, Sort sort, ReportPublicationStatus status);

    /**
     * Retrieve the report files that belong to the same batch (case of ONE_FILE_PER_YEAR).
     *
     * @param batchId   the batch id
     * @return a list of report file entities.
     */
    List<ReportFile> findByBatchId(String batchId);
}
