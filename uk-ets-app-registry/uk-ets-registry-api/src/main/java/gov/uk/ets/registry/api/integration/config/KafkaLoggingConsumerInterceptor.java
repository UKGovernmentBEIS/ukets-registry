package gov.uk.ets.registry.api.integration.config;

import gov.uk.ets.registry.api.integration.config.KafkaLoggingEntry.Type;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

@Log4j2
public class KafkaLoggingConsumerInterceptor<K, V> implements ConsumerInterceptor<K, V> {

    @Override
    public ConsumerRecords<K, V> onConsume(ConsumerRecords<K, V> records) {
        records.forEach(consumerRecord -> log.info(
            MessageExtractor.convertToMap(KafkaLoggingEntry.builder()
                .type(Type.CONSUMING)
                .correlationId(MessageExtractor.resolveHeader(KafkaConstants.CORRELATION_ID_HEADER, consumerRecord.headers()))
                .correlationParentId(
                    MessageExtractor.resolveHeader(KafkaConstants.CORRELATION_PARENT_ID_HEADER, consumerRecord.headers()))
                .clientId(MessageExtractor.resolveHeader(KafkaConstants.PRODUCER_CLIENT_ID_HEADER, consumerRecord.headers()))
                .recordKey(consumerRecord.key())
                .recordValue(MessageExtractor.resolveRecordValueAsMap(consumerRecord.value()))
                .topic(consumerRecord.topic())
                .partition(consumerRecord.partition())
                .offset(consumerRecord.offset())
                .build())));
        return records;
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {

    }

    @Override
    public void close() {

    }

}