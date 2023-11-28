package gov.uk.ets.registry.api.authz.ruleengine;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to specify that a parameter will be used to create business rules.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RuleInput {
    /**
     * The input type.
     *
     * @return the type
     */
    RuleInputType value();
}
