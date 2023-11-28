package gov.uk.ets.registry.api.transaction.checks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to include a business check into a group.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessCheckGrouping {

    /**
     * Returns the groups where the check belongs to.
     * @return some groups
     */
    BusinessCheckGroup[] groups();

}
