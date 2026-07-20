package gov.uk.ets.reports.api.messaging;

import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

import java.io.Serializable;

@Configuration
@Import(SharedKafkaConfig.class)
@EnableKafka
@RequiredArgsConstructor
public class ReportApiMessagingConfiguration {

    @Value("${kafka.report.consumer.group.id}")
    private String reportsApiConsumerGroupId;

    @Value("${kafka.report.transactional.id}")
    private String reportsTransactionalId;

    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean("reportsApiKafkaTemplate")
    KafkaTemplate<String, Serializable> reportsApiKafkaTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(reportsTransactionalId, null);
    }

    @Bean("reportsApiListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, ReportGenerationEvent> reportsApiListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReportGenerationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(ReportGenerationEvent.class, reportsApiConsumerGroupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
