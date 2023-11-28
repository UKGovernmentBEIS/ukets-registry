package gov.uk.ets.registry.api.file.upload.allocationtable.notification;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;

import java.time.ZonedDateTime;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;



@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UploadAllocationTableNotificationApplianceTest {

    
    private static final String TEST_EMAIL1 = "test1@test1.com";
    private static final String TEST_EMAIL2 = "test2@test2.com";
    
    @Mock
    GroupNotificationClient groupNotificationClient;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    UploadAllocationTableNotificationAppliance uploadAllocationTableNotificationAppliance;

    @Mock
    JoinPoint joinPoint;
    @Mock
    EmitsGroupNotifications notificationAnnotation;
    
    @BeforeEach()
    public void setUp() {
        when(notificationService.findEmailsOfAuthorityUsers(true))
            .thenReturn(Set.of(TEST_EMAIL1));
        when(notificationService.findEmailsOfAuthorityUsers(true))
            .thenReturn(Set.of(TEST_EMAIL2));
    }
    
    @Test
    void shouldCreateNotificationForAllocationTableRequest() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.UPLOAD_ALLOCATION_TABLE_REQUESTED});
        
        Long requestId = 738383L;
        FileHeaderDto fileHeaderDto = new FileHeaderDto(
            56473L, 
            "UK_NAT_2021_2022_78bf49d3032ac62e1cd94aaafba3e0a3", 
            BaseType.ALLOCATION_TABLE,
            ZonedDateTime.now());
        Object[] args = new Object[]{
            fileHeaderDto
            };
        
        when(joinPoint.getArgs()).thenReturn(args);
        
        uploadAllocationTableNotificationAppliance.apply(joinPoint, notificationAnnotation,requestId);

        UploadAllocationTableEmailNotification notification1 = new UploadAllocationTableEmailNotification(
            notificationService.findEmailsOfAuthorityUsers(true),
            GroupNotificationType.UPLOAD_ALLOCATION_TABLE_REQUESTED,
            AllocationType.NAT,
            requestId.toString()); 

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification1);
    }
    
    @ParameterizedTest
    @EnumSource(TaskOutcome.class)
    void shouldCreateNotificationForAllocationTableApproval(TaskOutcome outcome) {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.UPLOAD_ALLOCATION_TABLE_COMPLETED});
        
        Long requestId = 738383L;
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST);
        taskDetailsDTO.setFileName("UK_NAT_2021_2022_78bf49d3032ac62e1cd94aaafba3e0a3");

        Object[] args = new Object[]{
            taskDetailsDTO, 
            outcome,
            "A comment"
            };
        
        when(joinPoint.getArgs()).thenReturn(args);
        
        uploadAllocationTableNotificationAppliance.apply(joinPoint, notificationAnnotation,new TaskCompleteResponse(requestId,taskDetailsDTO));

        UploadAllocationTableEmailNotification notification1 = new UploadAllocationTableEmailNotification(
            notificationService.findEmailsOfAuthorityUsers(true),
            TaskOutcome.APPROVED == outcome ? 
                GroupNotificationType.UPLOAD_ALLOCATION_TABLE_APPROVED :
                    GroupNotificationType.UPLOAD_ALLOCATION_TABLE_REJECTED,
            AllocationType.NAT,
            requestId.toString()); 

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification1);
    }
}
