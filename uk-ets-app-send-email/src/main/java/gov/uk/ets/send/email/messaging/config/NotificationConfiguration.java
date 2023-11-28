package gov.uk.ets.send.email.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import gov.uk.ets.send.email.messaging.domain.GroupNotification;
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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

import java.util.Map;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class NotificationConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.group.notification.consumer.group-id}")
    private String groupNotificationConsumerGroup;

    @Value("${kafka.group.notification.consumer.json.trusted.packages}")
    private String trustedPackages;

    @Value("${kafka.max.poll.records}")
    private String maxPollRecords;

    @Value("${kafka.idle.between.polls}")
    private Long idleBetweenPolls;

    private final KafkaConsumerConfig consumerConfig;

    /**
     * The template bean.
     *
     * @return the KafkaTemplate bean
     */
    @Bean
    public KafkaTemplate<String, GroupNotification> kafkaTemplate(KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils
                .createNonTransactionalKafkaTemplate(
                        UkEtsKafkaConfigProperties.builder()
                                .kafkaBootstrapAddress(kafkaBootstrapAddress)
                                .kafkaProducerConfig(producerConfig)
                                .jsonSerializer(getJsonSerializer())
                                .build());
    }

    private ConsumerFactory<String, GroupNotification> consumerFactory() {
        Map<String, Object> props = consumerConfig.getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupNotificationConsumerGroup);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

        return new DefaultKafkaConsumerFactory<>(props,
                getStringDeserializer(),
                getJsonDeserializer());
    }

    private StringDeserializer getStringDeserializer() {
        return new StringDeserializer();
    }

    private ErrorHandlingDeserializer<GroupNotification> getJsonDeserializer() {
        try (JsonDeserializer<GroupNotification> jsonDeserializer =
                     new JsonDeserializer<>(GroupNotification.class).ignoreTypeHeaders()) {
            jsonDeserializer.addTrustedPackages(trustedPackages);
            return new ErrorHandlingDeserializer<>(jsonDeserializer);
        }
    }

    private JsonSerializer<GroupNotification> getJsonSerializer() {
        return new JsonSerializer<>(getObjectMapper());
    }

    private ObjectMapper getObjectMapper() {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
                .builder()
                .allowIfBaseType(GroupNotification.class)
                .allowIfBaseType(Set.class)
                .build();
        return JsonMapper.builder()
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
                .build();
    }

    /**
     * Creates a groupNotificationConsumerFactory bean.
     *
     * @return the groupNotificationConsumerFactory bean
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GroupNotification> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GroupNotification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setIdleBetweenPolls(idleBetweenPolls);
        return factory;
    }

}
