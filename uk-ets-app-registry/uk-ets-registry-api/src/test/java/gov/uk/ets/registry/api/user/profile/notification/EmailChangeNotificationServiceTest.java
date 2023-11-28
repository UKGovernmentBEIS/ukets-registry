package gov.uk.ets.registry.api.user.profile.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.profile.domain.EmailChange;
import gov.uk.ets.registry.api.user.profile.notification.EmailChangeNotificationService.NotifyTaskFinalizationCommand;
import gov.uk.ets.registry.usernotifications.GroupNotification;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailChangeNotificationServiceTest {
    @Mock
    private GroupNotificationClient groupNotificationClient;

    private EmailChangeNotificationService emailChangeNotificationService;

    @BeforeEach
    void setup() {
        emailChangeNotificationService = new EmailChangeNotificationService(groupNotificationClient);
    }

    @Test
    void emitEmailChangeNotifications() {
        // given
        String oldEmail = "test@test.old";
        String newEmail = "test@test.new";
        String verificationUrl = "verificationurl";
        EmailChange emailChange = mock(EmailChange.class);
        given(emailChange.getNewEmail()).willReturn(newEmail);
        given(emailChange.getOldEmail()).willReturn(oldEmail);
        given(emailChange.getConfirmationUrl()).willReturn(verificationUrl);
        Long expiration = 60L;
        given(emailChange.getExpiration()).willReturn(expiration);


        // when
        emailChangeNotificationService.emitEmailChangeNotifications(emailChange);

        // then
        EmailChangeConfirmationNotification emailChangeConfirmationNotification = EmailChangeConfirmationNotification.builder()
            .newUserEmail(newEmail)
            .confirmationUrl(verificationUrl)
            .expiration(expiration)
            .build();
        EmailChangeRequestedNotification changeEmailRequestedNotification = new EmailChangeRequestedNotification(oldEmail);
        InOrder order = inOrder(groupNotificationClient);
        order.verify(groupNotificationClient, times(1)).emitGroupNotification(eq(emailChangeConfirmationNotification));
        order.verify(groupNotificationClient, times(1)).emitGroupNotification(eq(changeEmailRequestedNotification));
        order.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @MethodSource("outcomes")
    void emitEmailChangeFinalizationNotification(TaskOutcome outcome) {
        // given
        String newEmail = "new_email@email.com";
        String oldEmail = "old_email@email.com";
        String comment = "a rejection comment";
        EmailChangeApprovedNotification emailChangeApprovedNotification = new EmailChangeApprovedNotification(newEmail);
        EmailChangeRejectedNotification emailChangeRejectedNotification = new EmailChangeRejectedNotification(oldEmail, comment);

        NotifyTaskFinalizationCommand command = NotifyTaskFinalizationCommand.builder()
            .newEmail(newEmail)
            .oldEmail(oldEmail)
            .comment(comment)
            .outcome(outcome)
            .build();

        //when
        emailChangeNotificationService.emitEmailChangeFinalizationNotification(command);
        // then
        ArgumentCaptor<GroupNotification> notificationCaptor = ArgumentCaptor.forClass(GroupNotification.class);
        then(groupNotificationClient).should(times(1)).emitGroupNotification(notificationCaptor.capture());
        if (outcome == TaskOutcome.APPROVED) {
            assertEquals(emailChangeApprovedNotification, notificationCaptor.getValue());
        } else {
            assertEquals(emailChangeRejectedNotification, notificationCaptor.getValue());
        }
    }

    static Stream<TaskOutcome> outcomes() {
        return Stream.of(TaskOutcome.values());
    }
}