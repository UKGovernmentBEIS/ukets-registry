package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhocemail;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.notification.EmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.NotificationModelTestHelper;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationDefinitionRepository;
import gov.uk.ets.registry.api.notification.userinitiated.repository.NotificationRepository;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.SchedulingNotificationService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdHocEmailIntegrationTest extends BaseIntegrationTest {

    private static final String NOTIFICATION_EMAIL_TOPIC = "group.notification.topic";

    @Autowired
    private Configuration freemarkerConfiguration;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SchedulingNotificationService schedulingNotificationService;

    @Autowired
    private NotificationDefinitionRepository notificationDefinitionRepository;

    @Autowired
    private UploadedFilesRepository uploadedFilesRepository;

    private NotificationModelTestHelper notificationModelTestHelper;

    private Consumer<String, EmailNotification> emailConsumer;

    @BeforeAll
    public void init() {
        JsonDeserializer<EmailNotification> emailDeserializer = new JsonDeserializer<>(EmailNotification.class);
        emailDeserializer.addTrustedPackages("*"); // Prevents deserialization issues

        emailConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs("email-group"), new StringDeserializer(), emailDeserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(emailConsumer, NOTIFICATION_EMAIL_TOPIC);

        notificationModelTestHelper = new NotificationModelTestHelper(notificationDefinitionRepository, notificationRepository, uploadedFilesRepository);
    }

    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> config =
            new HashMap<>(KafkaTestUtils.consumerProps(groupId, "false", embeddedKafkaBroker));
        // Ensure transactional messages are read
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_uncommitted");
        // Read only new messages
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return config;
    }

    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates/email");
        notificationModelTestHelper.createAndSaveAdHocEmailNotification();
        notificationRepository.flush();
    }

    @AfterEach
    protected void cleanUp() {
        emailConsumer.commitSync();
        notificationRepository.deleteAll();
    }

    @AfterAll
    protected void tearDown() {
        emailConsumer.close();
        super.tearDown();
    }

    @Test
    void shouldScheduleNotificationsAndSendToKafka() throws InterruptedException {

        assertTrue(notificationRepository.findActiveNotificationByType(NotificationType.AD_HOC_EMAIL).isPresent());
        schedulingNotificationService.scheduleNotifications();

        // Wait for Kafka messages
        ConsumerRecords<String, EmailNotification> records =
            KafkaTestUtils.getRecords(emailConsumer, Duration.ofSeconds(10), 2);

        // Assert that exactly 2 records are received
        assertEquals(2, records.count(), "Expected exactly 2 messages in Kafka");

        // Expected values (adjust these as needed for your test setup)
        Set<String> expectedRecipients1 = Set.of("john@example.com");
        String expectedBody1 = "Hi John your age is 30";

        Set<String> expectedRecipients2 = Set.of("alice@example.com");
        String expectedBody2 = "Hi Alice your age is 25";

        List<EmailNotification> notifications = new ArrayList<>();
        for (ConsumerRecord<String, EmailNotification> record : records) {
            assertNotNull(record.value(), "EmailNotification should not be null");
            assertInstanceOf(EmailNotification.class, record.value(), "Expected an EmailNotification instance");
            notifications.add(record.value());
        }

        // Assert the first notification
        EmailNotification notification1 = notifications.get(0);
        assertEquals(expectedRecipients1, notification1.getRecipients(), "Unexpected recipients in first notification");
        assertTrue(notification1.getSubject().contains("Email Subject"));
        assertEquals(expectedBody1, notification1.getBodyPlain(), "Unexpected plain body in first notification");

        // Assert the second notification
        EmailNotification notification2 = notifications.get(1);
        assertEquals(expectedRecipients2, notification2.getRecipients(), "Unexpected recipients in second notification");
        assertTrue(notification2.getSubject().contains("Email Subject"));
        assertEquals(expectedBody2, notification2.getBodyPlain(), "Unexpected plain body in second notification");
    }

}
