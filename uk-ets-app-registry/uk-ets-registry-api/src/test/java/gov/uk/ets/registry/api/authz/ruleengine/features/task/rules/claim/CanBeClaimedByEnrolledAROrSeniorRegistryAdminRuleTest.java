package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CanBeClaimedByEnrolledAROrSeniorRegistryAdminRuleTest {

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
        ErrorBody errorBody = new CanBeClaimedByEnrolledAROrSeniorRegistryAdminRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        securityStore.setUserRoles(Arrays.asList(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        CanBeClaimedByEnrolledAROrSeniorRegistryAdminRule rule = new CanBeClaimedByEnrolledAROrSeniorRegistryAdminRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        rule = new CanBeClaimedByEnrolledAROrSeniorRegistryAdminRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        User user = new User();
        user.setState(UserStatus.VALIDATED);
        securityStore.setUser(user);
        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));
        rule = new CanBeClaimedByEnrolledAROrSeniorRegistryAdminRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        user = new User();
        user.setState(UserStatus.ENROLLED);
        securityStore.setUser(user);
        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));
        rule = new CanBeClaimedByEnrolledAROrSeniorRegistryAdminRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

    }
}
