package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import static java.time.ZoneOffset.UTC;
import static java.util.function.Predicate.not;

import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.compliance.web.model.VerifiedEmissionsDTO;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadActionError;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadActionException;
import gov.uk.ets.registry.api.file.upload.emissionstable.error.EmissionsUploadBusinessError;
import gov.uk.ets.registry.api.file.upload.emissionstable.messaging.UpdateOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.file.upload.error.FileUploadErrorCsvFileGenerator;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.user.service.UserService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import org.apache.poi.ooxml.POIXMLException;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

/**
 * Service for handling emission tables.
 */
@Service
@AllArgsConstructor
public class EmissionsTableService {

    /**
     * Repository for emission entries.
     */
    private final EmissionsEntryRepository emissionsEntryRepository;

    private final FileUploadService fileUploadService;

    private final EmissionsTableExcelFileValidationService emissionsTableExcelFileValidationService;

    private final ComplianceEventService complianceEventService;

    private final UserService userService;

    @Transactional
    public Set<Long> submitEmissionEntries(UploadedFile file, Date completedDate) {

        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getFileData())) {
            List<EmissionsUploadBusinessError> fileContentErrors =
                emissionsTableExcelFileValidationService.validateFileContent(multiPartInputStream);
            if (fileContentErrors.isEmpty()) {
                return saveEmissionsTable(file.getFileName(), file.getFileData(), completedDate);
            } else {
                FileUploadErrorCsvFileGenerator csvFileGenerator =
                    FileUploadErrorCsvFileGenerator.builder()
                                                   .emissionsFileContentErrors(fileContentErrors)
                                                   .build();
                UploadedFile errorFile = fileUploadService.saveFileInDatabaseStartNewTransaction(
                    csvFileGenerator.generateCSVErrorFileName(file.getFileName()),
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
        } catch (IOException exception) {
            throw new FileUploadException(EmissionsTableUploadProcessor.ERROR_PROCESSING_FILE, exception);
        }
    }

    /**
     * Saves the provided emissions file in the db.If the compliant entity has submitted
     * the same amount of emissions in the last upload then this entry is ignored and no 
     * event must be generated.
     * @param filename the name of the excel file
     * @param fileContent the content of the excel
     * @param completedDate the date the task was completed
     * @return a list of compliant entity identifiers that had the same amount already submitted 
     *     hence not taken into account
     * @throws IOException in case the fileContent is corrupted
     */
    public Set<Long> saveEmissionsTable(String filename, byte[] fileContent, Date completedDate) throws IOException {

        Set<Long> unprocessedCompliantEntityIdentifers = new HashSet<>();
        try (ReadableWorkbook book = new ReadableWorkbook(new ByteArrayInputStream(fileContent))) {
            Sheet sheet = book.getFirstSheet();
            List<Row> rows = sheet.read();

            EmissionsTableExcelHeaderValidator headerValidator = new EmissionsTableExcelHeaderValidator(rows.get(0));

            rows.stream()
                .skip(1)
                .filter(r -> r.getFirstNonEmptyCell().isPresent())
                .forEach(r -> {
                    EmissionsEntry entry = new EmissionsEntry();
                    entry.setCompliantEntityId(Long.valueOf(r.getCellText(headerValidator.getIdentifierColumnIndex())));
                    entry.setYear(Long.valueOf(r.getCellText(headerValidator.getYearColumnIndex())));

                    Optional.ofNullable(r.getCellText(headerValidator.getEmissionsColumnIndex()))
                        .stream()
                        .filter(not(String::isBlank))
                        .forEach(e -> {
                            entry.setEmissions(Long.valueOf(e));
                        });

                    entry.setFilename(filename);
                    entry.setUploadDate(LocalDateTime.now());
                    Optional<VerifiedEmissionsDTO> existingStoredEmissionsForCompliantEntityAndYear 
                        = emissionsEntryRepository.findLatestByCompliantEntityIdentifier(entry.getCompliantEntityId())
                        .stream()
                        .filter(t -> t.getYear().equals(entry.getYear()))
                        .findFirst();
                    
                    if (existingStoredEmissionsForCompliantEntityAndYear.isPresent()) {
                        //In case the values are not equal
                        Long reportableEmissions = null;
                        if (Optional.ofNullable(existingStoredEmissionsForCompliantEntityAndYear.get().getReportableEmissions()).isPresent()) {
                            reportableEmissions = Long.valueOf(existingStoredEmissionsForCompliantEntityAndYear.get().getReportableEmissions());
                        }
                        if (!Objects.equal(reportableEmissions, entry.getEmissions())) {
                            emissionsEntryRepository.save(entry);
                            publishUpdateOfVerifiedEmissionsEvent(entry, completedDate);       
                            if (unprocessedCompliantEntityIdentifers.contains(entry.getCompliantEntityId())) {
                                unprocessedCompliantEntityIdentifers.remove(entry.getCompliantEntityId());
                            }
                        } else {
                            unprocessedCompliantEntityIdentifers.add(entry.getCompliantEntityId());
                        }
                    } else {
                        emissionsEntryRepository.save(entry);
                        publishUpdateOfVerifiedEmissionsEvent(entry, completedDate);                           
                    }

                });
        }
        
        return unprocessedCompliantEntityIdentifers;

    }

    public List<EmissionsEntry> findNonEmptyEntriesForCompliantEntityForYearBefore(Long compliantEntityId, Long year) {
        return emissionsEntryRepository.findNonEmptyEntriesBeforeYear(compliantEntityId, year);
    }
    
    public List<EmissionsEntry> findByCompliantEntityIdAndYearBefore(Long compliantEntityId, Long year) {
        return emissionsEntryRepository.findByCompliantEntityIdAndYearBefore(compliantEntityId, year);
    }

    public List<EmissionsEntry> findNonEmptyEntriesForCompliantEntityForYearAfter(Long compliantEntityId, Long year) {
        return emissionsEntryRepository.findNonEmptyEntriesAfterYear(compliantEntityId, year);
    }

    public void publishUpdateOfVerifiedEmissionsEvent(EmissionsEntry entry, Date completedDate) {
        publishUpdateOfVerifiedEmissionsEvent(entry, completedDate, userService.getCurrentUser().getUrid());
    }

    public void publishUpdateOfVerifiedEmissionsEvent(EmissionsEntry entry, Date completedDate, String actorId) {

        UpdateOfVerifiedEmissionsEvent event = UpdateOfVerifiedEmissionsEvent.builder()
            .compliantEntityId(entry.getCompliantEntityId())
            .actorId(actorId)
            .dateTriggered(LocalDateTime.now(ZoneId.of("UTC")))
            .dateRequested(LocalDateTime.ofInstant(completedDate.toInstant(), UTC))
            .year(entry.getYear().intValue())
            .verifiedEmissions(entry.getEmissions())
            .build();

        complianceEventService.processEvent(event);

    }
}
