package gov.uk.ets.registry.api.reconciliation.messaging;

import gov.uk.ets.registry.api.reconciliation.transfer.ReconciliationSummary;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

/**
 * Kafka template configuration for reconciliation process.
 */
@Configuration
@RequiredArgsConstructor
public class ReconciliationConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.reconciliation-uktl.consumer.group-id:reconciliation-uktl.group}")
    private String reconciliationUktlAnswerConsumerGroup;

    @Value("${kafka.reconciliation-uktl.consumer.json.trusted.package:*")
    private String reconciliationAnswerTrustedPackages;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    private final KafkaConsumerConfig consumerConfig;

    @Bean("uktlReconciliationProducerTemplate")
    public KafkaTemplate<String, ReconciliationSummary> uktlReconciliationProducerTemplate(
        KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils
            .createTransactionalKafkaTemplate(
                UkEtsKafkaConfigProperties.builder()
                    .maxAgeInMillis(maxAgeInMillis)
                    .transactionalId("tx-uktl-reconciliation")
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }

    @Bean("uktlReconciliationConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ReconciliationSummary>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReconciliationSummary>
            factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(uktlConsumerFactory());
        return factory;
    }

    private ConsumerFactory<String, ReconciliationSummary> uktlConsumerFactory() {
        Map<String, Object> props = consumerConfig.getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, reconciliationUktlAnswerConsumerGroup);

        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(props,
                stringDeserializer,
                getReconciliationAnswerJsonDeserializer());
        }
    }

    private JsonDeserializer<ReconciliationSummary> getReconciliationAnswerJsonDeserializer() {
        try (
            JsonDeserializer<ReconciliationSummary> jsonDeserializer
                = new JsonDeserializer<>(ReconciliationSummary.class)
                .ignoreTypeHeaders()) {
            jsonDeserializer.addTrustedPackages(reconciliationAnswerTrustedPackages);
            return jsonDeserializer;
        }
    }
}
