package gov.uk.ets.registry.api.integration.metscontacts;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;

import java.time.Duration;
import java.util.List;

@Component
public class MetsContactsIntegrationTestsKafkaHelper {

    public void sendKafkaRequest(
            String REQUEST_TOPIC,
            MetsContactsEvent event,
            String correlationId,
            KafkaTemplate<String, MetsContactsEvent> kafkaTemplate
    ) {
        ProducerRecord<String, MetsContactsEvent> record =
                new ProducerRecord<>(
                        REQUEST_TOPIC,
                        null,
                        null,
                        "integration-test",
                        event,
                        List.of(new RecordHeader("Correlation-Id", correlationId.getBytes()))
                );

        kafkaTemplate.send(record);
    }

    public MetsContactsEventOutcome receiveOutcome(
            Consumer<String, MetsContactsEventOutcome> responseConsumer,
            String correlationId) {

        var records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(15));
        return extractByCorrelation(records, correlationId);
    }

    public MetsContactsEventOutcome extractByCorrelation(
            org.apache.kafka.clients.consumer.ConsumerRecords<String, MetsContactsEventOutcome> records,
            String correlationId) {

        for (var rec : records) {
            var header = rec.headers().lastHeader("Correlation-Id");
            if (header == null) {
                continue;
            }
            String value = new String(header.value(), java.nio.charset.StandardCharsets.UTF_8);
            if (correlationId.equals(value)) {
                return rec.value();
            }
        }
        return null;
    }
}
