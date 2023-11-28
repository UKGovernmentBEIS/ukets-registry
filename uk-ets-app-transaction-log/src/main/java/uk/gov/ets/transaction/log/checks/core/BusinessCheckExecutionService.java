package uk.gov.ets.transaction.log.checks.core;

import java.util.Arrays;
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
     */
    private void executeChecks(BusinessCheckContext context, List<BusinessCheck> checks) {
        for (BusinessCheck check : checks) {
            if (!context.hasError()) {
                check.execute(context);
            }
        }
    }

    /**
     * Executes the business checks on the provided context.
     *
     * @param context The business check context.
     * @param checks The business checks to execute.
     */
    public BusinessCheckResult execute(BusinessCheckContext context, BusinessCheck... checks) {
        executeChecks(context, Arrays.asList(checks));
        return new BusinessCheckResult(context.getErrors());
    }

}
