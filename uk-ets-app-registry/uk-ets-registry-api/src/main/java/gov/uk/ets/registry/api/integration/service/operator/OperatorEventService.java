package gov.uk.ets.registry.api.integration.service.operator;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import java.util.Optional;

public interface OperatorEventService {
    
    /**
     * Sends an update for the Operator.
     *
     * @param operator The operator
     * @param accountType The account type
     */
    void updateOperator(CompliantEntity operator, String accountType);
    
    /**
     * Sends an update for the Operator.
     *
     * @param operator The operator
     * @param accountType The account type
     * @param parentCorrelationIdOptional The correlation id of the parent event if it exists
     */
    void updateOperator(CompliantEntity operator, String accountType, Optional<String> parentCorrelationIdOptional);
}
