package gov.uk.ets.publication.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
@ConfigurationPropertiesScan
public class UkEtsAppPublicationApiApplication {

    public static void main(final String[] args) {
        SpringApplication.run(UkEtsAppPublicationApiApplication.class);
    }

}
