package gov.uk.ets.registry.api.itl.reconciliation.messaging;

import java.io.Serializable;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;
import uk.gov.ets.lib.commons.kyoto.types.ReceiveTotalsRequest;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class ITLReconciliationMessagingConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.itl.reconciliation.consumer.json.trusted.package:*")
    private String trustedPackages;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    private final KafkaConsumerConfig consumerConfig;
    private final KafkaProducerConfig producerConfig;

    public static final String DEFAULT_ITL_RECONCILIATION_OUT_TOPIC = "itl.originating.reconciliation.out.topic";
    private static final String DEFAULT_ITL_RECONCILIATION_IN_TOPIC = "itl.originating.reconciliation.in.topic";

    /**
     * Creates a transactional KafkaTemplate
     */
    @Bean("itlReconciliationIncomingKafkaTemplate")
    public KafkaTemplate<String, Serializable> itlReconciliationIncomingKafkaTemplate(KafkaProperties kafkaProperties) {
        KafkaTemplate<String, Serializable> kafkaTemplate =
            new KafkaTemplate<>(itlReconciliationProducerFactory(kafkaProperties));
        kafkaTemplate.setDefaultTopic(DEFAULT_ITL_RECONCILIATION_IN_TOPIC);
        return kafkaTemplate;
    }

    /**
     * Creates a transactional KafkaTemplate
     */
    @Bean("itlReconciliationOutgoingKafkaTemplate")
    public KafkaTemplate<String, Serializable> itlReconciliationOutgoingKafkaTemplate(KafkaProperties kafkaProperties) {
        KafkaTemplate<String, Serializable> kafkaTemplate =
            new KafkaTemplate<>(itlReconciliationProducerFactory(kafkaProperties));
        kafkaTemplate.setDefaultTopic(DEFAULT_ITL_RECONCILIATION_OUT_TOPIC);
        return kafkaTemplate;
    }

    /**
     * Creates a transactional producer, for exactly-once delivery.
     */
    @Bean
    public ProducerFactory<String, Serializable> itlReconciliationProducerFactory(KafkaProperties kafkaProperties) {
        return KafkaConfigUtils
            .createStaticTransactionalIdKafkaProducerFactory(
                UkEtsKafkaConfigProperties.builder()
                    .maxAgeInMillis(maxAgeInMillis)
                    .transactionalId("itl-reconciliation-out")
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }

    private ConsumerFactory<String, ReceiveTotalsRequest> itlProvideTotalsConsumerFactory() {
        Map<String, Object> consumerProperties = consumerConfig.getCommonConfigurationProperties();
        consumerProperties
            .put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "itl-reconciliation-consumer");
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory<>(consumerProperties, stringDeserializer,
                getValueDeserializer());
        }
    }

    @Bean("itlProvideTotalsListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ReceiveTotalsRequest>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReceiveTotalsRequest>
            factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(itlProvideTotalsConsumerFactory());
        return factory;
    }

    /**
     * Logs information when the
     * {@link gov.uk.ets.registry.api.itl.reconciliation.service.ITLReconciliationProvideTotalsService#provideTotals}
     * method is retried.
     */
    @Bean
    public RetryListener retryListener() {
        return new RetryListener() {
            @Override
            public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
                log.warn("Retrying fired (open method)");
                return true;
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
                                                       Throwable throwable) {
                log.warn("Retrying fired (close method)");

            }

            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
                                                         Throwable throwable) {
                log.warn("Retrying fired (onError method)");

            }
        };
    }

    private JsonDeserializer<ReceiveTotalsRequest> getValueDeserializer() {
        JsonDeserializer<ReceiveTotalsRequest> jsonDeserializer =
            new JsonDeserializer<>(ReceiveTotalsRequest.class);
        jsonDeserializer.addTrustedPackages(trustedPackages);
        return jsonDeserializer;
    }
}
