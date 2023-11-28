package uk.ets.lib.commons.kafkaconfig;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Utility class offering factory methods to create proper {@link DefaultKafkaProducerFactory}  {@link KafkaTemplate}
 * instances for your use case.
 */
public class KafkaConfigUtils {

    private KafkaConfigUtils() {
    }

    /**
     * Creates a transactional {@link KafkaTemplate} instance with a unique transactional id per running application instance.
     * * The returned {@link KafkaTemplate} instance should be used only for the case of a transaction which is not started by a listener container.
     * * See also https://docs.spring.io/spring-kafka/docs/current/reference/html/#transactions
     */
    public static <V extends Serializable> KafkaTemplate<String, V> createTransactionalKafkaTemplate(
        UkEtsKafkaConfigProperties props) {
        return new KafkaTemplate<>(createTransactionalKafkaProducerFactory(props));
    }

    /**
     * Creates a transactional {@link KafkaTemplate} instance with a stable transactional id across the running application instances.
     * The returned instance should be used only for the case of a transaction which is started by a listener container.
     * See also https://docs.spring.io/spring-kafka/docs/current/reference/html/#transactions
     */
    public static <V extends Serializable> KafkaTemplate<String, V> createStaticTransactionalIdKafkaTemplate(
        UkEtsKafkaConfigProperties props) {
        return new KafkaTemplate<>(createStaticTransactionalIdKafkaProducerFactory(props));
    }

    /**
     * Creates a non transactional {@link KafkaTemplate} instance.
     */
    public static <V extends Serializable> KafkaTemplate<String, V> createNonTransactionalKafkaTemplate(
        UkEtsKafkaConfigProperties props) {
        return new KafkaTemplate<>(createNonTransactionalKafkaProducerFactory(props));
    }

    /**
     * Creates a transactional {@link DefaultKafkaProducerFactory} instance with a unique transactional id per running application instance.
     * <p>
     * The returned instance should be used only for the case of a transaction which is not started by a listener container.
     * See also https://docs.spring.io/spring-kafka/docs/current/reference/html/#transactions
     */
    public static <V extends Serializable> DefaultKafkaProducerFactory<String, V> createTransactionalKafkaProducerFactory(
        UkEtsKafkaConfigProperties props) {
        return createProducerFactory(CreateProducerFactoryCommand
            .builder()
            .transactional(true)
            .kafkaBootstrapAddress(props.getKafkaBootstrapAddress())
            .transactionalId(props.getTransactionalId())
            .jsonSerializer(props.getJsonSerializer())
            .kafkaProducerConfig(props.getKafkaProducerConfig())
            .maxAgeInMillis(props.getMaxAgeInMillis())
            .build());
    }

    /**
     * Creates a transactional {@link DefaultKafkaProducerFactory} instance with a stable transactional id across the running application instances.
     * <p>
     * The returned instance should be used only for the case of a transaction which is started by a listener container.
     * See also https://docs.spring.io/spring-kafka/docs/current/reference/html/#transactions
     */
    public static <V extends Serializable> DefaultKafkaProducerFactory<String, V> createStaticTransactionalIdKafkaProducerFactory(
        UkEtsKafkaConfigProperties props) {
        return createProducerFactory(CreateProducerFactoryCommand
            .builder()
            .transactional(true)
            .staticTransactionalId(true)
            .kafkaBootstrapAddress(props.getKafkaBootstrapAddress())
            .transactionalId(props.getTransactionalId())
            .jsonSerializer(props.getJsonSerializer())
            .kafkaProducerConfig(props.getKafkaProducerConfig())
            .maxAgeInMillis(props.getMaxAgeInMillis())
            .build());
    }

    /**
     * Creates a non transactional {@link DefaultKafkaProducerFactory} instance.
     */
    public static <V extends Serializable> DefaultKafkaProducerFactory<String, V> createNonTransactionalKafkaProducerFactory(
        UkEtsKafkaConfigProperties props) {
        return createProducerFactory(CreateProducerFactoryCommand
            .builder()
            .kafkaBootstrapAddress(props.getKafkaBootstrapAddress())
            .jsonSerializer(props.getJsonSerializer())
            .kafkaProducerConfig(props.getKafkaProducerConfig())
            .maxAgeInMillis(props.getMaxAgeInMillis())
            .build());
    }

    private static <V extends Serializable> DefaultKafkaProducerFactory createProducerFactory(
        CreateProducerFactoryCommand useCase) {
        Map<String, Object> configProps = new HashMap<>();
        // check producer config for backwards compatibility
        if (useCase.kafkaProducerConfig != null) {
            configProps = useCase.kafkaProducerConfig.getCommonConfigurationProperties();
        }
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, useCase.kafkaBootstrapAddress);
        if (useCase.transactional) {
            String transactionalId = useCase.staticTransactionalId ? useCase.transactionalId
                : useCase.transactionalId + UUID.randomUUID() + Instant.now().toEpochMilli();
            configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
            configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
            configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        }
        JsonSerializer jsonSerializer = useCase.jsonSerializer != null ? useCase.jsonSerializer : new JsonSerializer();

        DefaultKafkaProducerFactory defaultKafkaProducerFactory = new DefaultKafkaProducerFactory(configProps,
            new StringSerializer(),
            jsonSerializer);
        if (useCase.maxAgeInMillis != null) {
            defaultKafkaProducerFactory.setMaxAge(Duration.ofMillis(useCase.maxAgeInMillis));
        }
        return defaultKafkaProducerFactory;
    }

    @Builder
    private static class CreateProducerFactoryCommand {
        private final boolean staticTransactionalId;
        private final String kafkaBootstrapAddress;
        private final String transactionalId;
        private final boolean transactional;
        private final JsonSerializer jsonSerializer;
        private final KafkaProducerConfig kafkaProducerConfig;
        private final Long maxAgeInMillis;
    }


    /**
     * Creates a transactional {@link KafkaTemplate} instance with a unique transactional id per running application instance.
     * The returned {@link KafkaTemplate} instance should be used only for the case of a transaction which is not started by a listener container.
     * See also https://docs.spring.io/spring-kafka/docs/current/reference/html/#transactions
     *
     * @param transactionalId       The transactional id
     * @param kafkaBootstrapAddress Comma-delimited list of host:port pairs to use for establishing the initial connections to the Kafka cluster.
     * @param <V>                   The template value type. It should be serializable.
     * @param jsonSerializer        Optional parameter
     * @return The {@link KafkaTemplate} instance
     * @deprecated use {@link #createTransactionalKafkaTemplate(UkEtsKafkaConfigProperties)}  }
     */
    @Deprecated(forRemoval = true)
    public static <V extends Serializable> KafkaTemplate<String, V> createTransactionalKafkaTemplate(
        String transactionalId, String kafkaBootstrapAddress, KafkaProducerConfig kafkaProducerConfig,
        JsonSerializer... jsonSerializer) {
        return new KafkaTemplate<>(
            createTransactionalKafkaProducerFactory(transactionalId, kafkaBootstrapAddress, kafkaProducerConfig,
                jsonSerializer));
    }


    /**
     * Creates a transactional {@link KafkaTemplate} instance with a stable transactional id across the running application instances.
     * The returned instance should be used only for the case of a transaction which is started by a listener container.
     * See also https://docs.spring.io/spring-kafka/docs/current/reference/html/#transactions
     *
     * @param transactionalId       The transactional id
     * @param kafkaBootstrapAddress Comma-delimited list of host:port pairs to use for establishing the initial connections to the Kafka cluster.
     * @param <V>                   The template value type. It should be serializable.
     * @param jsonSerializer        Optional parameter
     * @return The {@link KafkaTemplate} instance
     * @deprecated use {@link #createStaticTransactionalIdKafkaTemplate(UkEtsKafkaConfigProperties)}
     */
    @Deprecated(forRemoval = true)

    public static <V extends Serializable> KafkaTemplate<String, V> createStaticTransactionalIdKafkaTemplate(
        String transactionalId, String kafkaBootstrapAddress, KafkaProducerConfig kafkaProducerConfig,
        JsonSerializer... jsonSerializer) {
        return new KafkaTemplate<>(
            createStaticTransactionalIdKafkaProducerFactory(transactionalId, kafkaBootstrapAddress, kafkaProducerConfig,
                jsonSerializer));
    }

    /**
     * Creates a non transactional {@link KafkaTemplate} instance.
     *
     * @param kafkaBootstrapAddress Comma-delimited list of host:port pairs to use for establishing the initial connections to the Kafka cluster.
     * @param <V>                   The template value type. It should be serializable.
     * @param jsonSerializer        Optional parameter
     * @return The {@link KafkaTemplate} instance
     * @deprecated use {@link #createNonTransactionalKafkaTemplate(UkEtsKafkaConfigProperties)}
     */
    @Deprecated(forRemoval = true)
    public static <V extends Serializable> KafkaTemplate<String, V> createNonTransactionalKafkaTemplate(
        String kafkaBootstrapAddress, KafkaProducerConfig kafkaProducerConfig, JsonSerializer... jsonSerializer) {
        return new KafkaTemplate<>(
            createNonTransactionalKafkaProducerFactory(kafkaBootstrapAddress, kafkaProducerConfig, jsonSerializer));
    }

    /**
     * Creates a transactional {@link DefaultKafkaProducerFactory} instance with a unique transactional id per running application instance.
     * <p>
     * The returned instance should be used only for the case of a transaction which is not started by a listener container.
     * See also https://docs.spring.io/spring-kafka/docs/current/reference/html/#transactions
     *
     * @param transactionalId       The transactional id
     * @param kafkaBootstrapAddress Comma-delimited list of host:port pairs to use for establishing the initial connections to the Kafka cluster.
     * @param <V>                   The template value type. It should be serializable.
     * @param jsonSerializer        Optional parameter
     * @return The {@link DefaultKafkaProducerFactory} instance
     * @deprecated use {@link #createTransactionalKafkaProducerFactory(UkEtsKafkaConfigProperties)}
     */
    @Deprecated(forRemoval = true)
    public static <V extends Serializable> DefaultKafkaProducerFactory<String, V> createTransactionalKafkaProducerFactory(
        String transactionalId, String kafkaBootstrapAddress, KafkaProducerConfig kafkaProducerConfig,
        JsonSerializer... jsonSerializer) {
        return createProducerFactory(CreateProducerFactoryCommand
            .builder()
            .transactional(true)
            .kafkaBootstrapAddress(kafkaBootstrapAddress)
            .transactionalId(transactionalId)
            .jsonSerializer(jsonSerializer.length > 0 ? jsonSerializer[0] : null)
            .kafkaProducerConfig(kafkaProducerConfig)
            .build());
    }

    /**
     * Creates a transactional {@link DefaultKafkaProducerFactory} instance with a stable transactional id across the running application instances.
     * <p>
     * The returned instance should be used only for the case of a transaction which is started by a listener container.
     * See also https://docs.spring.io/spring-kafka/docs/current/reference/html/#transactions
     *
     * @param transactionalId       The transactional id
     * @param kafkaBootstrapAddress Comma-delimited list of host:port pairs to use for establishing the initial connections to the Kafka cluster.
     * @param <V>                   The template value type. It should be serializable.
     * @param jsonSerializer        Optional parameter
     * @return The {@link DefaultKafkaProducerFactory} instance
     * @deprecated use {@link #createStaticTransactionalIdKafkaProducerFactory(UkEtsKafkaConfigProperties)}
     */
    @Deprecated(forRemoval = true)
    public static <V extends Serializable> DefaultKafkaProducerFactory<String, V> createStaticTransactionalIdKafkaProducerFactory(
        String transactionalId, String kafkaBootstrapAddress, KafkaProducerConfig kafkaProducerConfig,
        JsonSerializer... jsonSerializer) {
        return createProducerFactory(CreateProducerFactoryCommand
            .builder()
            .transactional(true)
            .staticTransactionalId(true)
            .kafkaBootstrapAddress(kafkaBootstrapAddress)
            .transactionalId(transactionalId)
            .jsonSerializer(jsonSerializer.length > 0 ? jsonSerializer[0] : null)
            .kafkaProducerConfig(kafkaProducerConfig)
            .build());
    }

    /**
     * Creates a non transactional {@link DefaultKafkaProducerFactory} instance.
     *
     * @param kafkaBootstrapAddress Comma-delimited list of host:port pairs to use for establishing the initial connections to the Kafka cluster.
     * @param <V>                   The template value type. It should be serializable.
     * @param jsonSerializer        Optional parameter
     * @return The {@link DefaultKafkaProducerFactory} instance
     * @deprecated use {@link #createNonTransactionalKafkaProducerFactory(UkEtsKafkaConfigProperties)}
     */
    @Deprecated(forRemoval = true)
    public static <V extends Serializable> DefaultKafkaProducerFactory<String, V> createNonTransactionalKafkaProducerFactory(
        String kafkaBootstrapAddress, KafkaProducerConfig kafkaProducerConfig, JsonSerializer... jsonSerializer) {
        return createProducerFactory(CreateProducerFactoryCommand
            .builder()
            .kafkaBootstrapAddress(kafkaBootstrapAddress)
            .jsonSerializer(jsonSerializer.length > 0 ? jsonSerializer[0] : null)
            .kafkaProducerConfig(kafkaProducerConfig)
            .build());
    }
}
