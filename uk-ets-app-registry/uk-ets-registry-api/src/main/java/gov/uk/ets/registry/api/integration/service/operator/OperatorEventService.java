package gov.uk.ets.registry.api.integration.service.operator;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;

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
     * @param correlationId The correlation id
     */
    void updateOperator(CompliantEntity operator, String accountType, String correlationId);
}
