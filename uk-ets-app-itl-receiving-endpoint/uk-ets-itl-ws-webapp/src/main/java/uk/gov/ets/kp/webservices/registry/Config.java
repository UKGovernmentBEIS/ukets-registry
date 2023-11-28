package uk.gov.ets.kp.webservices.registry;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableAspectJAutoProxy
@Configuration
public class Config {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapAddress;

    @Value("${spring.kafka.producer.properties.max.request.size}")
    private String maxRequestSize;
    
    @Value("${kafka.authentication.enabled}")
    private boolean authEnabled;

    @Value("${kafka.client.username}")
    private String kafkaClientUsername;

    @Value("${kafka.client.password}")
    private String kafkaClientPassword;

    @Bean
    public KafkaTopics kafkaTopics() {
        return new KafkaTopics();
    }

    @Bean
    public DefaultKafkaProducerFactory producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProperties(),
            new JsonSerializer<>(),
            new JsonSerializer<>());
    }

    @Bean
    public KafkaTemplate kafkaTemplate() {
        KafkaTemplate kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(kafkaTopics().getDefaultTopic());
        return kafkaTemplate;
    }

    @Bean
    public RegistryService registryService() {
        return new RegistryService(kafkaTemplate(), kafkaTopics());
    }

    @Bean
    public RegistryServiceAspect exceptionHandler() {
        return new RegistryServiceAspect();
    }

    private Map<String, Object> producerProperties() {
        Map<String, Object> configProps = new HashMap<>();
        // Wanted to reuse the KafkaAuthenticationConfigListener which is used in the rest of the services,
        // but there was an exception:
        //  No qualifying bean of type 'org.springframework.boot.autoconfigure.kafka.KafkaProperties'
        // It seems that the KafkaAutoConfiguration is not used but not sure why.
        if (authEnabled) {
            configProps.put("security.protocol", "SASL_PLAINTEXT");
            configProps.put("sasl.mechanism", "SCRAM-SHA-512");
            configProps.put("sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                    "username=\"" + kafkaClientUsername + "\" " +
                    "password=\"" + kafkaClientPassword + "\";");
        }
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);
        
        return configProps;
    }
}
