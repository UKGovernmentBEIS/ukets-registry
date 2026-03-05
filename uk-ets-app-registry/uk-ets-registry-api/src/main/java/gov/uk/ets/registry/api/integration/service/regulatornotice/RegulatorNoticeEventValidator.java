package gov.uk.ets.registry.api.integration.service.regulatornotice;

import gov.uk.ets.file.upload.error.ClamavException;
import gov.uk.ets.file.upload.services.ClamavService;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.file.upload.error.FileTypeNotValidException;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.file.upload.types.FileTypes;
import gov.uk.ets.registry.api.integration.service.account.validators.CommonAccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RegulatorNoticeEventValidator {

    private final AccountRepository accountRepository;

    private static final String ERROR_WHILE_PROCESSING_THE_FILE = "Error while processing the file";

    @Qualifier("regulatorNoticeValidator")
    private final CommonAccountValidator regulatorNoticeValidator;
    private final FileUploadService fileUploadService;
    private final ClamavService clamavService;

    public List<IntegrationEventErrorDetails> validate(RegulatorNoticeEvent event) {

        List<IntegrationEventErrorDetails> errorDetails = new ArrayList<>();

        regulatorNoticeValidator.validateMandatoryField(
                event.getRegistryId(),
                "Registry ID",
                s -> !s.matches("^\\d+$"),
                IntegrationEventError.ERROR_0603,
                errorDetails
        );

        regulatorNoticeValidator.validateMandatoryField(
                event.getType(),
                "Notification Type",
                s -> false,
                IntegrationEventError.ERROR_0602,
                errorDetails);

        if (accountRepository.findByCompliantEntityIdentifier(Long.valueOf(event.getRegistryId())).isEmpty()) {
            errorDetails.add(
                    new IntegrationEventErrorDetails(
                            IntegrationEventError.ERROR_0603,
                            "Registry ID"
                    )
            );
        }

        if (event.getFileData() != null && event.getFileData().length > 0) {
            scanFile(event.getFileData(), errorDetails);
            validateFileType(event, errorDetails);
        }

        return errorDetails;
    }

    private void validateFileType(RegulatorNoticeEvent event, List<IntegrationEventErrorDetails> errorDetails) {

        try (InputStream inputStream = new ByteArrayInputStream(event.getFileData())) {

            fileUploadService.validateFileType(
                    inputStream,
                    FileTypes.REGULATOR_NOTICE_DOCUMENT
            );

        } catch (FileTypeNotValidException ex) {

            errorDetails.add(
                    new IntegrationEventErrorDetails(
                            IntegrationEventError.ERROR_0605,
                            "File Type"
                    )
            );

        } catch (IOException | NoSuchAlgorithmException ex) {

            throw new FileUploadException(ERROR_WHILE_PROCESSING_THE_FILE);
        }
    }

    public void scanFile(byte[] fileData, List<IntegrationEventErrorDetails> errorDetails) {
        try (InputStream multiPartInputStream = new ByteArrayInputStream(fileData)) {
            clamavService.scan(multiPartInputStream);
        } catch (ClamavException clamavException) {
            errorDetails.add(
                    new IntegrationEventErrorDetails(
                            IntegrationEventError.ERROR_0604,
                            "File Content"
                    )
            );
        } catch (IOException ioException) {
            throw new FileUploadException(ERROR_WHILE_PROCESSING_THE_FILE);
        }
    }
}
