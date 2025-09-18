package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhocemail;


import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.emailgeneration.EmailException;
import gov.uk.ets.registry.api.notification.userinitiated.NotificationModelTestHelper;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import gov.uk.ets.registry.api.notification.userinitiated.services.UserInitiatedNotificationService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationUpdater;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhoc.email.AdHocEmailNotificationInstanceService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@PostgresJpaTest
@DataJpaTest(includeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*notification.userinitiated.services.scheduling.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*notification.userinitiated.repository.*")
})
@Import({Configuration.class, StringEmailTemplateProcessor.class})
class AdHocEmailNotificationInstanceServiceIntegrationTest {

    private static final String OHA_LABEL = "ETS - Operator holding account";
    private static final String PERMIT_ID = "permit-id";
    private static final String USER_FIRST_NAME = "first";
    private static final String USER_LAST_NAME = "last";
    public static final String ACCOUNT_FULL_IDENTIFIER = "UK-100-123456-324";
    public static final String MASKED_ACCOUNT_FULL_IDENTIFIER = "UK-100-12XXXX-324";
    public static final String INSTALLATION_NAME = "Test installation";
    public static final String WORK_EMAIL_ADDRESS = "test@test.com";

    @Autowired
    AdHocEmailNotificationInstanceService cut;

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private NotificationDefinitionRepository definitionRepository;
    @Autowired
    private UploadedFilesRepository uploadedFilesRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationUpdater updater;

    @MockBean
    private UserWorkContactRepository userWorkContactRepository;

    @MockBean
    private ComplianceService complianceService;

    @MockBean
    private EventService eventService;

    @MockBean
    private GroupNotificationClient groupNotificationClient;

    @MockBean
    UserInitiatedNotificationService userInitiatedNotificationService;

    NotificationModelTestHelper notificationModelTestHelper;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUrid(NotificationModelTestHelper.TEST_URID);
        user.setState(UserStatus.ENROLLED);
        user.setFirstName(USER_FIRST_NAME);
        user.setLastName(USER_LAST_NAME);
        entityManager.persist(user);
        notificationModelTestHelper = new NotificationModelTestHelper(definitionRepository, notificationRepository, uploadedFilesRepository);
        notificationModelTestHelper.setUp();
    }

    @Test
    void shouldGenerateNotifications() {

        List<IdentifiableEmailNotification> identifiableEmailNotifications = cut.generateNotificationInstances();

        assertThat(identifiableEmailNotifications).hasSize(2);
        IdentifiableEmailNotification emailNotification1 = identifiableEmailNotifications.get(0);
        IdentifiableEmailNotification emailNotification2 = identifiableEmailNotifications.get(1);

        assertThat(emailNotification1.getSubject())
            .isEqualTo(
                String.format(
                    "(7-%s-1) Email Subject",
                    emailNotification1.getNotificationId())
            );

        assertThat(emailNotification1.getBodyPlain()).isEqualTo("Hi John your age is 30");

        assertThat(emailNotification2.getSubject())
            .isEqualTo(
                String.format(
                    "(7-%s-1) Email Subject",
                    emailNotification1.getNotificationId())
            );

        assertThat(emailNotification2.getBodyPlain()).isEqualTo("Hi Alice your age is 25");
    }

    @Test
    void shouldThrowWhenTemplateHasErrors() {
        Notification notification =
            notificationRepository.findActiveNotificationByType(NotificationType.AD_HOC_EMAIL)
                .orElse(null);
        String newLongText = notification.getLongText().replace("${Name}", "${naaame}");
        notification.setLongText(newLongText);
        notificationRepository.save(notification);

        assertThrows(EmailException.class, () -> cut.generateNotificationInstances());
    }

}
