package uk.ets.lib.kafka.deadletter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.kafka.core.ProducerFactory;
import uk.ets.lib.commons.kafkaconfig.KafkaConfigUtils;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.ets.lib.commons.kafkaconfig.UkEtsKafkaConfigProperties;

/**
 * {@link Configuration} that declares the beans definitions needed to create a {@link
 * ConcurrentKafkaListenerContainerFactoryBeanPostProcessor}
 */
@Configuration
public class DeadLetterConfig {


    /**
     * The {@link ProducerFactory} bean factory method. It creates the bean that is injected to {@link
     * #concurrentKafkaListenerContainerFactoryBeanPostProcessor(ProducerFactory, DeadLetterConfigProperties)}. It
     * depends on {@link PropertySourcesPlaceholderConfigurer} bean factory post processor.
     * <p>
     * Use {@link KafkaConfigUtils} to create an authentication-aware factory.
     *
     * @param kafkaBootstrapAddress The spring.kafka.bootstrap-servers property.
     * @return The {@link ProducerFactory} bean.
     */
    @DependsOn("propertySourcesPlaceholderConfigurer")
    @Bean
    public static ProducerFactory<String, String> deadLetterProducerFactory(
        @Value("${spring.kafka.bootstrap-servers:unknown}") String kafkaBootstrapAddress,
        KafkaProducerConfig kafkaProducerConfig) {
        return KafkaConfigUtils.createNonTransactionalKafkaProducerFactory(
            UkEtsKafkaConfigProperties.builder()
                .kafkaBootstrapAddress(kafkaBootstrapAddress)
                .kafkaProducerConfig(kafkaProducerConfig)
                .build());
    }

    /**
     * The {@link DeadLetterConfigProperties} bean factory method. It creates the {@link DeadLetterConfigProperties} bean
     * that is injected to {@link #concurrentKafkaListenerContainerFactoryBeanPostProcessor(ProducerFactory,
     * DeadLetterConfigProperties)}.
     *
     * @return The {@link DeadLetterConfigProperties} bean
     */
    @Bean
    public static DeadLetterConfigProperties deadLetterConfigProperties() {
        return new DeadLetterConfigProperties();
    }

    /**
     * The bean post processor factory method.
     * It creates the {@link ConcurrentKafkaListenerContainerFactoryBeanPostProcessor} bean.
     *
     * @param deadLetterProducerFactory  The {@link ProducerFactory} autowired bean
     * @param deadLetterConfigProperties The {@link DeadLetterConfigProperties} autowired bean
     * @return The {@link ConcurrentKafkaListenerContainerFactoryBeanPostProcessor} bean
     */
    @Bean
    public static ConcurrentKafkaListenerContainerFactoryBeanPostProcessor concurrentKafkaListenerContainerFactoryBeanPostProcessor(
        ProducerFactory<String, String> deadLetterProducerFactory,
        DeadLetterConfigProperties deadLetterConfigProperties) {
        return new ConcurrentKafkaListenerContainerFactoryBeanPostProcessor(deadLetterProducerFactory,
            deadLetterConfigProperties);
    }
}
