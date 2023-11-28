package gov.uk.ets.registry.api.user.profile.notification;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.task.web.model.EmailChangeTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.profile.domain.EmailChange;
import gov.uk.ets.registry.api.user.profile.notification.EmailChangeNotificationService.NotifyTaskFinalizationCommand;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.stream.Stream;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailChangeNotificationApplianceTest {

    @Mock
    EmailChangeNotificationService emailChangeNotificationService;

    @Mock
    JoinPoint joinPoint;

    @Mock
    EmailChange result;

    @Mock
    EmitsGroupNotifications annotation;

    EmailChangeNotificationAppliance appliance;

    @BeforeEach
    void setUp() {
        appliance = new EmailChangeNotificationAppliance(emailChangeNotificationService);
    }

    @ParameterizedTest
    @MethodSource("notificationTypes")
    void apply(GroupNotificationType type) {
        //given
        given(annotation.value()).willReturn(new GroupNotificationType[]{type});
        if (type == GroupNotificationType.EMAIL_CHANGE_REQUESTED) {
            // when
            appliance.apply(joinPoint, annotation, result);
            // then
            then(emailChangeNotificationService).should(times(1)).emitEmailChangeNotifications(result);
        } else if (type == GroupNotificationType.EMAIL_CHANGE_TASK_COMPLETED) {
            // given
            EmailChangeTaskDetailsDTO dto = EmailChangeTaskDetailsDTO
                .builder()
                .userNewEmail("test_new@test.test")
                .userCurrentEmail("test_old@test.test")
                .build();
            Object[] args = new Object[]{
                dto, TaskOutcome.APPROVED,
                "a comment"
            };
            given(joinPoint.getArgs()).willReturn(args);

            // when
            appliance.apply(joinPoint, annotation, result);
            // then
            NotifyTaskFinalizationCommand command = NotifyTaskFinalizationCommand.builder()
                .outcome((TaskOutcome) args[1])
                .oldEmail(dto.getUserCurrentEmail())
                .newEmail(dto.getUserNewEmail())
                .comment((String)args[2])
                .build();
            then(emailChangeNotificationService).should(times(1)).emitEmailChangeFinalizationNotification(eq(command));
        }
    }

    static Stream<GroupNotificationType> notificationTypes() {
        return Stream.of(GroupNotificationType.EMAIL_CHANGE_REQUESTED, GroupNotificationType.EMAIL_CHANGE_TASK_COMPLETED);
    }
}