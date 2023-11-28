package gov.uk.ets.registry.api.authz;

import gov.uk.ets.registry.api.authz.miners.ApiAuthzStore;
import gov.uk.ets.registry.api.authz.miners.Evaluator;
import gov.uk.ets.registry.api.authz.miners.PolicyMiner;
import gov.uk.ets.registry.api.authz.miners.ResourceMiner;
import gov.uk.ets.registry.api.authz.miners.ScopeMiner;
import gov.uk.ets.registry.api.authz.miners.SecurityPolicyMiner;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleMiner;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleStore;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "authz.security.policy.extraction.on.startup.enabled", havingValue = "true")
public class SecurityPolicyExporterConfig {

    @Bean
    public Evaluator evaluator(ApiAuthzStore apiAuthzStore) {
        return new Evaluator(apiAuthzStore);
    }

    @Bean
    public ResourceMiner resourceMiner(ResourceServerRepresentation resourceServerRepresentation) {
        return new ResourceMiner(resourceServerRepresentation);
    }

    @Bean
    public ScopeMiner scopeMiner(ResourceServerRepresentation resourceServerRepresentation) {
        return new ScopeMiner(resourceServerRepresentation);
    }

    @Bean
    public PolicyMiner policyMiner(ResourceServerRepresentation resourceServerRepresentation,
                                   ApiAuthzStore apiAuthzStore) {
        return new PolicyMiner(resourceServerRepresentation, apiAuthzStore, scopeMiner(resourceServerRepresentation));
    }

    @Bean
    public SecurityPolicyMiner securityPolicyMiner(ApiAuthzStore apiAuthzStore, PolicyMiner policyMiner,
                                                   ScopeMiner scopeMiner, ResourceMiner resourceMiner) {
        return new SecurityPolicyMiner(apiAuthzStore, policyMiner, scopeMiner, resourceMiner);
    }

    @Bean
    SecurityPolicyExporter securityPolicyExporter(SecurityPolicyMiner securityPolicyMiner,
                                                  BusinessRuleMiner businessRuleMiner,
                                                  BusinessRuleStore businessRuleStore,
                                                  ApiAuthzStore apiAuthzStore,
                                                  Evaluator evaluator) {
        return new SecurityPolicyExporter(securityPolicyMiner, apiAuthzStore, businessRuleMiner, businessRuleStore,
            evaluator);
    }
}
