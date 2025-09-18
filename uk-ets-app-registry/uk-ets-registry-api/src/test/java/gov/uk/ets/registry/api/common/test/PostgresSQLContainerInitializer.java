package gov.uk.ets.registry.api.common.test;

import java.util.Map;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Application context initializer for starting the shared postgres test container.
 */
public class PostgresSQLContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String POSTGRES_DOCKER_IMAGE = "postgres:15.2";

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_DOCKER_IMAGE));
    static {
        postgres.setCommand("postgres", "-c", "max_connections=1000");
        postgres.start();
    }

    private static Map<String, String> createConnectionConfiguration() {
        return Map.of(
            "spring.datasource.url", postgres.getJdbcUrl(),
            "spring.datasource.username", postgres.getUsername(),
            "spring.datasource.password", postgres.getPassword()
        );
    }

    /**
     * Initializes the application context with the test container postgresql database connection properties.
     *
     * @param applicationContext The application context
     */
    @Override
    public void initialize(
        ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        MapPropertySource testcontainers = new MapPropertySource(
            "testcontainers",
            (Map) createConnectionConfiguration()
        );
        environment.getPropertySources().addFirst(testcontainers);
    }
}
