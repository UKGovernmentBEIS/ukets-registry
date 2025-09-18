package gov.uk.ets.registry.api.authz.ruleengine;

import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 * Executes a list of business rules by executing the {@link BusinessRule#permit()} of each rule.
 * Rule evaluation semantics are the following:
 * <ul>
 *     <li>Each rule can return PERMITTED, FORBIDDEN, NOT_APPLICABLE.</li>
 *     <li>PERMITTED, NOT_APPLICABLE {@link BusinessRule.Outcome} are ignored.</li>
 *     <li>The first rule with a FORBIDDEN result that is found is returned as the global outcome. </li>
 * </ul>
 */
@Component
public class DontPermitIfOneRuleReturnsForbiddenBusinessRuleRunner implements BusinessRuleRunner {

    /**
     * Executes a list of business rules.
     * @param rules The list of business rules
     * @return The result of the run
     */
    public BusinessRule.Outcome run(@NotNull List<BusinessRule> rules) {
        return rules.stream()
            .map(BusinessRule::permit)
            .filter(BusinessRule.Outcome::isForbidden)
            .findFirst()
            .orElse(BusinessRule.Outcome.PERMITTED_OUTCOME);
    }
}
