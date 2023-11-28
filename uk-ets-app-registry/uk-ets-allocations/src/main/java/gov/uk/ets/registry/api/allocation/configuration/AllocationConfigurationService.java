package gov.uk.ets.registry.api.allocation.configuration;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for transaction configuration.
 */
@Service
@AllArgsConstructor
public class AllocationConfigurationService {

    /**
     * Transaction configuration properties.
     */
    private AllocationConfigurationProperties properties;

    /**
     * Returns the allocation year.
     * @return the allocation year.
     */
    public Integer getAllocationYear() {
        Integer result = LocalDate.now().getYear();
        if (properties.getAllocationYear() != null) {
            result = properties.getAllocationYear();
        }
        return result;
    }

}
