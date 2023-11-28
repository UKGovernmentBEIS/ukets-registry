package gov.uk.ets.registry.api.authz.miners;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
@Log4j2
public class SecurityPolicyMiner {

    @Value("classpath:keycloak/uk-ets-registry-api-authz-config.json")
    private Resource apiAuthzConfig;

    private final ApiAuthzStore policyStore;

    private final PolicyMiner policyMiner;
    private final ScopeMiner scopeMiner;
    private final ResourceMiner resourceMiner;

    /**
     * Used to fill the {@link ApiAuthzStore} with data.
     */
    public void mine() {
        resourceMiner.resources().forEach(r -> policyStore.addResource(r.getName(), r));
        scopeMiner.scopes().forEach(s -> policyStore.addScope(s.getName(), s));
        policyMiner.policies().forEach(s -> policyStore.addPolicy(s.getName(), s));
    }


}
