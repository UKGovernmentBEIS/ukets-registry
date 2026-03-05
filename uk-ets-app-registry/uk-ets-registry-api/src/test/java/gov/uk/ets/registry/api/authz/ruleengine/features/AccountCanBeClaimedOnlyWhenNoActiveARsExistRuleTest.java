package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.User;

public class AccountCanBeClaimedOnlyWhenNoActiveARsExistRuleTest {

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
        ErrorBody errorBody = new AccountCanBeClaimedOnlyWhenNoActiveARsExistRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }
    
    @ParameterizedTest
    @EnumSource(
      value = AccountAccessState.class,
      names = {"ACTIVE"},
      mode = EnumSource.Mode.EXCLUDE)
    void permit(AccountAccessState accountAccessState) {
    	AccountAccess accountAccess = new AccountAccess();
    	accountAccess.setUser(user);
    	accountAccess.setRight(AccountAccessRight.INITIATE);
    	accountAccess.setState(accountAccessState);
        Account account = new Account();
        account.setAccountAccesses(List.of(accountAccess));
        accountAccess.setAccount(account);
        
        securityStore.setAccount(account);
        AccountCanBeClaimedOnlyWhenNoActiveARsExistRule rule =
                new AccountCanBeClaimedOnlyWhenNoActiveARsExistRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
  
    @ParameterizedTest
    @EnumSource(
      value = AccountAccessState.class,
      names = {"ACTIVE"},
      mode = EnumSource.Mode.INCLUDE)
    void forbit(AccountAccessState accountAccessState) {
    	AccountAccess accountAccess = new AccountAccess();
    	accountAccess.setUser(user);
    	accountAccess.setRight(AccountAccessRight.INITIATE);
    	accountAccess.setState(accountAccessState);
        Account account = new Account();
        account.setAccountAccesses(List.of(accountAccess));
        accountAccess.setAccount(account);
        
        securityStore.setAccount(account);
        AccountCanBeClaimedOnlyWhenNoActiveARsExistRule rule =
                new AccountCanBeClaimedOnlyWhenNoActiveARsExistRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }    
    
}
