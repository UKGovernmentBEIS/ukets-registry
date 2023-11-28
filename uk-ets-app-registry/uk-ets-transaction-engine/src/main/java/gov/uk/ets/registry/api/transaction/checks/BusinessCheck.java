package gov.uk.ets.registry.api.transaction.checks;

/**
 * Represents a business check.
 */
public interface BusinessCheck {

    /**
     * Executes the check on the given context.
     * @param context The check context.
     */
    void execute(BusinessCheckContext context);

    /**
     * Whether the check belongs to the provided group.
     * @param businessCheckGroup The business check group.
     * @return false/true
     */
    boolean belongsToGroup(BusinessCheckGroup businessCheckGroup);
}
