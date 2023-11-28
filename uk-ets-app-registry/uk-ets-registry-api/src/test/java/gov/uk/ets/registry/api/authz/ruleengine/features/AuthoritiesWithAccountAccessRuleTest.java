package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthoritiesWithAccountAccessRuleTest {
    private static final String URID = "UK123456";
    private static final String URID_2 = "UK123457";

    private BusinessSecurityStore securityStore;

    @Mock
    private User user;
    @Mock
    private User requestedUser;


    @BeforeEach
    public void setUp() {
        securityStore = new BusinessSecurityStore();
        given(user.getUrid()).willReturn(URID);
        given(requestedUser.getUrid()).willReturn(URID_2);
    }

    @Test
    void shouldPermitAuthorityToAccessAccount() {
        securityStore.setUserRoles(List.of(UserRole.AUTHORITY_USER));
        Account account = new Account();
        securityStore.setAccount(account);
        AccountAccess aa1 = new AccountAccess();
        aa1.setAccount(account);
        aa1.setUser(user);
        aa1.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        securityStore.setAccountAccesses(List.of(aa1));

        AuthoritiesWithAccountAccessRule rule = new AuthoritiesWithAccountAccessRule(securityStore);

        assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldForbidAuthorityToAccessAccount() {
        securityStore.setUserRoles(List.of(UserRole.AUTHORITY_USER));
        Account account = new Account();
        securityStore.setAccount(account);
        AccountAccess aa1 = new AccountAccess();
        aa1.setAccount(account);
        aa1.setUser(user);
        aa1.setRight(AccountAccessRight.ROLE_BASED);
        securityStore.setAccountAccesses(List.of(aa1));

        AuthoritiesWithAccountAccessRule rule = new AuthoritiesWithAccountAccessRule(securityStore);

        assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.FORBIDDEN);
    }
}
