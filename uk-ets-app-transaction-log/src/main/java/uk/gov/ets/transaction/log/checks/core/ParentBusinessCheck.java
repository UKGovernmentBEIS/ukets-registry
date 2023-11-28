package uk.gov.ets.transaction.log.checks.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Service;

/**
 * Parent class for business checks.
 */
@Getter
@NoArgsConstructor
public abstract class ParentBusinessCheck implements BusinessCheck {

    /**
     * Adds an error to the business context.
     * The error code is dynamically retrieved by the qualifier.
     * @param context The business context.
     * @param message The message
     */
    protected void addError(BusinessCheckContext context, String message) {
        Service annotation = this.getClass().getAnnotation(Service.class);
        String value = annotation.value().replace("check", "");
        final Integer errorNumber = Integer.valueOf(value);
        context.addError(errorNumber, message);
        LogManager.getLogger(this.getClass()).info("Business check error {}: {}. {}", errorNumber, message,
            context.getTransaction());
    }

}
