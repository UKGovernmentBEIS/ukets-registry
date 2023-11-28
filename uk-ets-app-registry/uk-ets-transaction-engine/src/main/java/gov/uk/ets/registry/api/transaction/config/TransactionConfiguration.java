package gov.uk.ets.registry.api.transaction.config;

import gov.uk.ets.registry.api.transaction.messaging.TransactionNotification;
import gov.uk.ets.registry.api.transaction.messaging.UKTLTransactionAnswer;
import java.io.Serializable;
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


@Configuration
@RequiredArgsConstructor
public class TransactionConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.transaction-uktl.consumer.group-id}")
    private String transactionUktlAnswerConsumerGroup;

    @Value("${kafka.transaction-uktl.consumer.json.trusted.package")
    private String transactionAnswerTrustedPackages;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    private final KafkaConsumerConfig consumerConfig;

    @Bean
    public KafkaTemplate<String, Serializable> itlTransactionProducerTemplate(KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils
            .createTransactionalKafkaTemplate(
                UkEtsKafkaConfigProperties.builder()
                    .maxAgeInMillis(maxAgeInMillis)
                    .transactionalId("tx-itl-transaction")
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }

    @Bean
    public KafkaTemplate<String, TransactionNotification> uktlTransactionProducerTemplate(
        KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils
            .createTransactionalKafkaTemplate(
                UkEtsKafkaConfigProperties.builder()
                    .maxAgeInMillis(maxAgeInMillis)
                    .transactionalId("tx-uktl-transaction")
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }

    @Bean("uktlTransactionConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UKTLTransactionAnswer>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UKTLTransactionAnswer> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(uktlConsumerFactory());
        return factory;
    }

    private ConsumerFactory<String, UKTLTransactionAnswer> uktlConsumerFactory() {
        Map<String, Object> props = consumerConfig.getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, transactionUktlAnswerConsumerGroup);

        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory(props,
                stringDeserializer,
                getTransactionAnswerJsonDeserializer());
        }
    }

    private JsonDeserializer<UKTLTransactionAnswer> getTransactionAnswerJsonDeserializer() {
        try (
            JsonDeserializer<UKTLTransactionAnswer> jsonDeserializer
                = new JsonDeserializer<>(UKTLTransactionAnswer.class).ignoreTypeHeaders()) {
            jsonDeserializer.addTrustedPackages(transactionAnswerTrustedPackages);
            return jsonDeserializer;
        }
    }
}
