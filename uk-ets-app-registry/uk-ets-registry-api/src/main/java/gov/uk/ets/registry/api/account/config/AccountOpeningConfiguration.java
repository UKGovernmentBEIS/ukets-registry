package gov.uk.ets.registry.api.account.config;

import gov.uk.ets.registry.api.account.messaging.UKTLAccountOpeningAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;


@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class AccountOpeningConfiguration {

    @Value("${kafka.account-opening-uktl.consumer.group-id}")
    private String accountOpeningUktlAnswerConsumerGroup;

    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean("uktlAccountOpeningConsumerFactory")
    ConcurrentKafkaListenerContainerFactory<String, UKTLAccountOpeningAnswer> uktlAccountOpeningConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UKTLAccountOpeningAnswer> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(UKTLAccountOpeningAnswer.class, accountOpeningUktlAnswerConsumerGroup));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
