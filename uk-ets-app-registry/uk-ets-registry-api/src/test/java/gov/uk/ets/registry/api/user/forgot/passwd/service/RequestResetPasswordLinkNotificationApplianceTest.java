package gov.uk.ets.registry.api.user.forgot.passwd.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.notification.RequestResetPasswordLinkNotification;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RequestResetPasswordLinkNotificationApplianceTest {

    private static final String TEST_EMAIL_ADDRESS = "reset.password@ukets.com";
    public static final Set<String> RECIPIENTS = Set.of("test1@email.com", "test2@email.com");

    @Mock
    GroupNotificationClient groupNotificationClient;
    @Mock
    NotificationService notificationService;
    @InjectMocks
    RequestResetPasswordLinkNotificationAppliance requestResetPasswordLinkNotificationAppliance;

    @Mock
    JoinPoint joinPoint;
    @Mock
    EmitsGroupNotifications notificationAnnotation;

    @Captor
    ArgumentCaptor<RequestResetPasswordLinkNotification> notificationCaptor;

    @BeforeEach()
    public void setUp() {

    }


    @Test
    public void shouldCreateNotificationForRequestResetPasswordLinkSuccess() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.REQUEST_RESET_PASSWORD_LINK});

        ForgotPasswordEmailDTO response = ForgotPasswordEmailDTO.builder()
            .success(true)
            .confirmationUrl("")
            .email(TEST_EMAIL_ADDRESS)
            .expiration(5L)
            .build();

        requestResetPasswordLinkNotificationAppliance.apply(joinPoint, notificationAnnotation, response);

        RequestResetPasswordLinkNotification notification = RequestResetPasswordLinkNotification.builder()
            .emailAddress(TEST_EMAIL_ADDRESS)
            .resetPasswordUrl("")
            .expiration(5L)
            .build();

        verify(groupNotificationClient, Mockito.times(1)).emitGroupNotification(notification);
    }

    @Test
    public void shouldNotCreateNotificationForRequestResetPasswordLinkFailure() {
        when(notificationAnnotation.value())
            .thenReturn(new GroupNotificationType[] {GroupNotificationType.REQUEST_RESET_PASSWORD_LINK});

        ForgotPasswordEmailDTO response = ForgotPasswordEmailDTO.builder()
            .success(false)
            .confirmationUrl("")
            .email(TEST_EMAIL_ADDRESS)
            .expiration(5L)
            .build();

        requestResetPasswordLinkNotificationAppliance.apply(joinPoint, notificationAnnotation, response);

        RequestResetPasswordLinkNotification notification = RequestResetPasswordLinkNotification.builder()
            .emailAddress(TEST_EMAIL_ADDRESS)
            .resetPasswordUrl("")
            .build();

        verify(groupNotificationClient, Mockito.times(0)).emitGroupNotification(notification);
    }
}
