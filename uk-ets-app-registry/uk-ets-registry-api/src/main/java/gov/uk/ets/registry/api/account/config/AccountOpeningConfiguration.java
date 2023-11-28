package gov.uk.ets.registry.api.account.config;

import gov.uk.ets.registry.api.account.messaging.UKTLAccountOpeningAnswer;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;


@Configuration
@RequiredArgsConstructor
public class AccountOpeningConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.account-opening-uktl.consumer.group-id}")
    private String accountOpeningUktlAnswerConsumerGroup;

    @Value("${kafka.account-opening-uktl.consumer.json.trusted.package")
    private String accountOpeningAnswerTrustedPackages;

    private final KafkaConsumerConfig consumerConfig;

    @Bean("uktlAccountOpeningConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UKTLAccountOpeningAnswer>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UKTLAccountOpeningAnswer> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(uktlConsumerFactory());
        return factory;
    }

    private ConsumerFactory<String, UKTLAccountOpeningAnswer> uktlConsumerFactory() {
        Map<String, Object> props = consumerConfig.getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, accountOpeningUktlAnswerConsumerGroup);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (StringDeserializer stringDeserializer = new StringDeserializer()) {
            return new DefaultKafkaConsumerFactory(props,
                                                   stringDeserializer,
                                                   getAccountOpeningAnswerJsonDeserializer());
        }
    }

    private JsonDeserializer<UKTLAccountOpeningAnswer> getAccountOpeningAnswerJsonDeserializer() {
        try (
            JsonDeserializer<UKTLAccountOpeningAnswer> jsonDeserializer
                = new JsonDeserializer<>(UKTLAccountOpeningAnswer.class).ignoreTypeHeaders()) {
            jsonDeserializer.addTrustedPackages(accountOpeningAnswerTrustedPackages);
            return jsonDeserializer;
        }
    }
}
