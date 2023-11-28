package uk.gov.ets.kp.webservices.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest(properties = {"kafka.authentication.enabled=true"})
@ContextConfiguration(classes = Config.class)
class KafkaConfigTest {

    @Autowired
    Config kafkaConfig;

    @Autowired
    KafkaTopics kafkaTopics;

    @Autowired
    Environment env;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Test
    public void shouldSetSecurityConfig() {
        DefaultKafkaProducerFactory defaultKafkaProducerFactory = kafkaConfig.producerFactory();

        assertEquals("SASL_PLAINTEXT",
            defaultKafkaProducerFactory.getConfigurationProperties().get("security.protocol"));
        assertEquals("SCRAM-SHA-512", defaultKafkaProducerFactory.getConfigurationProperties().get("sasl.mechanism"));
    }

    @Test
    public void testKafkaTopics() {
        assertEquals(env.getProperty("spring.kafka.template.default-topic"), kafkaTopics.getDefaultTopic());
        assertEquals(env.getProperty("spring.kafka.template.itl-notices-topic"), kafkaTopics.getNoticesKafkaTopic());
        assertEquals(env.getProperty("spring.kafka.template.reconciliation-in-topic"), kafkaTopics.getReconciliationInTopic());
        assertEquals(kafkaTopics.getDefaultTopic(), kafkaTemplate.getDefaultTopic());
    }
}
