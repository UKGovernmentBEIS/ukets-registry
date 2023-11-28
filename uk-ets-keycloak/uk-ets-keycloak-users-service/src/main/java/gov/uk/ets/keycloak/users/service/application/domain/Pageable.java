package gov.uk.ets.keycloak.users.service.application.domain;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Pageable query results
 */
@Getter
@Builder
public class Pageable {
    /**
     * The {@link UserProjection} search results
     */
    protected List<UserProjection> items;
    /**
     * The results count number
     */
    protected long totalResults;

    @Override
    public String toString() {
        return "Pageable{" +
            "items=" + items +
            ", totalResults=" + totalResults +
            '}';
    }
}
