package gov.uk.ets.registry.api.allocation.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Encapsulates all transaction related configuration properties.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "business.property.transaction")
public class AllocationConfigurationProperties {

    /**
     * The current allocation year.
     * If present, it overrides the current system year.
     */
    private Integer allocationYear;

}
