package uk.ets.lib.commons.kafkaconfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@ExtendWith(MockitoExtension.class)
class KafkaConfigUtilsTest {

    String kafkaBootstrapAddress;

    @BeforeEach
    void init() {
        kafkaBootstrapAddress = "localhost:9098";
        when(kafkaProperties.getSecurity()).thenReturn(new KafkaProperties.Security());
    }

    @Mock
    private KafkaProperties kafkaProperties;

    @InjectMocks
    private KafkaProducerConfig kafkaProducerConfig;

    @Test
    void createTransactionalKafkaTemplate() {
        // given
        String transactionalId = "tx-test";
        JsonSerializer testCustomJsonSerializer = new JsonSerializer();

        // when
        KafkaTemplate<String, Serializable> kafkaTemplate =
            KafkaConfigUtils
                .createTransactionalKafkaTemplate(transactionalId, kafkaBootstrapAddress, kafkaProducerConfig);

        // then
        ProducerFactory producerFactory = kafkaTemplate.getProducerFactory();
        assertNotNull(producerFactory);
        verifyTransactionalConfigurationProperties(producerFactory);
        assertTrue(producerFactory.getTransactionIdPrefix().startsWith(transactionalId));
        assertNotEquals(producerFactory.getTransactionIdPrefix(), transactionalId);
        Serializer<Serializable> serializer = kafkaTemplate.getProducerFactory().getValueSerializerSupplier().get();
        assertNotNull(serializer);
        assertFalse(serializer == testCustomJsonSerializer);

        // when
        kafkaTemplate = KafkaConfigUtils
            .createTransactionalKafkaTemplate(transactionalId, kafkaBootstrapAddress, kafkaProducerConfig,
                testCustomJsonSerializer);

        // then
        assertTrue(kafkaTemplate.getProducerFactory().getValueSerializerSupplier().get() == testCustomJsonSerializer);
    }

    @Test
    void createStaticTransactionalIdKafkaTemplate() {
        // given
        String transactionalId = "tx-test";
        JsonSerializer testCustomJsonSerializer = new JsonSerializer();

        // when
        KafkaTemplate<String, Serializable> kafkaTemplate =
            KafkaConfigUtils
                .createStaticTransactionalIdKafkaTemplate(transactionalId, kafkaBootstrapAddress, kafkaProducerConfig);

        // then
        ProducerFactory producerFactory = kafkaTemplate.getProducerFactory();
        assertNotNull(producerFactory);
        verifyTransactionalConfigurationProperties(producerFactory);

        assertEquals(producerFactory.getTransactionIdPrefix(), transactionalId);

        Serializer<Serializable> serializer = kafkaTemplate.getProducerFactory().getValueSerializerSupplier().get();
        assertNotNull(serializer);

        // when
        kafkaTemplate = KafkaConfigUtils
            .createStaticTransactionalIdKafkaTemplate(transactionalId, kafkaBootstrapAddress, kafkaProducerConfig,
                testCustomJsonSerializer);

        // then
        assertTrue(kafkaTemplate.getProducerFactory().getValueSerializerSupplier().get() == testCustomJsonSerializer);
    }

    @Test
    void createNonTransactionalKafkaTemplate() {
        // given
        JsonSerializer testCustomJsonSerializer = new JsonSerializer();

        // when
        KafkaTemplate<String, Serializable> kafkaTemplate =
            KafkaConfigUtils.createNonTransactionalKafkaTemplate(kafkaBootstrapAddress,
                kafkaProducerConfig);

        // then
        ProducerFactory producerFactory = kafkaTemplate.getProducerFactory();
        assertNotNull(producerFactory);
        verifyNonTransactionalConfigurationProperties(producerFactory);
        assertNull(producerFactory.getTransactionIdPrefix());
        Serializer<Serializable> serializer = kafkaTemplate.getProducerFactory().getValueSerializerSupplier().get();
        assertNotNull(serializer);
        assertFalse(serializer == testCustomJsonSerializer);

        // when
        kafkaTemplate = KafkaConfigUtils
            .createNonTransactionalKafkaTemplate(kafkaBootstrapAddress, kafkaProducerConfig, testCustomJsonSerializer);

        // then
        assertTrue(kafkaTemplate.getProducerFactory().getValueSerializerSupplier().get() == testCustomJsonSerializer);
    }

    private void verifyNonTransactionalConfigurationProperties(ProducerFactory producerFactory) {
        assertEquals(kafkaBootstrapAddress,
            producerFactory.getConfigurationProperties().get(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG));
        assertNotEquals("all", producerFactory.getConfigurationProperties().get(ProducerConfig.ACKS_CONFIG));
        assertNotEquals("true",
            producerFactory.getConfigurationProperties().get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
    }

    private void verifyTransactionalConfigurationProperties(ProducerFactory producerFactory) {
        assertEquals(kafkaBootstrapAddress,
            producerFactory.getConfigurationProperties().get(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("all", producerFactory.getConfigurationProperties().get(ProducerConfig.ACKS_CONFIG));
        assertEquals("true",
            producerFactory.getConfigurationProperties().get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
    }
}
