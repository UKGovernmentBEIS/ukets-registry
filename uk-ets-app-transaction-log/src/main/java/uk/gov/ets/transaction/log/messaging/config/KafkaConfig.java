package uk.gov.ets.transaction.log.messaging.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import uk.ets.lib.commons.kafkaconfig.KafkaConsumerConfig;
import uk.ets.lib.commons.kafkaconfig.KafkaProducerConfig;
import uk.gov.ets.transaction.log.messaging.types.AccountNotification;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationSummary;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaConsumerConfig kafkaConsumerConfig(KafkaProperties kafkaProperties) {
        return new KafkaConsumerConfig(kafkaProperties);
    }

    @Bean
    public KafkaProducerConfig kafkaProducerConfig(KafkaProperties kafkaProperties) {
        return new KafkaProducerConfig(kafkaProperties);
    }

    @Bean
    public DefaultKafkaProducerFactory producerFactory() {
        DefaultKafkaProducerFactory<String, Object> defaultKafkaProducerFactory =
            new DefaultKafkaProducerFactory<>(producerProperties(),
            new StringSerializer(),
            new JsonSerializer<>());
        defaultKafkaProducerFactory.setMaxAge(Duration.ofMillis(maxAgeInMillis));
        return defaultKafkaProducerFactory;
    }

    @Bean
    public KafkaTransactionManager kafkaTransactionManager() {
        KafkaTransactionManager ktm = new KafkaTransactionManager(producerFactory());
        ktm.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        return ktm;
    }


    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory em) {
        return new JpaTransactionManager(em);
    }

    @Bean
    public ChainedKafkaTransactionManager chainedTransactionManager(JpaTransactionManager transactionManager,
                                                                    KafkaTransactionManager kafkaTransactionManager) {
        return new ChainedKafkaTransactionManager(kafkaTransactionManager, transactionManager);
    }

    @Bean
    public ConsumerFactory<String, AccountNotification> accountNotificationConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProperties(), new StringDeserializer(),
            new JsonDeserializer<>(AccountNotification.class, false));
    }

    @Bean
    public ConsumerFactory<String, TransactionNotification> transactionNotificationConsumerFactory() {
        ObjectMapper mapper = JacksonUtils.enhancedObjectMapper();
        mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        return new DefaultKafkaConsumerFactory<>(consumerProperties(), new StringDeserializer(),
            new JsonDeserializer<>(TransactionNotification.class, mapper, false));
    }

    @Bean
    public ConsumerFactory<String, ReconciliationSummary> reconciliationConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProperties(), new StringDeserializer(),
            new JsonDeserializer<>(ReconciliationSummary.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountNotification> accountNotificationKafkaListenerContainerFactory(
        ChainedKafkaTransactionManager<Object, Object> chainedKafkaTransactionManager,
        KafkaTemplate<Object, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, AccountNotification> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(accountNotificationConsumerFactory());
        factory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionNotification> transactionNotificationKafkaListenerContainerFactory(
        ChainedKafkaTransactionManager<Object, Object> chainedKafkaTransactionManager,
        KafkaTemplate<Object, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionNotification> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionNotificationConsumerFactory());
        factory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReconciliationSummary> reconciliationKafkaListenerContainerFactory(
        ChainedKafkaTransactionManager<Object, Object> chainedKafkaTransactionManager,
        KafkaTemplate<Object, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, ReconciliationSummary> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reconciliationConsumerFactory());
        factory.getContainerProperties().setTransactionManager(chainedKafkaTransactionManager);

        return factory;
    }

    private Map<String, Object> consumerProperties() {
        Map<String, Object> props = kafkaConsumerConfig(kafkaProperties).getCommonConfigurationProperties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction_log_messages");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return props;
    }


    private Map<String, Object> producerProperties() {
        Map<String, Object> configProps = kafkaProducerConfig(kafkaProperties).getCommonConfigurationProperties();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "uk-transaction-log");
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");

        return configProps;
    }
}
