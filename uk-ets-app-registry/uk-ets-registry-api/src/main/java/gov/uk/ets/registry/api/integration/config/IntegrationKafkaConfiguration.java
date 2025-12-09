package gov.uk.ets.registry.api.integration.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEventOutcome;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;

@Configuration
@Log4j2
@ConditionalOnProperty(name = "kafka.integration.enabled", havingValue = "true")
@RequiredArgsConstructor
public class IntegrationKafkaConfiguration {

    @Value("${kafka.integration.bootstrap-servers}")
    private String integrationKafkaBootstrapAddress;
    @Value("${kafka.max.age.millis}")
    private Long maxAgeInMillis;
    @Value("${kafka.consumer.max.attempts:3}")
    private Long maxAttempts;

    @Value("${kafka.integration.authentication.enabled}")
    private boolean integrationKafkaAuthenticationEnabled;
    
    @Value("${kafka.integration.auto.offset.reset}")
    private String autoOffsetReset;
    
    @Value("${kafka.integration.installation.emissions.consumer.group-id}")
    private String installationEmissionsConsumerGroup;

    @Value("${kafka.integration.aviation.emissions.consumer.group-id}")
    private String aviationEmissionsConsumerGroup;

    @Value("${kafka.integration.maritime.emissions.consumer.group-id}")
    private String maritimeEmissionsConsumerGroup;
    
    @Value("${kafka.integration.outcome.consumer.group-id}")
    private String outcomeConsumerGroup;

    @Value("${kafka.integration.account.opening.request.consumer.group-id}")
    private String accountOpeningRequestConsumerGroup;

    @Value("${kafka.integration.account.opening.response.consumer.group-id}")
    private String accountOpeningResponseConsumerGroup;

    @Value("${kafka.integration.set.operator.response.consumer.group-id}")
    private String setOperatorResponseConsumerGroup;

    @Value("${kafka.integration.installation.emissions.response.transactional.id}")
    private String installationEmissionsResponseTransactionalId;

    @Value("${kafka.integration.aviation.emissions.response.transactional.id}")
    private String aviationEmissionsResponseTransactionalId;

    @Value("${kafka.integration.maritime.emissions.response.transactional.id}")
    private String maritimeEmissionsResponseTransactionalId;    
    
    @Value("${kafka.integration.account.opening.response.transactional.id}")
    private String accountOpeningResponseTransactionalId;

    @Value("${kafka.integration.set.operator.request.transactional.id}")
    private String setOperatorIdRequestTransactionalId;

    @Value("${kafka.integration.installation.emissions.response.topic}")
    private String installationEmissionsResponseTopic;

    @Value("${kafka.integration.maritime.emissions.response.topic}")
    private String maritimeEmissionsResponseTopic;
    
    @Value("${kafka.integration.aviation.emissions.response.topic}")
    private String aviationEmissionsResponseTopic;

    @Value("${kafka.integration.security.protocol}")
    private String integrationKafkaSecurityProtocol;

    @Value("${kafka.integration.sasl.mechanism}")
    private String integrationKafkaSaslMechanism;

    @Value("${kafka.integration.sasl.jaas.config}")
    private String integrationKafkaSaslJaasConfig;

    @Value("${kafka.integration.sasl.client.callback.handler.class}")
    private String integrationKafkaSaslClientCallbackHandlerClass;

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.installation.emissions.enabled"}, havingValue = "true")
    @Bean("installationEmissionsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> installationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(AccountEmissionsUpdateEvent.class, installationEmissionsConsumerGroup));
        setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.aviation.emissions.enabled"}, havingValue = "true")
    @Bean("aviationEmissionsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> aviationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(AccountEmissionsUpdateEvent.class, aviationEmissionsConsumerGroup));
        setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }
    
    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.maritime.emissions.enabled"}, havingValue = "true")
    @Bean("maritimeEmissionsConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> maritimeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(AccountEmissionsUpdateEvent.class, maritimeEmissionsConsumerGroup));
        setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }    

    @Bean("outcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEventOutcome> outcomeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdateEventOutcome> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(AccountEmissionsUpdateEventOutcome.class, outcomeConsumerGroup));
        setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("accountOpeningOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEventOutcome> accountOpeningOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEventOutcome> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(AccountOpeningEventOutcome.class, accountOpeningResponseConsumerGroup));
        setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("accountOpeningConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEvent> accountOpeningConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEvent> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(AccountOpeningEvent.class, accountOpeningRequestConsumerGroup));
        setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @Bean("operatorSetIdOutcomeConsumerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OperatorUpdateEventOutcome> operatorSetIdOutcomeConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OperatorUpdateEventOutcome> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(OperatorUpdateEventOutcome.class, setOperatorResponseConsumerGroup));
        setupErrorHandler(factory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.aviation.emissions.enabled"}, havingValue = "true")
    @Bean("aviationEmissionsOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountEmissionsUpdateEventOutcome> aviationEmissionsOutcomeKafkaTemplate() {
        return getKafkaTemplate(aviationEmissionsResponseTransactionalId, aviationEmissionsResponseTopic);
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.maritime.emissions.enabled"}, havingValue = "true")
    @Bean("maritimeEmissionsOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountEmissionsUpdateEventOutcome> maritimeEmissionsOutcomeKafkaTemplate() {
        return getKafkaTemplate(maritimeEmissionsResponseTransactionalId, maritimeEmissionsResponseTopic);
    }

    @ConditionalOnProperty(name = {"kafka.integration.enabled", "kafka.integration.emissions.enabled", "kafka.integration.installation.emissions.enabled"}, havingValue = "true")
    @Bean("installationEmissionsOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountEmissionsUpdateEventOutcome> installationEmissionsOutcomeKafkaTemplate() {
        return getKafkaTemplate(installationEmissionsResponseTransactionalId, installationEmissionsResponseTopic);
    }

    @Bean("accountOpeningOutcomeKafkaTemplate")
    public KafkaTemplate<String, AccountOpeningEventOutcome> accountOpeningOutcomeKafkaTemplate() {
        return getKafkaTemplate(accountOpeningResponseTransactionalId, null);
    }

    @Bean("operatorIdKafkaTemplate")
    public KafkaTemplate<String, OperatorUpdateEvent> operatorIdKafkaTemplate() {
        return getKafkaTemplate(setOperatorIdRequestTransactionalId, null);
    }

    private <V> KafkaTemplate<String, V> getKafkaTemplate(String transactionalId, String topic) {
        KafkaTemplate<String, V> kafkaTemplate =
            new KafkaTemplate<>(createProducerFactory(transactionalId + UUID.randomUUID()));
        kafkaTemplate.setDefaultTopic(topic);
        kafkaTemplate.setProducerListener(new KafkaLoggingProducerListener<>());
        return kafkaTemplate;
    }

    // This method should be moved to commons when migration to shared kafka is done.
    private <T> ConsumerFactory<String, T> consumerFactory(Class<T> clazz, String consumerGroup) {
        Map<String, Object> props = integrationKafkaAuthenticationEnabled ? buildProperties() : new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, integrationKafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        //This is to read messages from partitions that have no committed offset after re-enabling a consumer.
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, clazz);
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, KafkaProperties.IsolationLevel.READ_COMMITTED.toString().toLowerCase(
            Locale.ROOT));
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, KafkaLoggingConsumerInterceptor.class.getName());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    private Map<String, Object> buildProperties() {
        Map<String, Object> props = new HashMap<>();

        BiConsumer<String, String> setPropertyIfPresent = (value, key) -> Optional.ofNullable(value)
            .filter(s -> !s.isBlank())
            .ifPresent(s -> props.put(key, s));

        setPropertyIfPresent.accept(integrationKafkaSecurityProtocol, "security.protocol");
        setPropertyIfPresent.accept(integrationKafkaSaslMechanism, "sasl.mechanism");
        setPropertyIfPresent.accept(integrationKafkaSaslJaasConfig, "sasl.jaas.config");
        setPropertyIfPresent.accept(integrationKafkaSaslClientCallbackHandlerClass, "sasl.client.callback.handler.class");
        return props;
    }

    // This method should be moved to commons when migration to shared kafka is done.
    private <T> ProducerFactory<String, T> createProducerFactory(String transactionalId) {
        Map<String, Object> configProps = buildProperties(transactionalId);

        JsonSerializer<T> jsonSerializer = new JsonSerializer<>();
        DefaultKafkaProducerFactory<String, T> defaultKafkaProducerFactory =
            new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), jsonSerializer);
        if (maxAgeInMillis != null) {
            defaultKafkaProducerFactory.setMaxAge(Duration.ofMillis(maxAgeInMillis));
        }

        return defaultKafkaProducerFactory;
    }

    private Map<String, Object> buildProperties(String transactionalId) {
        Map<String, Object> configProps = integrationKafkaAuthenticationEnabled ? buildProperties() : new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, integrationKafkaBootstrapAddress);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        if (transactionalId != null) {
            configProps.put("transactional.id", transactionalId);
            configProps.put("acks", "all");
        }
        return configProps;
    }

    // This method should be moved to commons when migration to shared kafka is done.
    private <T> void setupErrorHandler(ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        KafkaTemplate<Object, Object> template = deadLetterProducerFactory();
        template.setProducerListener(new KafkaLoggingProducerListener<>());
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(template,
            // Remove the following when we move to Spring Boot 3.4 (the default implementation will be the same)
            (cr, e) -> {
                String dltTopic = cr.topic() + "-dlt";
                log.warn("Could not process message, sent to {} topic", dltTopic, e);
                return new TopicPartition(dltTopic, cr.partition());
            });

        FixedBackOff fixedBackOff = new FixedBackOff(100L, maxAttempts);

        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, fixedBackOff);
        defaultErrorHandler.defaultFalse(); //any exception is classified as non retryable

        if (factory.getContainerProperties().getTransactionManager() != null) {
            DefaultAfterRollbackProcessor<String, T> processor =
                new DefaultAfterRollbackProcessor<>(deadLetterPublishingRecoverer, fixedBackOff, template, false);
            factory.setAfterRollbackProcessor(processor);
        } else {
            factory.setCommonErrorHandler(defaultErrorHandler);
        }
    }

    private KafkaTemplate<Object, Object> deadLetterProducerFactory() {
        Map<String, Object> configProps = buildProperties(null);
        DefaultKafkaProducerFactory<Object, Object> defaultKafkaProducerFactory =
            new DefaultKafkaProducerFactory<>(configProps);
        if (maxAgeInMillis != null) {
            defaultKafkaProducerFactory.setMaxAge(Duration.ofMillis(maxAgeInMillis));
        }

        Map<Class<?>, Serializer<?>> valueDelegates = new LinkedHashMap<>(); // retains the order when iterating
        valueDelegates.put(byte[].class, new ByteArraySerializer());
        valueDelegates.put(Object.class, new JsonSerializer<>());
        DelegatingByTypeSerializer valueByTypeSerializer = new DelegatingByTypeSerializer(valueDelegates, true);
        defaultKafkaProducerFactory.setValueSerializer(valueByTypeSerializer);

        Map<Class<?>, Serializer<?>> keyDelegates = new LinkedHashMap<>(); // retains the order when iterating
        keyDelegates.put(byte[].class, new ByteArraySerializer());
        keyDelegates.put(Object.class, new StringSerializer());
        DelegatingByTypeSerializer keyByTypeSerializer = new DelegatingByTypeSerializer(keyDelegates, true);
        defaultKafkaProducerFactory.setKeySerializer(keyByTypeSerializer);

        return new KafkaTemplate<>(defaultKafkaProducerFactory);
    }

}
