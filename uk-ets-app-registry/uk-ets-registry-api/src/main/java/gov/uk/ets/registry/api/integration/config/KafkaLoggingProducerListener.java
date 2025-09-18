package gov.uk.ets.registry.api.integration.config;

import gov.uk.ets.registry.api.integration.config.KafkaLoggingEntry.Type;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.lang.Nullable;

@Log4j2
public class KafkaLoggingProducerListener<K, V> implements ProducerListener<K, V> {

    @Override
    public void onSuccess(ProducerRecord<K, V> producerRecord, RecordMetadata recordMetadata) {
        log.info(
            MessageExtractor.convertToMap(KafkaLoggingEntry.builder()
                .type(Type.PRODUCING)
                .correlationId(MessageExtractor.resolveHeader(KafkaConstants.CORRELATION_ID_HEADER, producerRecord.headers()))
                .correlationParentId(MessageExtractor.resolveHeader(KafkaConstants.CORRELATION_PARENT_ID_HEADER, producerRecord.headers()))
                .clientId(MessageExtractor.resolveHeader(KafkaConstants.PRODUCER_CLIENT_ID_HEADER, producerRecord.headers()))
                .recordKey(producerRecord.key())
                .recordValue(MessageExtractor.resolveRecordValueAsMap(producerRecord.value()))
                .topic(producerRecord.topic())
                .partition(recordMetadata.partition())
                .offset(recordMetadata.offset())
                .build()));
    }

    @Override
    public void onError(ProducerRecord<K, V> producerRecord, @Nullable RecordMetadata recordMetadata, Exception exception) {
        log.error(
            MessageExtractor.convertToMap(KafkaLoggingEntry.builder()
                .type(Type.PRODUCING)
                .correlationId(MessageExtractor.resolveHeader(KafkaConstants.CORRELATION_ID_HEADER, producerRecord.headers()))
                .clientId(MessageExtractor.resolveHeader(KafkaConstants.PRODUCER_CLIENT_ID_HEADER, producerRecord.headers()))
                .recordKey(producerRecord.key())
                .recordValue(MessageExtractor.resolveRecordValueAsMap(producerRecord.value()))
                .topic(producerRecord.topic())
                .partition(recordMetadata != null ? recordMetadata.partition() : producerRecord.partition())
                .offset(recordMetadata != null ? recordMetadata.offset() : null)
                .build()), exception);
    }
}
