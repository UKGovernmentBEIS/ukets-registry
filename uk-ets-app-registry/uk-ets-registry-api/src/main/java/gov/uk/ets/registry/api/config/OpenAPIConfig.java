package gov.uk.ets.registry.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for REST API documentation.
 */
@Configuration
public class OpenAPIConfig {

    /**
     * The build properties.
     */
    @Autowired
    private BuildProperties buildProperties;


    @Bean
    public OpenAPI registryOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("UK ETS Registry API Documentation")
                .description("Backend REST API documentation for the UK ETS Registry application")
                .summary(String.format("%s %s %s", buildProperties.getName(), buildProperties.getVersion(), buildProperties.getTime()))
                .version(buildProperties.getVersion()));
    }
}