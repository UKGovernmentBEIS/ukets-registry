package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.AccountSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.User;

public class AccountCanBeClaimedOnlyWhenNoPendingAddARTaskExistsRuleTest {

    @Mock
    private User user;

    @Mock
    private Account account;

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(account);
        securityStore.setUser(user);
    }

    @Test
    void error() {
        ErrorBody errorBody = new AccountCanBeClaimedOnlyWhenNoPendingAddARTaskExistsRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }
    
    @Test
    void permit() {
        AccountSecurityStoreSlice slice = new AccountSecurityStoreSlice();
        slice.setPendingAddAuthorizedRepresentativeTasks(1L);
        securityStore.setAccountSecurityStoreSlice(slice);
        AccountCanBeClaimedOnlyWhenNoPendingAddARTaskExistsRule rule = new AccountCanBeClaimedOnlyWhenNoPendingAddARTaskExistsRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        slice.setPendingAddAuthorizedRepresentativeTasks(0L);
        securityStore.setAccountSecurityStoreSlice(slice);
        rule = new AccountCanBeClaimedOnlyWhenNoPendingAddARTaskExistsRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }    	
	
}
