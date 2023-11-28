package gov.uk.ets.registry.api.transaction.checks;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The service for executing business checks.
 */
@Service
@AllArgsConstructor
public class BusinessCheckExecutionService {

    /**
     * Executes the business checks on the provided context.
     *
     * @param context The business check context.
     * @param checks The business checks to execute.
     * @throws BusinessCheckException in case any business check fails.
     */
    public void executeThrows(BusinessCheckContext context, List<BusinessCheck> checks) {
        executeChecks(context, checks, null, true, true);
    }

    /**
     * Executes the business checks on the provided context.
     *
     * @param context The business check context.
     * @param checks The business checks to execute.
     * @param group The business check group.
     * @throws BusinessCheckException in case any business check fails.
     */
    public void executeThrows(BusinessCheckContext context, List<BusinessCheck> checks, BusinessCheckGroup group) {
        executeChecks(context, checks, group, true, true);
    }

    /**
     * Executes the business checks on the provided context.
     *
     * @param context The business check context.
     * @param checks The business checks to execute.
     * @param group The business check group.
     * @param stopProcessingOnFirstError Whether to stop the execution upon the first failure.
     * @throws BusinessCheckException in case any business check fails.
     */
    public void executeThrows(BusinessCheckContext context, List<BusinessCheck> checks, BusinessCheckGroup group, boolean stopProcessingOnFirstError) {
        executeChecks(context, checks, group, stopProcessingOnFirstError, true);
    }

    /**
     * Executes the business checks on the provided context.
     *
     * @param context The business check context.
     * @param checks The business checks to execute.
     * @param group The business check group.
     * @param stopProcessingOnFirstError Whether to stop the execution upon the first failure.
     */
    private void executeChecks(BusinessCheckContext context, List<BusinessCheck> checks, BusinessCheckGroup group, boolean stopProcessingOnFirstError, boolean throwExceptionOnError) {
        for (BusinessCheck check : checks) {
            boolean valid = true;
            if (stopProcessingOnFirstError) {
                valid = !context.hasError();
            }

            if (valid && check.belongsToGroup(group)) {
                check.execute(context);
            }
        }
        if (throwExceptionOnError && context.hasError()) {
            throw new BusinessCheckException(context.getErrors());
        }
    }

    /**
     * Executes the business checks on the provided context.
     *
     * @param context The business check context.
     * @param checks The business checks to execute.
     */
    public BusinessCheckResult execute(BusinessCheckContext context, List<BusinessCheck> checks) {
        executeChecks(context, checks, null, true, false);
        return new BusinessCheckResult(context.getErrors());
    }

    /**
     * Executes the business checks on the provided context.
     *
     * @param context The business check context.
     * @param checks The business checks to execute.
     */
    public BusinessCheckResult execute(BusinessCheckContext context, List<BusinessCheck> checks, BusinessCheckGroup group) {
        executeChecks(context, checks, group, true, false);
        return new BusinessCheckResult(context.getErrors());
    }

}
