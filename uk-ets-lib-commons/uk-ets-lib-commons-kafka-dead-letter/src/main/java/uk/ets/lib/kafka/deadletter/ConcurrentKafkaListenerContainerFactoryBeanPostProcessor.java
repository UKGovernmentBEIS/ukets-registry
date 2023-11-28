package uk.ets.lib.kafka.deadletter;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.lang.Nullable;
import org.springframework.util.backoff.FixedBackOff;

/**
 * {@link BeanPostProcessor} that adds a {@link DeadLetterPublishingRecoverer} error handler to every {@link
 * ConcurrentKafkaListenerContainerFactory} bean during Spring context loading. By default the Dead letter topic name is
 * the name of the application concatenated with the .DLT suffix.
 */
@Log4j2
public class ConcurrentKafkaListenerContainerFactoryBeanPostProcessor implements BeanPostProcessor {

    private DeadLetterConfigProperties properties;

    private final KafkaTemplate template;

    public ConcurrentKafkaListenerContainerFactoryBeanPostProcessor(
        ProducerFactory<String, String> deadLetterProducerFactory,
        DeadLetterConfigProperties properties) {
        this.template = new KafkaTemplate<>(deadLetterProducerFactory);
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ConcurrentKafkaListenerContainerFactory) {
            ConcurrentKafkaListenerContainerFactory factory = (ConcurrentKafkaListenerContainerFactory) bean;

            DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(template,
                (cr, e) -> {
                    logException(e);
                    return new TopicPartition(properties.getApplicationName() + ".DLT",
                        properties.getDeadLetterTopicPartition());
                });

            FixedBackOff fixedBackOff = new FixedBackOff(0L, properties.getDeadLetterMaxAttempts() - 1);

            if (factory.getContainerProperties().getTransactionManager() != null) {
                DefaultAfterRollbackProcessor<String, String> processor =
                    new DefaultAfterRollbackProcessor(deadLetterPublishingRecoverer, fixedBackOff, template, false);
                factory.setAfterRollbackProcessor(processor);
            } else {
                SeekToCurrentErrorHandler seekToCurrentErrorHandler =
                    new SeekToCurrentErrorHandler(deadLetterPublishingRecoverer, fixedBackOff);
                factory.setErrorHandler(seekToCurrentErrorHandler);
            }

        }
        return bean;
    }

    /**
     * TODO: I was not able to use both SeekToCurrentErrorHandler and e.g. LoggingErrorHandler,
     * so for the moment the exception is logged manually.
     * If we do not log it here, the message is sent to the dead letter topic but nothing is logged
     */
    private void logException(Exception e) {
        if (e.getCause() instanceof DeserializationException) {
            log.error("Could not deserialize message, sent to dead letter topic",
                ((DeserializationException) e.getCause()).getMostSpecificCause());
        }
    }
}
