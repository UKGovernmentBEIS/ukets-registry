package gov.uk.ets.registry.api.transaction.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration class, responsible for mapping carry-over date values from the application.properties file.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "business.property.transaction.reverse.surrender")
public class ReversalSurrenderConfiguration {



}
