package gov.uk.ets.registry.api.authz.ruleengine;

import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to specify that a method is protected with the rules specified in the values().
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Protected {

    /**
     * The rule to use for protection.
     *
     * @return the rules
     */
    Class<? extends AbstractBusinessRule>[] value();
}
