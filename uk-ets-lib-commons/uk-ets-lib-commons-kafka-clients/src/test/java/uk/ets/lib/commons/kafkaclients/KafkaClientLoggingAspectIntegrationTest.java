package uk.ets.lib.commons.kafkaclients;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.then;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;

@Disabled
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@TestInstance(Lifecycle.PER_CLASS)
@EmbeddedKafka(
    topics = {
        "success-topic",
    },
    brokerProperties = "auto.create.topics.enable=false",
    count = 3,
    ports = {0,0,0}
)
@TestPropertySource(properties = {
    "logging.level.root=OFF",
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}",
    "kafka.authentication.enabled=false"})
@SpringBootTest(classes = {TestConfig.class, KafkaClientsConfiguration.class, TestKafkaClient.class})
class KafkaClientLoggingAspectIntegrationTest {
    @SpyBean
    Logger log;

    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    TestKafkaClient kafkaClient;

    /**
     * TODO: Find out how to assert that the right message has been logged.
     */
    @Test
    void test() {
        String data = "a message that failed to be delivered";
        String topic = "non existed topic";
       try{
           kafkaClient.sendToKafka(topic, data).get();
       } catch (Exception e) {
       }
       then(log).should().error(contains(data + " failed to be sent to topic with name: " + topic));
    }

    @AfterAll
    public void teardown() {
        embeddedKafkaBroker.destroy();
    }
}
