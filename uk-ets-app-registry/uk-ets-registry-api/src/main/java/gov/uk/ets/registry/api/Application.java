/*
 * Copyright (c) 2019.
 *
 * UK Emission Trading Scheme.
 */

package gov.uk.ets.registry.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Used to initialize the Spring Boot application.
 */
@Configuration
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableRetry
public class Application {
    /**
     * Main method to initialize the Spring Boot application.
     *
     * @param args The command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class);
    }
}
