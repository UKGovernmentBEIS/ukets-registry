package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NonZeroAccountBalanceRuleTest {
    @Mock
    private Account account;

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(account);
    }

    @Test
    void error() {
        ErrorBody errorBody = new NonZeroAccountBalanceRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        Account account = new Account();
        account.setBalance(null);
        securityStore.setAccount(account);
        NonZeroAccountBalanceRule rule = new NonZeroAccountBalanceRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        account.setBalance(0L);
        securityStore.setAccount(account);
        rule = new NonZeroAccountBalanceRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        account.setBalance(200L);
        securityStore.setAccount(account);
        rule = new NonZeroAccountBalanceRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }
}
