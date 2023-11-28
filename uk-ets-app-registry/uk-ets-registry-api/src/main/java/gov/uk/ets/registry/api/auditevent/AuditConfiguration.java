package gov.uk.ets.registry.api.auditevent;

import gov.uk.ets.registry.api.auditevent.types.AuditCommand;
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
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

@Configuration
@RequiredArgsConstructor
public class AuditConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.domain.event.consumer.group-id}")
    private String auditCommandConsumerGroup;

    @Value("${kafka.domain.event.consumer.json.trusted.packages}")
    private String trustedPackages;

    private final KafkaConsumerConfig consumerConfig;

    @Bean("auditCommandProducerTemplate")
    public KafkaTemplate<String, DomainEvent> auditCommandProducerTemplate(KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils.createNonTransactionalKafkaTemplate(
            UkEtsKafkaConfigProperties.builder()
                .kafkaBootstrapAddress(kafkaBootstrapAddress)
                .kafkaProducerConfig(producerConfig)
                .jsonSerializer(new JsonSerializer<AuditCommand>())
                .build());
    }

    private ConsumerFactory<String, DomainEvent> consumerFactory() {
        Map<String, Object> props = consumerConfig.getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, auditCommandConsumerGroup);

        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory(props,
                stringDeserializer,
                getAuditCommandJsonDeserializer());
        }

    }

    private JsonDeserializer<DomainEvent> getAuditCommandJsonDeserializer() {

        try (JsonDeserializer<DomainEvent> jsonDeserializer = new JsonDeserializer<>(DomainEvent.class)
            .ignoreTypeHeaders()) {
            jsonDeserializer.addTrustedPackages(trustedPackages);
            return jsonDeserializer;
        }

    }

    @Bean("domainEventConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DomainEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DomainEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
