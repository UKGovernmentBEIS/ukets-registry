package uk.ets.app.send.sms.config;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import uk.ets.app.send.sms.domain.SmsNotification;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.sms.notification.consumer.group-id}")
    private String smsNotificationConsumerGroup;

    @Value("${kafka.sms.notification.consumer.json.trusted.packages}")
    private String trustedPackages;

    @Value("${kafka.max.poll.records}")
    private String maxPollRecords;

    @Value("${kafka.idle.between.polls}")
    private Long idleBetweenPolls;

    @Bean
    public KafkaConsumerConfig kafkaConsumerConfig(KafkaProperties kafkaProperties) {
        return new KafkaConsumerConfig(kafkaProperties);
    }

    @Bean
    public KafkaProducerConfig kafkaProducerConfig(KafkaProperties kafkaProperties) {
        return new KafkaProducerConfig(kafkaProperties);
    }

    /**
     * The template bean.
     *
     * @return the KafkaTemplate bean
     */
    @Bean
    public KafkaTemplate<String, SmsNotification> kafkaTemplate(KafkaProducerConfig producerConfig) {
        return KafkaConfigUtils
            .createNonTransactionalKafkaTemplate(
                UkEtsKafkaConfigProperties.builder()
                    .kafkaBootstrapAddress(kafkaBootstrapAddress)
                    .kafkaProducerConfig(producerConfig)
                    .build());
    }

    private ConsumerFactory<String, SmsNotification> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaConsumerConfig(kafkaProperties).getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, smsNotificationConsumerGroup);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

        return new DefaultKafkaConsumerFactory<>(props,
            getStringDeserializer(),
            getJsonDeserializer());
    }

    private StringDeserializer getStringDeserializer() {
        return new StringDeserializer();
    }

    private ErrorHandlingDeserializer<SmsNotification> getJsonDeserializer() {
        try (JsonDeserializer<SmsNotification> jsonDeserializer =
                 new JsonDeserializer<>(SmsNotification.class).ignoreTypeHeaders()) {
            jsonDeserializer.addTrustedPackages(trustedPackages);
            return new ErrorHandlingDeserializer<>(jsonDeserializer);
        }
    }

    /**
     * Creates a groupNotificationConsumerFactory bean.
     *
     * @return the groupNotificationConsumerFactory bean
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SmsNotification> kafkaListenerContainerFactory(KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, SmsNotification> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        factory.getContainerProperties().setIdleBetweenPolls(idleBetweenPolls);
        return factory;
    }

}
