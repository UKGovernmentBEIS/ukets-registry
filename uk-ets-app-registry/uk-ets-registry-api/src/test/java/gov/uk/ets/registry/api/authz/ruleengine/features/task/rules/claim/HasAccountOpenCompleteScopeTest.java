package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HasAccountOpenCompleteScopeTest {

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        securityStore = new BusinessSecurityStore();
    }

    @Test
    void error() {
        ErrorBody errorBody = new HasAccountOpenCompleteScope(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permitWithAllowedScope() {
        // given
        Set<String> scopes = Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName());
        securityStore.setUserScopes(scopes);

        HasAccountOpenCompleteScope rule = new HasAccountOpenCompleteScope(securityStore);

        // when
        BusinessRule.Outcome outcome = rule.permit();

        // then
        assertEquals(BusinessRule.Result.PERMITTED, outcome.getResult());
    }

    @Test
    void permitWithoutAllowedScopes() {
        // given
        Set<String> scopes = Set.of(Scope.SCOPE_TASK_ACCOUNT_OPEN_WRITE.getScopeName(),
            Scope.SCOPE_TASK_ACCOUNT_OPEN_READ.getScopeName());
        securityStore.setUserScopes(scopes);

        HasAccountOpenCompleteScope rule = new HasAccountOpenCompleteScope(securityStore);

        // when
        BusinessRule.Outcome outcome = rule.permit();

        // then
        assertEquals(BusinessRule.Result.FORBIDDEN, outcome.getResult());
    }
}
