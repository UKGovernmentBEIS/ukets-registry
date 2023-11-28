package gov.uk.ets.registry.api.user.admin.shared;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The search users paged results data transfer object
 */
@Getter
@Setter
public class KeycloakUserSearchPagedResults implements Serializable {
    /**
     * The {@link KeycloakUserProjection} search results
     */
    private List<KeycloakUserProjection> items;
    /**
     * The results count number
     */
    private long totalResults;
}
