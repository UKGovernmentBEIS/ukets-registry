package gov.uk.ets.registry.api.messaging.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

import java.io.Serializable;


@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class MessagingConfiguration {

    @Value("${kafka.account.opening-uktl.question.transactional.id}")
    private String transactionLogNotificationTransactionalId;

    private final SharedKafkaConfig sharedKafkaConfig;

    /**
     * Generates a KafkaTemplate.
     *
     * @return the KafkaTemplate.
     */
    @Bean("producerTemplate")
    @Primary
    KafkaTemplate<String, Serializable> transactionProducerTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(transactionLogNotificationTransactionalId, null);
    }
}
