package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.compliance;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationUpdaterTest {

    private NotificationUpdater cut;

    @Spy
    private Notification n;


    @Test
    void shouldUpdateRecurrentNotification() {
        cut = new NotificationUpdater();
        n.setSchedule(NotificationSchedule.builder()
            .runEveryXDays(1)
            .build());

        cut.update(n);

        assertThat(n.getTimesFired()).isEqualTo(1);
        assertThat(n.getLastExecutionDate()).isEqualToIgnoringMinutes(LocalDateTime.now(UTC));
        verify(n, never()).setStatus(any());
    }


    @Test
    void shouldUpdateNonRecurrentNotification() {
        cut = new NotificationUpdater();
        n.setSchedule(NotificationSchedule.builder()
            .build());

        cut.update(n);

        assertThat(n.getTimesFired()).isEqualTo(1);
        assertThat(n.getLastExecutionDate()).isEqualToIgnoringMinutes(LocalDateTime.now(UTC));
        assertThat(n.getStatus()).isEqualTo(NotificationStatus.EXPIRED);
        assertThat(n.getSchedule().getEndDateTime()).isEqualToIgnoringMinutes(LocalDateTime.now(UTC));
    }

}
