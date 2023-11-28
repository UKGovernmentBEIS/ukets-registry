package gov.uk.ets.registry.api.user.forgot.passwd.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.notification.RequestResetPasswordLinkNotification;
import gov.uk.ets.registry.api.notification.ResetPasswordSuccessNotification;
import gov.uk.ets.registry.api.user.forgot.passwd.web.ResetPasswordResponse;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordSuccessNotificationApplianceTest {

    
    private static final String TEST_EMAIL_ADDRESS = "reset.password@ukets.com";
    
    @Mock
    GroupNotificationClient groupNotificationClient;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    ResetPasswordSuccessNotificationAppliance resetPasswordSuccessNotificationAppliance;

    @Mock
    JoinPoint joinPoint;
    @Mock
    EmitsGroupNotifications notificationAnnotation;

    @Captor
    ArgumentCaptor<RequestResetPasswordLinkNotification> notificationCaptor;


    @Test
    public void shouldCreateNotificationForRequestResetPasswordLinkSuccess() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.RESET_PASSWORD_SUCCESS});

        ResetPasswordResponse response = new ResetPasswordResponse(true,TEST_EMAIL_ADDRESS);
        
        resetPasswordSuccessNotificationAppliance.apply(joinPoint, notificationAnnotation, response);

        ResetPasswordSuccessNotification notification = ResetPasswordSuccessNotification.builder()
            .emailAddress(TEST_EMAIL_ADDRESS)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }
    
    @Test
    public void shouldNotCreateNotificationForRequestResetPasswordLinkFailure() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.RESET_PASSWORD_SUCCESS});

        ResetPasswordResponse response = new ResetPasswordResponse(false,null);
        
        resetPasswordSuccessNotificationAppliance.apply(joinPoint, notificationAnnotation, response);

        ResetPasswordSuccessNotification notification = ResetPasswordSuccessNotification.builder()
            .emailAddress(TEST_EMAIL_ADDRESS)
            .build();

        verify(groupNotificationClient, Mockito.times(0)).emitGroupNotification(notification);
    }
}
