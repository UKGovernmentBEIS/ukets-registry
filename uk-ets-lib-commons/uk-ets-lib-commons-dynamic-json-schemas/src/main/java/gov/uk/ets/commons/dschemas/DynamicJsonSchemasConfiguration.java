package gov.uk.ets.commons.dschemas;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Configuration class.
 */
@Configuration
@ComponentScan
public class DynamicJsonSchemasConfiguration {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Bean
    public JsonValidator jsonValidator() {
        return new JsonValidator();
    }

    @Bean
    public JsonSchemaRegistry jsonSchemaRegistry() {
        return new JsonSchemaRegistry();
    }

    @Bean
    public JsonSchemaGenerator jsonSchemaGenerator() {
        return new JsonSchemaGenerator();
    }

    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public JsonSchemaValidatingArgumentResolver jsonSchemaValidatingArgumentResolver() {
        return new JsonSchemaValidatingArgumentResolver(jsonSchemaRegistry(), jsonValidator(), requestMappingHandlerAdapter);
    }
}
