package gov.uk.ets.registry.usernotifications;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this notification to denote that.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EmitsGroupNotifications {

    /**
     * The notification that will be emitted must have a type.
     *
     * @return the array of notification types
     */
    GroupNotificationType[] value();
}
