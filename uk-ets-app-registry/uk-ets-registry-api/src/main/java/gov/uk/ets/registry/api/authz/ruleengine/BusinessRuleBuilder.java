package gov.uk.ets.registry.api.authz.ruleengine;

import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * Builds one or more business rules.
 */
@Component
public class BusinessRuleBuilder {

    @Resource(name = "requestScopedBusinessSecurityStore")
    private BusinessSecurityStore businessSecurityStore;

    /**
     * Build many business rules.
     *
     * @param ruleClasses an array with the class of each rule.
     * @return a list of created business rules.
     */
    public List<BusinessRule> build(Class<? extends AbstractBusinessRule>[] ruleClasses)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<BusinessRule> list = new ArrayList<>();
        for (Class<? extends AbstractBusinessRule> ruleClass : ruleClasses) {
            BusinessRule build = build(ruleClass);
            list.add(build);
        }
        return list;
    }

    /**
     * Build one business rule.
     *
     * @param businessRuleClass the class of the rule.
     * @return a created business rule.
     */
    public BusinessRule build(Class<? extends AbstractBusinessRule> businessRuleClass)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return businessRuleClass.getDeclaredConstructor(
            BusinessSecurityStore.class
        ).newInstance(businessSecurityStore);
    }
}
