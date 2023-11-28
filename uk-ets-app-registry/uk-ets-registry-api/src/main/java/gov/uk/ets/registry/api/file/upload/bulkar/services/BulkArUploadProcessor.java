package gov.uk.ets.registry.api.file.upload.bulkar.services;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.services.FileUploadProcessor;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.file.upload.types.FileTypes;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArContentValidationWrapper;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArTaskBuilder;
import gov.uk.ets.registry.api.task.domain.Task;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import gov.uk.ets.registry.api.task.service.TaskEventService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ooxml.POIXMLException;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("bulkArProcessor")
@RequiredArgsConstructor
public class BulkArUploadProcessor implements FileUploadProcessor {

    private final FileUploadService fileUploadService;
    private final BulkArExcelFileValidationService bulkArExcelFileValidationService;
    private final UploadedFilesRepository uploadedFilesRepository;
    private final AuthorizedRepresentativeService authorizedRepresentativeService;
    private final TaskEventService taskEventService;

    private static final String ERROR_PROCESSING_FILE = "Error while processing the file";

    /**
     * {@inheritDoc}
     */
    @Override
    public FileHeaderDto loadAndVerifyFileIntegrity(MultipartFile file) {
        fileUploadService.scan(file);
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
            BulkArContentValidationWrapper wrapper = new BulkArContentValidationWrapper();
            fileUploadService.validateFileType(multiPartInputStream, FileTypes.BULK_AR_TABLE);
            bulkArExcelFileValidationService.validateFileContent(multiPartInputStream,
                                                                 wrapper);
            UploadedFile uploadedFile = fileUploadService.saveFileInDatabase(file);
            return new FileHeaderDto(uploadedFile.getId(), uploadedFile.getFileName(), BaseType.BULK_AR, uploadedFile.getCreationDate().atZone(ZoneId.of("UTC")));
        } catch (POIXMLException exception) {
            throw new FileUploadException("Strict OOXML is not supported");
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new FileUploadException(ERROR_PROCESSING_FILE, exception);
        }
    }

    /**
     * Responsible for the submission of the persisted task related file.
     *
     * @param dto the file header DTO
     * @return the number of add AR tasks created
     */
    @Override
    public Long submitUploadedFile(FileHeaderDto dto) {
        UploadedFile file = uploadedFilesRepository.findById(dto.getId()).orElseThrow(
            () -> new FileUploadException(ERROR_PROCESSING_FILE));
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getFileData())) {
            BulkArContentValidationWrapper wrapper = new BulkArContentValidationWrapper();
            ReadableWorkbook wb = new ReadableWorkbook(multiPartInputStream);
            Sheet sheet = wb.getFirstSheet();
            List<Row> sheetList = sheet.read();
            bulkArExcelFileValidationService.validateColumnHeaders(
                sheetList.get(0).getCells(0, sheetList.get(0).getCellCount()), wrapper);
            List<BulkArTaskBuilder> dtos = extractDataFromFile(sheetList, wrapper);
            List<Task> tasks = authorizedRepresentativeService.placeBulkArTasks(dtos);
            tasks.forEach(t-> {
                taskEventService.createAndPublishTaskAndAccountRequestEvent(t, t.getInitiatedBy().getUrid());
                authorizedRepresentativeService.propagateEmailNotifications(t);
            });
            return (long) tasks.size();
        } catch (IOException exception) {
            throw new FileUploadException(ERROR_PROCESSING_FILE, exception);
        }
    }

    private List<BulkArTaskBuilder> extractDataFromFile(List<Row> sheetList, BulkArContentValidationWrapper wrapper) {
       return sheetList.stream()
                       .skip(1)
                       .filter(r -> r.getFirstNonEmptyCell().isPresent())
                       .map(row -> BulkArTaskBuilder
                               .builder().accountFullIdentifier(row.getCellText(wrapper.getAccountNumberPosition()))
                               .userUrid(row.getCellText(wrapper.getUserIdPosition()))
                               .permissions(AccountAccessRight.parse(row.getCellText(wrapper.getPermissionPosition())
                                                                          .toLowerCase()))
                               .build())
                       .collect(Collectors.toList());
    }
}
