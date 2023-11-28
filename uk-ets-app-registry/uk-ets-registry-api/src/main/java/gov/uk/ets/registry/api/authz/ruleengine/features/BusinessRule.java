package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.common.error.ErrorBody;
import lombok.Builder;
import lombok.Getter;

/**
 * BusinessRule permits or not an action. An implementing class should be always created with the
 * necessary data in its constructor.
 */
public interface BusinessRule {
    /**
     * The identifier of the rule.
     *
     * @return {@link String}
     */
    String key();

    /**
     * The error of the rule.
     *
     * @return {@link ErrorBody}
     */
    ErrorBody error();

    /**
     * The result of the rule. The result of this method should never have external dependencies.
     * I.e. from this code the database or keycloak should never be accessed.
     *
     * @return {@link Outcome}
     */
    Outcome permit();

    /**
     * A forbidden outcome.
     *
     * @return {@link Outcome}
     */
    Outcome forbiddenOutcome();

    /**
     * Rule outcome. This is a Rule result with a message.
     */
    @Getter
    @Builder
    class Outcome {
        public static final Outcome PERMITTED_OUTCOME = builder().result(Result.PERMITTED).build();
        public static final Outcome NOT_APPLICABLE_OUTCOME = builder().result(Result.NOT_APPLICABLE).build();
        Result result;
        String failedOnKey;
        ErrorBody errorBody;

        public boolean isForbidden() {
            return result.equals(Result.FORBIDDEN);
        }
    }

    /**
     * Some rules will yield a PERMITTED result, some others FORBIDDEN and some will be NOT_APPLICABLE.
     */
    enum Result {
        FORBIDDEN, PERMITTED, NOT_APPLICABLE
    }
}
