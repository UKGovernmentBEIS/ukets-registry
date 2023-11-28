package gov.uk.ets.registry.api.authz;

import static org.junit.jupiter.api.Assertions.assertTrue;


import gov.uk.ets.registry.api.authz.miners.ApiAuthzStore;
import gov.uk.ets.registry.api.authz.miners.MinedPolicy;
import gov.uk.ets.registry.api.authz.miners.MinedResource;
import gov.uk.ets.registry.api.authz.miners.MinedScope;
import gov.uk.ets.registry.api.authz.miners.PolicyMiner;
import gov.uk.ets.registry.api.authz.miners.ResourceMiner;
import gov.uk.ets.registry.api.authz.miners.ScopeMiner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.Logic;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

public class SecurityPolicyExporterTest {
    ScopeMiner scopeMiner;
    ResourceMiner resourceMiner;
    PolicyMiner policyMiner;
    ResourceServerRepresentation resourceServerRepresentation;

    @BeforeEach
    void setUp() {
        ApiAuthzStore policyStore = new ApiAuthzStore();
        resourceServerRepresentation = new ResourceServerRepresentation();
        resourceServerRepresentation.setScopes(testScopes());
        resourceServerRepresentation.setResources(testResources());
        resourceServerRepresentation.setPolicies(testPolicies());
        scopeMiner = new ScopeMiner(resourceServerRepresentation);
        resourceMiner = new ResourceMiner(resourceServerRepresentation);
        policyMiner = new PolicyMiner(resourceServerRepresentation, policyStore, scopeMiner);
    }
    private List<PolicyRepresentation> testPolicies() {
        List<PolicyRepresentation> policyRepresentations = new ArrayList<>();
        policyRepresentations.add(testPolicy("permission-1", "scope",
            Map.of("scopes", "[\"scope-1\"]",
                "applyPolicies", "[\"policy-1\"]")
        ));
        policyRepresentations.add(testPolicy("permission-2", "resource",
            Map.of("resources", "[\"resource-1\"]",
                "applyPolicies", "[\"policy-2\"]")
        ));
        policyRepresentations.add(testPolicy("policy-1", "aggregate",
            Map.of("applyPolicies", "[\"policy-2\",\"policy-3\"]")
        ));
        policyRepresentations.add(testPolicy("policy-2", "role",
            Map.of( "roles", "[{\"id\":\"role-2\",\"required\":true}]")
        ));
        policyRepresentations.add(testPolicy("policy-3", "role",
            Map.of( "roles", "[{\"id\":\"role-3\",\"required\":true}]")
        ));
        return policyRepresentations;
    }

    private PolicyRepresentation testPolicy(String name, String type, Map<String, String> config) {
        PolicyRepresentation policy = new PolicyRepresentation();
        policy.setName(name);
        policy.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
        policy.setLogic(Logic.POSITIVE);
        policy.setType(type);
        policy.setConfig(config);
        return policy;
    }

    private List<ResourceRepresentation> testResources() {
        List<ResourceRepresentation> resourceRepresentations = new ArrayList<>();
        resourceRepresentations.add(
            testResource("resource-1", "/registry-api/resource1", "scope-1", "scope-2")
        );
        return resourceRepresentations;
    }

    private ResourceRepresentation testResource(String... input) {
        String[] scopeNames = Arrays.copyOfRange(input, 2, input.length);
        return new ResourceRepresentation(
            input[0],
            Stream.of(scopeNames).map(ScopeRepresentation::new).collect(Collectors.toSet()),
            input[1], null
        );
    }

    @Test
    public void testThatScopeMinerWorks() {
        List<MinedScope> scopes = scopeMiner.scopes();
        assertTrue(scopes.stream().anyMatch(s -> s.getName().contains("scope-1")));
    }

    @Test
    public void testThatResourceMinerWorks() {
        List<MinedResource> resources = resourceMiner.resources();
        assertTrue(resources.stream()
            .map(MinedResource::urlOrName)
            .anyMatch(urls -> urls.contains("/registry-api/resource1"))
        );
    }

    @Test
    public void testThatScopedPolicyMinerWorks() {
        List<MinedPolicy> policies = policyMiner.policies();
        assertTrue(policies.stream()
            .map(MinedPolicy::getName)
            .anyMatch(name -> name.equals("policy-1"))
        );
    }

    List<ScopeRepresentation> testScopes() {
        List<ScopeRepresentation> scopes = new ArrayList<>();
        scopes.add(testScope("scope-1"));
        scopes.add(testScope("scope-2"));
        scopes.add(testScope("scope-3"));
        return scopes;
    }

    ScopeRepresentation testScope(String name) {
        return new ScopeRepresentation(name);
    }
}
