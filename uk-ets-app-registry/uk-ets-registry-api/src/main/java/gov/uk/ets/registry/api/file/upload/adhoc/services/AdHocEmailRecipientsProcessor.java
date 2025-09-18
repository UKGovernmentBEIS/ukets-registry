package gov.uk.ets.registry.api.file.upload.adhoc.services;

import gov.uk.ets.registry.api.file.upload.adhoc.dto.AdhocNotificationFileDto;
import gov.uk.ets.registry.api.file.upload.adhoc.services.error.AdHocEmailRecipientsBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.file.upload.types.FileTypes;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;

import static gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableUploadProcessor.ERROR_PROCESSING_FILE;

@Service
@RequiredArgsConstructor
public class AdHocEmailRecipientsProcessor {

    private final StringEmailTemplateProcessor templateProcessor;
    private final FileUploadService fileUploadService;
    private final AdHocEmailRecipientsValidationService adHocEmailRecipientsValidationService;

    public AdhocNotificationFileDto loadAndVerifyFileIntegrity(MultipartFile file) {
        fileUploadService.scan(file);
        try (InputStream multiPartInputStream = new ByteArrayInputStream(file.getBytes())) {
            fileUploadService.validateFileType(multiPartInputStream, FileTypes.AD_HOC_EMAIL_RECIPIENTS);
            AdhocFileValidationWrapper wrapper =
                adHocEmailRecipientsValidationService.validateFileContent(multiPartInputStream);
            if (wrapper.getErrors().isEmpty()) {
                UploadedFile uploadedFile = fileUploadService.saveFileInDatabase(file);
                return new AdhocNotificationFileDto(uploadedFile.getId(),
                    uploadedFile.getFileName(),
                    BaseType.AD_HOC_EMAIL_RECIPIENTS,
                    uploadedFile.getCreationDate().atZone(ZoneId.of("UTC")),
                    wrapper.getTentativeRecipients());
            } else {
                throw new AdHocEmailRecipientsBusinessRulesException(wrapper.getErrors());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new FileUploadException(ERROR_PROCESSING_FILE, e);
        }
    }
}
