package gov.uk.ets.registry.api.common.test.itl;

import static gov.uk.ets.registry.api.itl.reconciliation.messaging.ITLReconciliationMessagingConfiguration.DEFAULT_ITL_RECONCILIATION_OUT_TOPIC;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.IsolationLevel;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EmbeddedKafka(
    // this is needed only for the embedded kafka test utils (like consumeFromAnEmbeddedTopic)
    topics = {
        DEFAULT_ITL_RECONCILIATION_OUT_TOPIC
    },
    count = 3,
    ports = {0, 0, 0},
    partitions = 3,
    controlledShutdown = true
)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(
    locations = "/integration-test-application.properties",
    properties = {
        "mail.fromAddress=test@test.gr",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.listener.missing-topics-fatal=false",
        "registry.file.max.errors.size=10",
        "kafka.authentication.enabled=false",
        "spring.datasource.hikari.maximum-pool-size=5"
    }
)
@ActiveProfiles("integrationTest") // disable security config
@Log4j2
public class IntegrationTestBase extends BasePostgresFixture {
    @Autowired
    protected EmbeddedKafkaBroker embeddedKafkaBroker;

    protected void callback(org.springframework.kafka.support.SendResult<String, Serializable> result) {
        if (result != null) {
            final RecordMetadata recordMetadata = result.getRecordMetadata();
            log.info("produced to {}, {}, {}", recordMetadata.topic(), recordMetadata.partition(),
                recordMetadata.offset());
        }
    }

    protected Map<String, Object> getConsumerProperties() {
        Map<String, Object> consumerProperties = KafkaTestUtils
            .consumerProps("KafkaTemplateTests" + UUID.randomUUID().toString(), "false", embeddedKafkaBroker);
        consumerProperties
            .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return consumerProperties;
    }
}
