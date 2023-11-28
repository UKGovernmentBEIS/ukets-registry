package uk.ets.lib.commons.kafkaconfig;

import java.util.Objects;
import java.util.Properties;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * <p>We want a way to set up Kafka Configuration Properties globally and conditionally.</p>
 * <p>We don't want these properties to be set AT ALL when kafka authentication is disabled,
 * since they are used from Kafka Auto Configuration.</p>
 * <p>Using @ConditionalOnProperty @Value, or @Autowired does not seem to work during the
 * ApplicationEnvironmentPreparedEvent (maybe it is too soon in the lifecycle?).</p>
 * This is why we use the environment.getProperty() method.
 * <p>In any case, if at some point authentication is enabled by default, we can remove this class
 * and add everything in a properties file.</p>
 */
@Log4j2
public class KafkaAuthenticationConfigListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    public static final String KAFKA_CLIENT_USERNAME = "kafka.client.username";
    public static final String KAFKA_CLIENT_PASSWORD = "kafka.client.password";
    public static final String DEFAULT_SECURITY_PROTOCOL = "SASL_PLAINTEXT";
    public static final String DEFAULT_SASL_MECHANISM = "SCRAM-SHA-512";

    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();

        if (Objects.equals(environment.getProperty("kafka.authentication.enabled"), "true")) {

            String kafkaClientUsername = environment.getProperty(KAFKA_CLIENT_USERNAME);
            if (kafkaClientUsername == null) {
                handleMissingEnvVariable(KAFKA_CLIENT_USERNAME);

            }
            String kafkaClientPassword = environment.getProperty(KAFKA_CLIENT_PASSWORD);
            if (kafkaClientPassword == null) {
                handleMissingEnvVariable(KAFKA_CLIENT_PASSWORD);
            }

            Properties props = new Properties();

            props.put("spring.kafka.security.protocol", DEFAULT_SECURITY_PROTOCOL);
            props.put("spring.kafka.properties.sasl.mechanism", DEFAULT_SASL_MECHANISM);
            props.put("spring.kafka.properties.sasl.jaas.config",
                "org.apache.kafka.common.security.scram.ScramLoginModule required " +
                    "username=\"" + kafkaClientUsername + "\" " +
                    "password=\"" + kafkaClientPassword + "\";");

            environment.getPropertySources().addFirst(new PropertiesPropertySource("kafkaAuthConfig", props));
        }

    }

    private void handleMissingEnvVariable(String envVariable) {
        log.warn("Environmental variable '{}' was not found. The application can not proceed without it.",
            envVariable);
        log.warn("Application will shutdown");
        throw new IllegalStateException();
    }
}
