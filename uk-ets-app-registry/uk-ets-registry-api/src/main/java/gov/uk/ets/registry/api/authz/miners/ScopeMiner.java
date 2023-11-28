package gov.uk.ets.registry.api.authz.miners;

import static java.util.stream.Collectors.toList;

import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;

@RequiredArgsConstructor
public class ScopeMiner {

    final ResourceServerRepresentation resourceServerRepresentation;

    /**
     * A list of scopes.
     *
     * @return the scopes.
     */
    public List<MinedScope> scopes() {
        return resourceServerRepresentation.getScopes().stream().map(
            scopeRepresentation -> new MinedScope(scopeRepresentation.getName())
        ).collect(toList());
    }

    /**
     * A list of scopes for a specific resource.
     *
     * @param resourceName the resource name
     * @return the resource name
     */
    public List<MinedScope> scopes(String resourceName) {
        return resourceServerRepresentation.getResources()
            .stream()
            .filter(r -> r.getName().equals(resourceName))
            .findFirst()
            .map(ResourceRepresentation::getScopes)
            .orElse(new HashSet<>())
            .stream()
            .map(scopeRepresentation -> new MinedScope(scopeRepresentation.getName()))
            .collect(toList());
    }
}
