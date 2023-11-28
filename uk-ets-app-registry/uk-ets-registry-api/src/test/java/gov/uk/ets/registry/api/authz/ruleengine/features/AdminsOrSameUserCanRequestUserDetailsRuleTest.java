package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminsOrSameUserCanRequestUserDetailsRuleTest {

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
    void error() {

        ErrorBody errorBody = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void shouldPermitSeniorAdministratorsToAccessOwnUserInfo() {
        securityStore.setUserRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setUser(user);
        securityStore.setRequestedUser(user);
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldPermitSeniorAdministratorsToAccessOtherUserInfo() {
        securityStore.setUserRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setUser(user);
        securityStore.setRequestedUser(requestedUser);
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldPermitJuniorAdministratorsToAccessOtherUserInfo() {
        securityStore.setUserRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setUser(user);
        securityStore.setRequestedUser(requestedUser);
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldPermitJuniorAdministratorsToAccessOwnUserInfo() {
        securityStore.setUserRoles(List.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        securityStore.setUser(user);
        securityStore.setRequestedUser(user);
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldPermitReadOnlyAdministratorsToAccessOwnUserInfo() {
        securityStore.setUser(user);
        securityStore.setRequestedUser(user);
        securityStore.setUserRoles(List.of(UserRole.READONLY_ADMINISTRATOR));
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldPermitReadOnlyAdministratorsToAccessOtherUserInfo() {
        securityStore.setUser(user);
        securityStore.setRequestedUser(requestedUser);
        securityStore.setUserRoles(List.of(UserRole.READONLY_ADMINISTRATOR));
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldPermitAuthorisedRepresentativesToAccessOwnUserInfo() {
        securityStore.setUser(user);
        securityStore.setRequestedUser(user);
        securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldForbidAuthorisedRepresentativesToAccessOtherUserInfo() {
        securityStore.setUser(user);
        securityStore.setRequestedUser(requestedUser);
        securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.FORBIDDEN);
    }

    @Test
    void shouldForbidAuthorityUserToAccessOtherUserInfo() {
        securityStore.setUser(user);
        securityStore.setRequestedUser(requestedUser);
        securityStore.setUserRoles(List.of(UserRole.AUTHORITY_USER));
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.FORBIDDEN);
    }

    @Test
    void shouldPermitAuthorityUserToAccessOwnUserInfo() {
        securityStore.setUser(user);
        securityStore.setRequestedUser(user);
        securityStore.setUserRoles(List.of(UserRole.AUTHORITY_USER));
        AdminsOrSameUserCanRequestUserDetailsRule rule = new AdminsOrSameUserCanRequestUserDetailsRule(securityStore);

        Assertions.assertThat(rule.permit().getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

}
