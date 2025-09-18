package gov.uk.ets.registry.api.notification.userinitiated.web.model;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotificationDTOValidator.class})
@Documented
public @interface ValidNotification {

    String message() default "The Notification is invalid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
