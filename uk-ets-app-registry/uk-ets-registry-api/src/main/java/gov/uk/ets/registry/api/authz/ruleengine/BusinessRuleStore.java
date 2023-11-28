package gov.uk.ets.registry.api.authz.ruleengine;


import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BusinessRuleStore {

    Map<String, Class<? extends AbstractBusinessRule>[]> businessRules = new HashMap<>();

    public void put(String url, Class<? extends AbstractBusinessRule>[] rules) {
        businessRules.put(url, rules);
    }

    public Map<String, Class<? extends AbstractBusinessRule>[]> get() {
        return businessRules;
    }
}
