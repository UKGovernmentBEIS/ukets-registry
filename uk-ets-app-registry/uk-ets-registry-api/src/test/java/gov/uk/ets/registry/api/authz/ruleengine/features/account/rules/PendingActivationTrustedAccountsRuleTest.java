package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.AccountSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PendingActivationTrustedAccountsRuleTest {
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
        ErrorBody errorBody = new PendingActivationTrustedAccountsRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        AccountSecurityStoreSlice slice = new AccountSecurityStoreSlice();
        slice.setPendingActivationTrustedAccounts(2L);
        securityStore.setAccountSecurityStoreSlice(slice);
        PendingActivationTrustedAccountsRule rule = new PendingActivationTrustedAccountsRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        slice.setPendingActivationTrustedAccounts(0L);
        securityStore.setAccountSecurityStoreSlice(slice);
        rule = new PendingActivationTrustedAccountsRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}
