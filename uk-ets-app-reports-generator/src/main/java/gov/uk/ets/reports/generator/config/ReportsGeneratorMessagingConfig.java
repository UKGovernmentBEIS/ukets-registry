package gov.uk.ets.reports.generator.config;

import gov.uk.ets.reports.generator.export.ReportGeneratorService;
import gov.uk.ets.reports.generator.kyotoprotocol.KyotoProtocolReportGeneratorService;
import gov.uk.ets.reports.generator.messaging.ReportGenerationCommandMessageListener;
import gov.uk.ets.reports.generator.messaging.ReportGenerationCommandMessageListenerErrorHandler;
import gov.uk.ets.reports.generator.messaging.ReportOutcomeMessageService;
import gov.uk.ets.reports.model.messaging.ReportGenerationCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import uk.ets.lib.commons.kafkaconfig.SharedKafkaConfig;

import java.io.Serializable;


/**
 * Configuration.
 */
@Configuration
@Import(SharedKafkaConfig.class)
@RequiredArgsConstructor
public class ReportsGeneratorMessagingConfig {


    @Value("${spring.kafka.consumer.group-id}")
    private String reportGenerationCommandConsumerGroup;

    private final SharedKafkaConfig sharedKafkaConfig;

    @Bean
    KafkaListenerErrorHandler reportGenerationCommandMessageListenerErrorHandler(
        ReportOutcomeMessageService reportOutcomeMessageService) {
        return new ReportGenerationCommandMessageListenerErrorHandler(reportOutcomeMessageService);
    }

    @Bean
    ReportGenerationCommandMessageListener reportGenerationCommandMessageListener(
            ReportGeneratorService reportGeneratorService, KyotoProtocolReportGeneratorService kyotoProtocolReportGeneratorService) {
        return new ReportGenerationCommandMessageListener(reportGeneratorService, kyotoProtocolReportGeneratorService);
    }

    /**
     * The template bean.
     *
     * @return the KafkaTemplate bean
     */

    @Bean("reportProducerTemplate")
    KafkaTemplate<String, Serializable> reportProducerTemplate() {
        return sharedKafkaConfig.getNonTransactionalKafkaTemplate(null);
    }

    @Bean("reportGenerationCommandConsumerFactory")
    ConcurrentKafkaListenerContainerFactory<String, ReportGenerationCommand> reportGenerationCommandConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReportGenerationCommand> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sharedKafkaConfig.consumerFactory(ReportGenerationCommand.class, reportGenerationCommandConsumerGroup));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
