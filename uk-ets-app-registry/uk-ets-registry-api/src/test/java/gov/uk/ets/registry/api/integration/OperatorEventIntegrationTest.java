package gov.uk.ets.registry.api.integration;

import static org.assertj.core.api.Assertions.assertThat;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.notification.EmailNotification;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;

@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "kafka.integration.enabled=true",
    "kafka.integration.installation.set.operator.enabled=false",
    "kafka.integration.aviation.set.operator.enabled=false",
    "kafka.integration.maritime.set.operator.enabled=true"
})
@EmbeddedKafka(partitions = 1, topics = { "maritime-set-operator-response-topic-dlt", "group.notification.topic" })
@Disabled
class OperatorEventIntegrationTest extends BaseIntegrationTest {

    private static final String OPERATOR_RESPONSE_TOPIC = "maritime-set-operator-response-topic";
    private static final String OPERATOR_RESPONSE_DLT_TOPIC = OPERATOR_RESPONSE_TOPIC + "-dlt";
    private static final String GROUP_NOTIFICATION_TOPIC = "group.notification.topic";
    private static final String DLT_CONSUMER_GROUP = "dlt-group";
    private static final String NOTIFICATION_CONSUMER_GROUP = "email-group";
    private KafkaTemplate<String, OperatorUpdateEventOutcome> kafkaTemplate;
    private Consumer<String, String> dltConsumer;
    private Consumer<String, EmailNotification> emailConsumer;

    @Autowired
    private Configuration freemarkerConfiguration;


    @BeforeAll
    public void init() {
        // Setup kafka template (producer)
        DefaultKafkaProducerFactory<String, OperatorUpdateEventOutcome> producerFactory =
            new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                new StringSerializer(),
                new JsonSerializer<>());
        kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // Setup kafka consumers
        /**** DLT ***/
        dltConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs(DLT_CONSUMER_GROUP), new StringDeserializer(),
            new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(dltConsumer, OPERATOR_RESPONSE_DLT_TOPIC);

        /**** Email ***/
        JsonDeserializer<EmailNotification> emailDeserializer = new JsonDeserializer<>(EmailNotification.class);
        emailConsumer = new DefaultKafkaConsumerFactory<>(
            consumerConfigs(NOTIFICATION_CONSUMER_GROUP), new StringDeserializer(), emailDeserializer).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(emailConsumer, GROUP_NOTIFICATION_TOPIC);
    }

    /**
     * Consumer configuration.
     *
     * @param groupId
     * @return
     */
    private Map<String, Object> consumerConfigs(String groupId) {
        Map<String, Object> config =
            new HashMap<>(KafkaTestUtils.consumerProps(groupId, "false", embeddedKafkaBroker));
        // make sure that we read transactionally committed messages
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        // make sure that we do not read old messages in case of re-balancing
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return config;
    }

    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/templates/email");
    }

    @AfterEach
    protected void cleanUp() {
        emailConsumer.commitSync();
        dltConsumer.commitSync();
    }

    @AfterAll
    protected void tearDown() {
        dltConsumer.close();
        emailConsumer.close();
        super.tearDown();
    }

    @Test
    void testOperatorUpdateEventOutcomeWithBusinessError() {
        // given
        OperatorUpdateEventOutcome outcome = new OperatorUpdateEventOutcome();
        OperatorUpdateEvent event = new OperatorUpdateEvent();
        event.setEmitterId("EmitterId");
        event.setOperatorId(123456L);
        event.setRegulator(RegulatorType.EA.name());
        outcome.setEvent(event);

        IntegrationEventErrorDetails errorDetails =
            new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0200, "Something is wrong");
        outcome.setErrors(List.of(errorDetails));
        outcome.setOutcome(IntegrationEventOutcome.ERROR);

        RecordHeader recordHeader = new RecordHeader("Correlation-Id", "correlationId".getBytes());
        ProducerRecord<String, OperatorUpdateEventOutcome> providedEvent =
            new ProducerRecord<>(OPERATOR_RESPONSE_TOPIC, null, null, "123456", outcome, List.of(recordHeader));

        // when
        kafkaTemplate.send(providedEvent);
        kafkaTemplate.flush();

        // then
        ConsumerRecords<String, EmailNotification> emailRecords =
            KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 1);
        assertThat(emailRecords.count()).isEqualTo(1);
    }

    @Test
    void testOperatorUpdateEventOutcomeInvalidMessage() {
        // given
        DefaultKafkaProducerFactory<String, String> producerFactory =
            new DefaultKafkaProducerFactory<>(KafkaTestUtils.producerProps(embeddedKafkaBroker),
                new StringSerializer(),
                new StringSerializer());
        KafkaTemplate<String, String> stringKafkaTemplate = new KafkaTemplate<>(producerFactory);

        // when
        stringKafkaTemplate.send(OPERATOR_RESPONSE_TOPIC, "test");
        kafkaTemplate.flush();

        // then
        ConsumerRecords<String, String>
            records = KafkaTestUtils.getRecords(dltConsumer, Duration.ofMillis(10000), 1);
        assertThat(records.count()).isEqualTo(1);
        Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
        ConsumerRecord<String, String> invalidFormat = iterator.next();
        assertThat(invalidFormat.key()).isNull();
        assertThat(invalidFormat.value()).isEqualTo("test");
    }
}
