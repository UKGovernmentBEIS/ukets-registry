package gov.uk.ets.registry.api.user.notification;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.notification.EmailChangeUserStatusNotification;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.notification.UserDeactivationNotification;
import gov.uk.ets.registry.api.notification.UserDetailsUpdateNotification;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.UserDeactivationTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.UserDetailsUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.EnrolmentKeyDTO;
import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.UserDeactivationDTO;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeResultDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserNotificationApplianceTest {

    private static final String TEST_EMAIL1 = "test1@test1.com";
    private static final String TEST_EMAIL2 = "test2@test2.com";
    private static final String IAM_IDENTIFIER1 = "test1 TEST1";
    private static final String IAM_IDENTIFIER2 = "test2 TEST2";

    @Mock
    GroupNotificationClient groupNotificationClient;
    @Mock
    NotificationService notificationService;
    @Mock
    UserService userService;
    @Mock
    UserAdministrationService userAdministrationService;
    @InjectMocks
    UserNotificationAppliance userNotificationAppliance;

    @Mock
    JoinPoint joinPoint;
    @Mock
    EmitsGroupNotifications notificationAnnotation;

    @BeforeEach()
    public void setUp() {
        when(notificationService.getEmailAddressWhenChangingUserStatus(IAM_IDENTIFIER1))
            .thenReturn(TEST_EMAIL1);
        when(notificationService.getEmailAddressWhenChangingUserStatus(IAM_IDENTIFIER2))
            .thenReturn(TEST_EMAIL2);
    }

    @Test
    public void shouldCreateNotificationForProposal() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.EMAIL_CHANGE_STATUS});

        userNotificationAppliance.apply(joinPoint, notificationAnnotation, UserStatusChangeResultDTO.builder().iamIdentifier(IAM_IDENTIFIER1).build());
        userNotificationAppliance.apply(joinPoint, notificationAnnotation, UserStatusChangeResultDTO.builder().iamIdentifier(IAM_IDENTIFIER2).build());

        EmailChangeUserStatusNotification notification1 = EmailChangeUserStatusNotification.builder()
            .emailAddress(TEST_EMAIL1)
            .build();

        EmailChangeUserStatusNotification notification2 = EmailChangeUserStatusNotification.builder()
            .emailAddress(TEST_EMAIL2)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification1);
        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification2);
    }
    
    @Test
    public void shouldCreateNotificationForUserDetailsUpdateProposal() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.USER_DETAILS_UPDATE_REQUEST});
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        UserDetailsDTO current = new UserDetailsDTO();
        current.setUrid("UK123456789");
        current.setEmailAddress("test_address");
        dto.setCurrent(current);
        Object[] args = new Object[]{
            "urid", dto
            };
        
        when(joinPoint.getArgs()).thenReturn(args);
        userNotificationAppliance.apply(joinPoint, notificationAnnotation, 12345678L);
                
        UserDetailsUpdateNotification notification = UserDetailsUpdateNotification.builder()
            .emailAddress(current.getEmailAddress())
            .requestId(Objects.toString(12345678L, null))
            .userId("89")
            .type(GroupNotificationType.USER_DETAILS_UPDATE_REQUEST)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }
    
    @Test
    public void shouldCreateNotificationForUserDetailsUpdateComplete() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.USER_DETAILS_UPDATE_COMPLETED});
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.USER_DETAILS_UPDATE_REQUEST);
        UserDetailsUpdateTaskDetailsDTO dto = new UserDetailsUpdateTaskDetailsDTO(taskDetailsDTO);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setUrid("UK123456789");
        current.setEmailAddress("test_address");
        dto.setCurrent(current);
        Object[] args = new Object[]{
            dto, TaskOutcome.APPROVED
            };
        
        when(joinPoint.getArgs()).thenReturn(args);
        userNotificationAppliance.apply(joinPoint, notificationAnnotation, 
                TaskCompleteResponse.builder().requestIdentifier(12345678L).build());
                
        UserDetailsUpdateNotification notification = UserDetailsUpdateNotification.builder()
            .emailAddress(current.getEmailAddress())
            .requestId(Objects.toString(12345678L, null))
            .userId("89")
            .taskOutcome(TaskOutcome.APPROVED)
            .type(GroupNotificationType.USER_DETAILS_UPDATE_COMPLETED)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }

    @Test
    @Disabled("Notifications has been removed for deactivation")
    public void shouldCreateNotificationForUserDeactivationRequest() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.USER_DEACTIVATION_REQUEST});
        User user = new User();
        user.setUrid("UK123456");
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(TEST_EMAIL1);

        Object[] args = new Object[]{"UK123456"};

        when(joinPoint.getArgs()).thenReturn(args);
        when(userService.getUserByUrid(anyString())).thenReturn(user);
        when(userAdministrationService.findByIamId(user.getIamIdentifier())).thenReturn(userRepresentation);
        userNotificationAppliance.apply(joinPoint, notificationAnnotation, 12345678L);

        UserDeactivationNotification notification =
            UserDeactivationNotification.builder()
                                         .emailAddress(userRepresentation.getEmail())
                                         .requestId(Objects.toString(12345678L, null))
                                         .userId("UK123456")
                                         .type(GroupNotificationType.USER_DEACTIVATION_REQUEST)
                                         .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }
    
    @Test
    @Disabled("Notifications has been removed for deactivation")
    public void shouldCreateNotificationForUserDeactivationComplete() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.USER_DEACTIVATION_COMPLETED});
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.USER_DEACTIVATION_REQUEST);
        UserDeactivationTaskDetailsDTO dto = new UserDeactivationTaskDetailsDTO(taskDetailsDTO);
        UserDeactivationDTO deactivationDto = new UserDeactivationDTO();
        EnrolmentKeyDTO enrolmentKey = new EnrolmentKeyDTO("urid", "REGISTRATION_CODE", new Date());
        deactivationDto.setEnrolmentKeyDetails(enrolmentKey);
        UserRepresentation userRepresentation = new UserRepresentation();
        Map<String, List<String>> attributes = new HashMap<String, List<String>>();
        attributes.put("state", Collections.singletonList("REGISTERED"));
        userRepresentation.setAttributes(attributes);
        userRepresentation.setEmail(TEST_EMAIL1);
        KeycloakUser user = new KeycloakUser(userRepresentation);
        deactivationDto.setUserDetails(user);
        dto.setChanged(deactivationDto);
        Object[] args = new Object[]{
            dto, TaskOutcome.APPROVED
            };
        
        when(joinPoint.getArgs()).thenReturn(args);
        userNotificationAppliance.apply(joinPoint, notificationAnnotation, 
                TaskCompleteResponse.builder().requestIdentifier(12345678L).build());
                
        UserDeactivationNotification notification = UserDeactivationNotification.builder()
            .emailAddress(TEST_EMAIL1)
            .requestId(Objects.toString(12345678L, null))
            .userId("urid")
            .type(GroupNotificationType.USER_DEACTIVATION_COMPLETED)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }
}
