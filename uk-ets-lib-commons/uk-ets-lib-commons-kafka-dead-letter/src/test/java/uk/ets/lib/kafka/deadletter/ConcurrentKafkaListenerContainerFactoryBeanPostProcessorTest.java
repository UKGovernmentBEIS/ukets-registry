package uk.ets.lib.kafka.deadletter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;

@ExtendWith(MockitoExtension.class)
class ConcurrentKafkaListenerContainerFactoryBeanPostProcessorTest {
    @Test
    void postProcessAfterInitialization() {
        //given
        ProducerFactory<String, String> producerFactory = Mockito.mock(ProducerFactory.class);

        DeadLetterConfigProperties properties = new DeadLetterConfigProperties();
        properties.setApplicationName("application-name");
        properties.setDeadLetterMaxAttempts(10);
        properties.setDeadLetterTopicPartition(0);
        ConcurrentKafkaListenerContainerFactoryBeanPostProcessor beanFactoryPostProcessor = new ConcurrentKafkaListenerContainerFactoryBeanPostProcessor(
            producerFactory, properties
        );
        ConcurrentKafkaListenerContainerFactory bean = Mockito.mock(ConcurrentKafkaListenerContainerFactory.class);
        given(bean.getContainerProperties()).willReturn(new ContainerProperties(""));
        ArgumentCaptor<SeekToCurrentErrorHandler> errorHandlerCaptor = ArgumentCaptor.forClass(SeekToCurrentErrorHandler.class);

        //when
        beanFactoryPostProcessor.postProcessAfterInitialization(bean, "a name");

        //then
        then(bean).should().setErrorHandler(errorHandlerCaptor.capture());
        SeekToCurrentErrorHandler errorHandler = errorHandlerCaptor.getValue();
        assertNotNull(errorHandler);
    }
}