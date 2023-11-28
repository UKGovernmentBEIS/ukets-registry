package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OnlySeniorAdminCanClaimTaskRuleTest {
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
        ErrorBody errorBody = new OnlySeniorAdminCanClaimTaskRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        securityStore.setUserRoles(Arrays.asList(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        OnlySeniorAdminCanClaimTaskRule rule = new OnlySeniorAdminCanClaimTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));
        rule = new OnlySeniorAdminCanClaimTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.READONLY_ADMINISTRATOR));
        rule = new OnlySeniorAdminCanClaimTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORITY_USER));
        rule = new OnlySeniorAdminCanClaimTaskRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        rule = new OnlySeniorAdminCanClaimTaskRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}
