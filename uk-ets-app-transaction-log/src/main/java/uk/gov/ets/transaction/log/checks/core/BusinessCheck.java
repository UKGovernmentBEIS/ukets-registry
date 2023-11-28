package uk.gov.ets.transaction.log.checks.core;

/**
 * Represents a business check.
 */
public interface BusinessCheck {

    /**
     * Executes the check on the given context.
     * @param context The check context.
     */
    void execute(BusinessCheckContext context);

}
