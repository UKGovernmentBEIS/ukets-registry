package gov.uk.ets.registry.api.authz.ruleengine;

import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 * Business rule runner interface. Implement this class to define your own business rule execution/evaluation
 * strategy.
 */
public interface BusinessRuleRunner {

    /**
     * All business rule runners run a collection of business rules.
     *
     * @param rules the rules to execute
     * @return the global result according to the rules implementation.
     */
    BusinessRule.Outcome run(@NotNull List<BusinessRule> rules);
}
