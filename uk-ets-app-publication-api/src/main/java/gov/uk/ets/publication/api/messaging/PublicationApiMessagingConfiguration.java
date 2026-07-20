package gov.uk.ets.publication.api.messaging;

import gov.uk.ets.reports.model.messaging.ReportGenerationEvent;
import java.io.Serializable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

@Configuration
@Import(SharedKafkaConfig.class)
@Log4j2
@EnableKafka
@RequiredArgsConstructor
public class PublicationApiMessagingConfiguration {

    @Value("${kafka.report.consumer.group.id}")
    private String publicationApiConsumerGroupId;

    @Value("${kafka.report.transactional.id}")
    private String reportsTransactionalId;

    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean("publicationApiKafkaTemplate")
    KafkaTemplate<String, Serializable> publicationApiKafkaTemplate() {
        return sharedKafkaConfig.getKafkaTemplate(reportsTransactionalId, null);
    }

    @Bean("publicationApiListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<String, ReportGenerationEvent> publicationApiListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReportGenerationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(ReportGenerationEvent.class, publicationApiConsumerGroupId));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
