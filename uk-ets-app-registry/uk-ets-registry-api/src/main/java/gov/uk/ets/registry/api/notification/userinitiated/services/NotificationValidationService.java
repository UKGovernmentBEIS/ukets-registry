package gov.uk.ets.registry.api.notification.userinitiated.services;

import gov.uk.ets.registry.api.file.upload.dto.NotificationValidationRequest;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.AbstractNotificationInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableUploadProcessor.ERROR_PROCESSING_FILE;


@Service
@RequiredArgsConstructor
public class NotificationValidationService {

    private final NotificationDefinitionRepository notificationDefinitionRepository;
    private final FileUploadService fileUploadService;
    private final List<AbstractNotificationInstanceService> notificationInstanceServices;

    public void validateNotification(NotificationValidationRequest request) {

        if (request.getType() == NotificationType.AD_HOC) {
            return;
        }

        NotificationDefinition definition = notificationDefinitionRepository.findByType(request.getType())
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Notification definition not found for type: %s", request.getType())));

        Notification notification = Notification.builder()
            .definition(definition)
            .longText(request.getBody())
            .build();

        if (request.getType() == NotificationType.AD_HOC_EMAIL) {
            if (request.getFileId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fileId is required for type " + NotificationType.AD_HOC_EMAIL);
            }
            notification.setUploadedFile(fileUploadService.findUploadedFileById(request.getFileId())
                .orElseThrow(() -> new FileUploadException(ERROR_PROCESSING_FILE)));
        }

        notificationInstanceServices.stream()
            .filter(service -> service.getSupportedNotificationTypes().contains(request.getType()))
            .findFirst()
            .ifPresentOrElse(
                service -> service.validate(notification),
                () -> {
                    throw new IllegalStateException("NotificationService class not found for type: " + request.getType());
                }
            );
    }
}
