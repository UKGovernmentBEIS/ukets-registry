package gov.uk.ets.registry.api.notification.userinitiated;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.NotificationValidationRequest;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.NotificationValidationService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.AbstractNotificationInstanceService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationValidationServiceTest {

    @Mock
    private NotificationDefinitionRepository notificationDefinitionRepository;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private NotificationGenerator generator;

    @InjectMocks
    private NotificationValidationService service;

    @Mock
    private NotificationParameterHolder paramHolder;

    @Mock
    private AbstractNotificationInstanceService abstractNotificationInstanceService;

    @Mock
    private UploadedFile uploadedFile;

    private List<AbstractNotificationInstanceService> notificationInstanceServices;

    @BeforeEach
    void setUp() {
        notificationInstanceServices = new ArrayList<>();
        notificationInstanceServices.add(abstractNotificationInstanceService);
        service = new NotificationValidationService(notificationDefinitionRepository, fileUploadService, notificationInstanceServices);
    }

    @Test
    void validateNotification_validRequest_noException() {
        NotificationValidationRequest request = new NotificationValidationRequest(1L, NotificationType.AD_HOC_EMAIL, "Test body");
        NotificationDefinition definition = new NotificationDefinition();

        when(fileUploadService.findUploadedFileById(1L)).thenReturn(Optional.of(uploadedFile));
        when(notificationDefinitionRepository.findByType(any())).thenReturn(Optional.of(definition));
        when(abstractNotificationInstanceService.getSupportedNotificationTypes()).thenReturn(List.of(NotificationType.AD_HOC_EMAIL));

        assertDoesNotThrow(() -> service.validateNotification(request));
        verify(abstractNotificationInstanceService).validate(any(Notification.class));
    }

    @Test
    void validateNotification_definitionNotFound_throwsException() {
        NotificationValidationRequest request = new NotificationValidationRequest(1L, NotificationType.AD_HOC_EMAIL, "Test body");
        when(notificationDefinitionRepository.findByType(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.validateNotification(request));
    }

    @Test
    void validateNotification_adHocType_noFurtherValidation() {
        NotificationValidationRequest request = new NotificationValidationRequest(1L, NotificationType.AD_HOC, "Test body");

        assertDoesNotThrow(() -> service.validateNotification(request));
        verifyNoInteractions(notificationDefinitionRepository, fileUploadService, abstractNotificationInstanceService);
    }

    @Test
    void validateNotification_adHocEmailType_validRequest_noException() {
        NotificationValidationRequest request = new NotificationValidationRequest(1L, NotificationType.AD_HOC_EMAIL, "Test body");
        request.setFileId(1L);
        NotificationDefinition definition = new NotificationDefinition();

        when(notificationDefinitionRepository.findByType(any())).thenReturn(Optional.of(definition));
        when(fileUploadService.findUploadedFileById(1L)).thenReturn(Optional.of(uploadedFile));
        when(abstractNotificationInstanceService.getSupportedNotificationTypes()).thenReturn(List.of(NotificationType.AD_HOC_EMAIL));

        assertDoesNotThrow(() -> service.validateNotification(request));
        verify(abstractNotificationInstanceService).validate(any(Notification.class));
    }

    @Test
    void validateNotification_adHocEmailType_fileIdNull_throwsException() {
        NotificationValidationRequest request = new NotificationValidationRequest(null, NotificationType.AD_HOC_EMAIL, "Test body");

        when(notificationDefinitionRepository.findByType(any())).thenReturn(Optional.of(new NotificationDefinition()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            service.validateNotification(request));

        assertEquals("fileId is required for type AD_HOC_EMAIL", exception.getReason());
    }

    @Test
    void validateNotification_adHocEmailType_fileNotFound_throwsException() {
        NotificationValidationRequest request = new NotificationValidationRequest(1L, NotificationType.AD_HOC_EMAIL, "Test body");
        request.setFileId(1L);
        NotificationDefinition definition = new NotificationDefinition();

        when(notificationDefinitionRepository.findByType(any())).thenReturn(Optional.of(definition));
        when(fileUploadService.findUploadedFileById(1L)).thenReturn(Optional.empty());

        assertThrows(FileUploadException.class, () -> service.validateNotification(request));
    }

}
