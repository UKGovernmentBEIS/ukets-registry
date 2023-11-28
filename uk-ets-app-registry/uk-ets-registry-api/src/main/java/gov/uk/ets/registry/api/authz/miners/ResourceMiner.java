package gov.uk.ets.registry.api.authz.miners;

import static java.util.stream.Collectors.toList;

import java.util.List;
import lombok.Getter;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;

@Getter
public class ResourceMiner {
    final ResourceServerRepresentation resourceServerRepresentation;
    final ScopeMiner scopeMiner;

    public ResourceMiner(
        ResourceServerRepresentation resourceServerRepresentation) {
        this.resourceServerRepresentation = resourceServerRepresentation;
        this.scopeMiner = new ScopeMiner(resourceServerRepresentation);
    }

    /**
     * Return all resources.
     *
     * @return the resources
     */
    public List<MinedResource> resources() {
        return resourceServerRepresentation
            .getResources()
            .stream()
            .map(resourceRepresentation -> new MinedResource(
                resourceRepresentation.getName(),
                resourceRepresentation.getUris(),
                scopeMiner.scopes(resourceRepresentation.getName()))
            )
            .collect(toList());
    }


}
