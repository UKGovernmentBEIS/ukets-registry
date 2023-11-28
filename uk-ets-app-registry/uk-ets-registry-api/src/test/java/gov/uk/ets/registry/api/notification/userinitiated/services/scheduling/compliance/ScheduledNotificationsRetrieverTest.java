package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationSchedulingRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ScheduledNotificationsRetrieverTest {

    public static final long NOTIFICATION1_ID = 1L;
    public static final long NOTIFICATION3_ID = 3L;
    public static final long NOTIFICATION4_ID = 4L;
    public static final long NOTIFICATION5_ID = 5L;

    @Mock
    private NotificationSchedulingRepository schedulingRepository;

    @InjectMocks
    private ScheduledNotificationsRetriever cut;

    LocalDateTime now;
    Notification n1, n2, n3, n4, n5, n6;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now(UTC);

        NotificationDefinition def1 = new NotificationDefinition();

        n1 = new Notification();
        n1.setId(NOTIFICATION1_ID);
        n1.setDefinition(def1);
        n1.setSchedule(NotificationSchedule.builder()
            .startDateTime(now.minusSeconds(1))
            .build());

        n2 = new Notification();
        n2.setDefinition(def1);
        n2.setSchedule(NotificationSchedule.builder()
            .startDateTime(now.minusSeconds(1))
            .build());
        n2.setLastExecutionDate(now);

        n3 = new Notification();
        n3.setId(NOTIFICATION3_ID);
        n3.setDefinition(def1);
        n3.setSchedule(NotificationSchedule.builder()
            .startDateTime(now.minusSeconds(1))
            .runEveryXDays(2)
            .build());
        n3.setLastExecutionDate(now.minusDays(2));

        n4 = new Notification();
        n4.setId(NOTIFICATION4_ID);
        n4.setDefinition(def1);
        n4.setSchedule(NotificationSchedule.builder()
            .startDateTime(now.minusSeconds(1))
            .build());
        n4.setLastExecutionDate(now.minusDays(2));

        n5 = new Notification();
        n5.setId(NOTIFICATION5_ID);
        n5.setDefinition(def1);
        n5.setSchedule(NotificationSchedule.builder()
            .startDateTime(now.minusDays(5))
            .runEveryXDays(1)
            .build());
        n5.setLastExecutionDate(now.minusDays(2));

        n6 = new Notification();
        n6.setDefinition(def1);
        n6.setSchedule(NotificationSchedule.builder()
            .startDateTime(now.minusDays(1).plusMinutes(4))
            .runEveryXDays(1)
            .build());
        n6.setLastExecutionDate(now);
    }

    @Test
    void shouldRetrieveNotificationsThatHaveNeverBeenSent() {

        List<Notification> notifications = List.of(n1, n2);
        when(schedulingRepository.getActiveNotifications(NotificationType.getComplianceNotificationTypes()))
            .thenReturn(notifications);

        List<Notification> notificationsToBeSent = cut.getNotificationsToBeSent();

        assertThat(notificationsToBeSent).hasSize(1);
        assertThat(notificationsToBeSent.get(0).getId()).isEqualTo(NOTIFICATION1_ID);
    }

    @Test
    void shouldRetrieveNotificationsThatHaveRecurrence() {
        List<Notification> notifications = List.of(n2, n3);
        when(schedulingRepository.getActiveNotifications(NotificationType.getComplianceNotificationTypes()))
            .thenReturn(notifications);

        List<Notification> notificationsToBeSent = cut.getNotificationsToBeSent();

        assertThat(notificationsToBeSent).hasSize(1);
        assertThat(notificationsToBeSent.get(0).getId()).isEqualTo(NOTIFICATION3_ID);
    }

    @Test
    void shouldNotRetrieveSentNotificationsWhichHaveNoRecurrence() {
        List<Notification> notifications = List.of(n2, n4);
        when(schedulingRepository.getActiveNotifications(NotificationType.getComplianceNotificationTypes()))
            .thenReturn(notifications);

        List<Notification> notificationsToBeSent = cut.getNotificationsToBeSent();

        assertThat(notificationsToBeSent).hasSize(0);
    }

    @Test
    void shouldRetrieveNotificationsWhichRecurrenceHasPassedButNotExecuted() {
        List<Notification> notifications = List.of(n5);

        when(schedulingRepository.getActiveNotifications(NotificationType.getComplianceNotificationTypes()))
            .thenReturn(notifications);


        List<Notification> notificationsToBeSent = cut.getNotificationsToBeSent();

        assertThat(notificationsToBeSent).hasSize(1);
        assertThat(notificationsToBeSent.get(0).getId()).isEqualTo(NOTIFICATION5_ID);

    }

    @Test
    void shouldNotRetrieveNotificationsWhereStartTimeHasNotPassed() {
        List<Notification> notifications = List.of(n6);

        when(schedulingRepository.getActiveNotifications(NotificationType.getComplianceNotificationTypes()))
            .thenReturn(notifications);


        List<Notification> notificationsToBeSent = cut.getNotificationsToBeSent();

        assertThat(notificationsToBeSent).hasSize(0);
    }
}
