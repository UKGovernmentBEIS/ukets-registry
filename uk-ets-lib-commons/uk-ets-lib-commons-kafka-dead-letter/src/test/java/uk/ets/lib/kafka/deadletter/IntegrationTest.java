package uk.ets.lib.kafka.deadletter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

@Disabled
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@TestInstance(Lifecycle.PER_CLASS)
@EmbeddedKafka(
    topics = {
        "${spring.application.name}",
        "${spring.application.name}.DLT"
    },
    count = 3,
    ports = {0,0,0}
)
@TestPropertySource(properties = {
    "logging.level.root=OFF",
    "dead.letter.max.attempts=3",
    "spring.application.name=test-app",
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}",
    "kafka.authentication.enabled=false"})
@SpringBootTest(
    classes = {TestConsumer.class, TestConfig.class, DeadLetterConfig.class})
public class IntegrationTest {
    @Value("${spring.application.name}")
    String applicationName;

    @Value("${dead.letter.max.attempts}")
    int attempts;

    @Qualifier("testTemplate")
    @Autowired
    private KafkaOperations<String, String> kafkaTemplate;

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    @SpyBean
    protected TestConsumer testConsumer;

    Consumer<String, String> deadletterConsumer;

    @AfterAll
    protected void tearDown() {
        deadletterConsumer.close();
        embeddedKafkaBroker.destroy();
    }

    @Test
    void test() throws InterruptedException {
        // given
        String message = "the message";
        String expectedDeadLetterTopic = applicationName + ".DLT";
        Map<String, Object> config = new HashMap<>(KafkaTestUtils.consumerProps("transaction_log_messages", "true", embeddedKafkaBroker));
        deadletterConsumer =  new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(deadletterConsumer, expectedDeadLetterTopic);
        //when
        kafkaTemplate.send("the-topic", message);
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);
        // then
        then(testConsumer).should(times(attempts)).handleAnswer(eq(message));
        ConsumerRecord<String, String> consumerRecord = KafkaTestUtils.getSingleRecord(deadletterConsumer, expectedDeadLetterTopic);
        assertNotNull(consumerRecord);
        assertEquals(consumerRecord.value(), message);
    }
}
