package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.AccountSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PendingTalRequestRuleTest {


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
        ErrorBody errorBody = new PendingTALRequestsRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void testDoNotPermitWithExistingPendingAccounts() {
        AccountSecurityStoreSlice slice = new AccountSecurityStoreSlice();
        slice.setLinkedPendingTrustedAccounts(List.of(new TrustedAccount()));
        securityStore.setAccountSecurityStoreSlice(slice);
        PendingTALRequestsRule rule = new PendingTALRequestsRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void testPermitWithNoExistingPendingAccounts() {
        AccountSecurityStoreSlice slice = new AccountSecurityStoreSlice();
        slice.setLinkedPendingTrustedAccounts(List.of());
        securityStore.setAccountSecurityStoreSlice(slice);
        PendingTALRequestsRule rule = new PendingTALRequestsRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        //     protected List<TrustedAccount> linkedPendingTrustedAccounts;
    }

    @Test
    void testNonApplicableWhenPendingAccountsIsNull() {
        AccountSecurityStoreSlice slice = new AccountSecurityStoreSlice();
        slice.setLinkedPendingTrustedAccounts(null);
        securityStore.setAccountSecurityStoreSlice(slice);
        PendingTALRequestsRule rule = new PendingTALRequestsRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
    }
}
