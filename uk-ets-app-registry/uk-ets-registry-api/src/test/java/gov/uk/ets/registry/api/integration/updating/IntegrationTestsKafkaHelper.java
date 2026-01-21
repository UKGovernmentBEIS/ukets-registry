package gov.uk.ets.registry.api.integration.updating;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;

import java.time.Duration;
import java.util.List;

@Component
public class IntegrationTestsKafkaHelper {

    public void sendKafkaRequest(
            String REQUEST_TOPIC,
            AccountUpdatingEvent event,
            String correlationId,
            KafkaTemplate<String, AccountUpdatingEvent> kafkaTemplate
    ) {
        ProducerRecord<String, AccountUpdatingEvent> record =
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

    public AccountUpdatingEventOutcome receiveOutcome(
            Consumer<String, AccountUpdatingEventOutcome> responseConsumer,
            String correlationId) {

        var records = KafkaTestUtils.getRecords(responseConsumer, Duration.ofSeconds(15));
        return extractByCorrelation(records, correlationId);
    }

    public AccountUpdatingEventOutcome extractByCorrelation(
            org.apache.kafka.clients.consumer.ConsumerRecords<String, AccountUpdatingEventOutcome> records,
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
