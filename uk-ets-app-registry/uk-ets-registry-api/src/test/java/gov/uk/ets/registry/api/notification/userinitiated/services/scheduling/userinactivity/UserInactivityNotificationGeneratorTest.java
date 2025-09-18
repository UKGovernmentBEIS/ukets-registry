package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.userinactivity;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.userinitiated.NotificationModelTestHelper;
import gov.uk.ets.registry.api.notification.userinitiated.domain.Notification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.domain.NotificationSchedule;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.BaseNotificationParameters;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.uk.ets.registry.api.notification.userinitiated.services.UserInitiatedNotificationService;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.NotificationUpdater;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.time.LocalDateTime;
import java.time.ZoneId;


@PostgresJpaTest
@DataJpaTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*notification.userinitiated.services.scheduling.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*notification.userinitiated.repository.*")
})
@Import({Configuration.class, StringEmailTemplateProcessor.class})
public class UserInactivityNotificationGeneratorTest {


    private static final String FIRST_NAME = "UserFirst";
    private static final String LAST_NAME = "UserLast";
    private static final String USER_ID = "UK12456K";    
    private static final String TEMPLATE = "Hello Mr  ${user.firstName} ${user.lastName}\n\n" +
        "Your user ID, is: ${user.urid}\n";
    public static final String TEST_EMAIL = "test@test.com";

    @Autowired
    UserInactivityNotificationGenerator cut;
    @Autowired
    private NotificationDefinitionRepository definitionRepository;

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

    NotificationParameterHolder parameterHolder;
    NotificationDefinition inactiveUserDefinition;
    Notification notificationEntity;

    @BeforeEach
    void setUp() {
        parameterHolder = NotificationParameterHolder.builder()
            .email(TEST_EMAIL)
            .email(TEST_EMAIL)
            .baseNotificationParameters(BaseNotificationParameters.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .urid(USER_ID)
                .build())
            .build();

        inactiveUserDefinition = definitionRepository.findByType(NotificationType.USER_INACTIVITY).orElse(null);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        notificationEntity = Notification.builder()
                .definition(inactiveUserDefinition)
                .status(NotificationStatus.ACTIVE)
                .schedule(NotificationSchedule.builder()
                        .startDateTime(now)
                        .endDateTime(now.plusDays(2))
                        .runEveryXDays(1)
                        .build())
                .creator(NotificationModelTestHelper.INACTIVE_USER_TEST_URID)
                .shortText("InactiveUser Subject")
                .longText(NotificationModelTestHelper.INACTIVE_USER_TEMPLATE)
                .build();
    }

    @Test
    public void shouldParseTemplateParams() {

        IdentifiableEmailNotification email = cut.generate(parameterHolder, notificationEntity.getShortText(), notificationEntity.getLongText());

        assertThat(email).isNotNull();
        assertThat(email.getRecipients()).containsExactly(TEST_EMAIL);
        assertThat(email.getBodyHtml()).contains(FIRST_NAME, LAST_NAME, USER_ID);
    }

    @Test
    void shouldFailForUnknownParams() {
        assertThrows(Exception.class, () -> cut.generate(parameterHolder, notificationEntity.getShortText(), "hello ${unknown}"));
    }
}
