package gov.uk.ets.registry.api.notification.userinitiated;

import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class NotificationModelTestHelper {

    public static final String TEST_URID = "UK1234567890";
    static final String TEST_PARAMETER = "test-parameter";
    private final TestEntityManager entityManager;
    private final NotificationDefinitionRepository definitionRepository;

    NotificationDefinition adHocDefinition;
    Notification complianceNotification;
    Notification adHocNotification;

    public NotificationModelTestHelper(TestEntityManager entityManager,
                                       NotificationDefinitionRepository definitionRepository) {
        this.entityManager = entityManager;
        this.definitionRepository = definitionRepository;
    }


    public void setUp() {
        adHocDefinition = definitionRepository.findByType(NotificationType.AD_HOC).orElse(null);
        NotificationDefinition definition2 =
            definitionRepository.findByType(NotificationType.EMISSIONS_MISSING_FOR_OHA)
                .orElse(null);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        complianceNotification = Notification.builder()
            .definition(definition2)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(now.minusDays(1))
                .endDateTime(now.plusDays(1))
                .runEveryXDays(1)
                .build())
            .creator(TEST_URID)
            .build();
        entityManager.persist(complianceNotification);

        adHocNotification = Notification.builder()
            .definition(adHocDefinition)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 10, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 11, 16, 10, 0))
                .build())
            .creator(TEST_URID)
            .build();
        entityManager.persist(adHocNotification);

        entityManager.persist(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 9, 13, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 10, 13, 10, 0))
                .build())
            .status(NotificationStatus.EXPIRED)
            .creator(TEST_URID)
            .build());

        entityManager.persist(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 9, 13, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 10, 14, 10, 0))
                .build())
            .status(NotificationStatus.EXPIRED)
            .creator(TEST_URID)
            .build());

        entityManager.persist(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 11, 13, 9, 30))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        entityManager.persist(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 11, 12, 9, 30))
                .endDateTime(LocalDateTime.of(2021, 11, 14, 9, 0))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        entityManager.persist(Notification.builder()
            .definition(adHocDefinition)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 12, 12, 9, 30))
                .build())
            .status(NotificationStatus.PENDING)
            .creator(TEST_URID)
            .build());

        adHocNotification = Notification.builder()
            .definition(adHocDefinition)
            .status(NotificationStatus.ACTIVE)
            .schedule(NotificationSchedule.builder()
                .startDateTime(LocalDateTime.of(2021, 10, 1, 10, 0))
                .endDateTime(LocalDateTime.of(2021, 11, 13, 9, 0))
                .build())
            .creator(TEST_URID)
            .build();
        entityManager.persist(adHocNotification);

        entityManager.flush();
        entityManager.clear();
    }
}
