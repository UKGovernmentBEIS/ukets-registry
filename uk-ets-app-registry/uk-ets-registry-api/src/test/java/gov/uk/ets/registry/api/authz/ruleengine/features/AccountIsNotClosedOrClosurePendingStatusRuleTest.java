package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AccountIsNotClosedOrClosurePendingStatusRuleTest {

    @Mock
    private User user;

    @Mock
    private Account account;

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(account);
        securityStore.setUser(user);
    }

    @Test
    void error() {
        ErrorBody errorBody = new AccountIsNotClosedOrClosurePendingStatusRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }
    
    @Test
    void permit() {
        Account account = new Account();
        account.setAccountStatus(AccountStatus.CLOSED);
        securityStore.setAccount(account);
        AccountIsNotClosedOrClosurePendingStatusRule rule =
                new AccountIsNotClosedOrClosurePendingStatusRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
        
        account.setAccountStatus(AccountStatus.CLOSURE_PENDING);
        securityStore.setAccount(account);
        rule = new AccountIsNotClosedOrClosurePendingStatusRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
        
        account.setAccountStatus(AccountStatus.SUSPENDED);
        securityStore.setAccount(account);
        rule = new AccountIsNotClosedOrClosurePendingStatusRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}
