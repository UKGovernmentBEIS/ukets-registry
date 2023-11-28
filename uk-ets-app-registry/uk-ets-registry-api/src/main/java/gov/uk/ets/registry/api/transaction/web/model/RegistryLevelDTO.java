package gov.uk.ets.registry.api.transaction.web.model;

import gov.uk.ets.registry.api.transaction.repository.RegistryLevelRepository;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * A wrapper for the projection result array.
 */
@Getter
@Setter
@Builder
public class RegistryLevelDTO {

    List<RegistryLevelRepository.RegistryLevelProjection> result;
}
