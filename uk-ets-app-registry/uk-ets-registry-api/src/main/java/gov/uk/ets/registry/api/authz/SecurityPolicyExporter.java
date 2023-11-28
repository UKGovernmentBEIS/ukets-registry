package gov.uk.ets.registry.api.authz;

import gov.uk.ets.registry.api.authz.miners.ApiAuthzStore;
import gov.uk.ets.registry.api.authz.miners.Evaluator;
import gov.uk.ets.registry.api.authz.miners.MinedScope;
import gov.uk.ets.registry.api.authz.miners.SecurityPolicyMiner;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleMiner;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleStore;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Exports the policies into a file.
 */
@RequiredArgsConstructor
@Log4j2
public class SecurityPolicyExporter implements ApplicationListener<ContextRefreshedEvent> {

    public static final String RESOURCES_SCOPES_POLICIES_ROLES_CSV = "resources-scopes-policies-roles.csv";
    private final SecurityPolicyMiner securityPolicyMiner;
    private final ApiAuthzStore apiAuthzStore;
    private final BusinessRuleMiner businessRuleMiner;
    private final BusinessRuleStore businessRuleStore;
    private final Evaluator evaluator;
    @Value("${authz.security.policy.extraction.on.startup.enabled:false}")
    private boolean extractionOnStartupEnabled;

    @Override
    @SneakyThrows
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (extractionOnStartupEnabled) {
            log.info("Start working on exporting file {}.", RESOURCES_SCOPES_POLICIES_ROLES_CSV);
            log.info("Mine business rules from Controllers");
            businessRuleMiner.mine(event);
            log.info("Mine resources, scopes, policies, roles from Keycloak authorization json.");
            securityPolicyMiner.mine();
            List<ResourceRow> ruleRows = calculateBusinessRulesPerUrlPattern();
            List<ResourceRow> policyRows = calculateBusinessRulesPerResourceNameOrUrlPattern();
            List<ResourceRow> rows = new ArrayList<>();
            ruleRows.forEach(ruleRow -> {
                Optional<ResourceRow> found =
                    policyRows.stream().filter(policyRow -> urlsMatch(ruleRow, policyRow)).findFirst();
                found.ifPresent(row -> {
                    rows.add(ruleRow.mergeWith(row));
                    policyRows.remove(row);
                });
                if (found.isEmpty()) {
                    rows.add(ruleRow);
                }

            });
            rows.addAll(policyRows);
            rows.sort(ResourceRow::compareTo);
            log.info("Exporting {}", RESOURCES_SCOPES_POLICIES_ROLES_CSV);
            writeCsv(rows);
        }
    }

    private void writeCsv(List<ResourceRow> rows) {
        try (
            FileWriter fileWriter = new FileWriter(RESOURCES_SCOPES_POLICIES_ROLES_CSV)
        ) {
            fileWriter.write("Resource, Scope, Roles, Rules" + System.lineSeparator());
            for (ResourceRow row : rows) {
                fileWriter.write(String.format("%s,%s,%s,%s%s",
                    row.key, row.scope, row.roles, row.rules, System.lineSeparator()));
            }
        } catch (IOException e) {
            log.warn(
                "An exception was thrown while creating the {} file: {}",
                RESOURCES_SCOPES_POLICIES_ROLES_CSV,
                e.getMessage()
            );
        }
    }

    private List<ResourceRow> calculateBusinessRulesPerResourceNameOrUrlPattern() {
        final List<ResourceRow> rows = new ArrayList<>();
        apiAuthzStore.getResources().values()
            .forEach(
                r -> rows.addAll(evaluator.evaluate(r).stream()
                    .flatMap(resourceScopeRoles ->
                        resourceScopeRoles.getResource().urlOrName().stream().map(urlOrName ->
                            new ResourceRow(
                                urlOrName,
                                resourceScopeRoles.getScope() == MinedScope.NO_SCOPE ? ""
                                    : resourceScopeRoles.getScope().getName(),
                                resourceScopeRoles.getEvaluationRepresentation(),
                                ""
                            )
                        )).collect(Collectors.toList())
                )
            );
        return rows;
    }

    private List<ResourceRow> calculateBusinessRulesPerUrlPattern() {
        return businessRuleStore.get().entrySet().stream()
            .map(ruleEntry ->
                new ResourceRow(
                    ruleEntry.getKey(), "", "", Stream.of(ruleEntry.getValue())
                    .map(Class::getSimpleName)
                    .reduce("", (s1, s2) -> s1.equalsIgnoreCase(s2) ? s1 : s1 + " " + s2))
            ).collect(Collectors.toList());
    }

    boolean urlsMatch(ResourceRow row1, ResourceRow row2) {
        return row1.key.equalsIgnoreCase(row2.key);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class ResourceRow implements Comparable<ResourceRow> {
        private final String key;
        private String scope;
        private String roles;
        private String rules;

        ResourceRow mergeWith(ResourceRow in) {
            return new ResourceRow(key,
                this.scope + in.scope,
                this.roles + in.roles,
                this.rules + this.rules);
        }


        @Override
        public int compareTo(ResourceRow o) {
            return key.compareToIgnoreCase(o.key);
        }
    }
}
