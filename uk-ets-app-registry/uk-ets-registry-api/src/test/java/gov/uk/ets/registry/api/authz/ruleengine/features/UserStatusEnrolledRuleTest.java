package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserStatusEnrolledRuleTest {
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

        ErrorBody errorBody = new UserStatusEnrolledRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        User testUser = new User();
        testUser.setState(UserStatus.ENROLLED);
        securityStore.setUser(testUser);
        UserStatusEnrolledRule rule = new UserStatusEnrolledRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        testUser.setState(UserStatus.REGISTERED);
        securityStore.setUser(testUser);
        rule = new UserStatusEnrolledRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        testUser.setState(UserStatus.VALIDATED);
        securityStore.setUser(testUser);
        rule = new UserStatusEnrolledRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        testUser.setState(UserStatus.SUSPENDED);
        securityStore.setUser(testUser);
        rule = new UserStatusEnrolledRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        testUser.setState(UserStatus.UNENROLLED);
        securityStore.setUser(testUser);
        rule = new UserStatusEnrolledRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }
}
