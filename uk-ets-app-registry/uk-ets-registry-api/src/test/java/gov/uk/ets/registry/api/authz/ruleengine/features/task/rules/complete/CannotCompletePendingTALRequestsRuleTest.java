package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CannotCompletePendingTALRequestsRuleTest {


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
        ErrorBody errorBody = new CannotCompletePendingTALRequestsRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void testDoNotPermitWithExistingPendingAccounts() {
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setLinkedPendingTrustedAccounts(List.of(new TrustedAccount()));
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        CannotCompletePendingTALRequestsRule rule = new CannotCompletePendingTALRequestsRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void testPermitWithNoExistingPendingAccounts() {
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setLinkedPendingTrustedAccounts(List.of());
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        CannotCompletePendingTALRequestsRule rule = new CannotCompletePendingTALRequestsRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }

    @Test
    void testNonApplicableWhenPendingAccountsIsNull() {
        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setLinkedPendingTrustedAccounts(null);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        CannotCompletePendingTALRequestsRule rule = new CannotCompletePendingTALRequestsRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
    }
}
