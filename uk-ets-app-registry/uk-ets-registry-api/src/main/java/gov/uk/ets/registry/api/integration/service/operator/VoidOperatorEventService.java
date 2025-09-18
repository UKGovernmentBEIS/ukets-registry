package gov.uk.ets.registry.api.integration.service.operator;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("!${kafka.integration.enabled:false} || !${kafka.integration.set.operator.enabled:false}")
public class VoidOperatorEventService implements OperatorEventService {

    @Override
    public void updateOperator(CompliantEntity operator, String accountType) {
        // Do nothing
    }

    @Override
    public void updateOperator(CompliantEntity operator, String accountType, String correlationId) {
        // Do nothing
    }
}
