package gov.uk.ets.registry.api.common.exception;

import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * A business rule exception.
 */
public class BusinessRuleErrorException extends RuntimeException {

    private final transient ErrorBody errorBody;

    public BusinessRuleErrorException(ErrorBody errorBody) {
        this.errorBody = errorBody;
    }

    public ErrorBody getErrorBody() {
        return errorBody;
    }
}
