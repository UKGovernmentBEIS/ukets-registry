package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccessTest {

    @Mock
    private User user;

    @Mock
    private List<UserRole> userRoles;

    @Mock
    private Account account;

    @Mock
    private List<AccountAccess> accountAccesses;

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(account);
        securityStore.setUser(user);
        securityStore.setUserRoles(userRoles);
        securityStore.setAccountAccesses(accountAccesses);
    }

    @Test
    void error() {
        ErrorBody errorBody = new ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccess(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @DisplayName("Test rule ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccess")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1} - {2}")
    void permit(UserRole userRole, AccountAccessRight accountAccessRight, BusinessRule.Result result) {
        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        securityStore.setAccount(account);
        securityStore.setUserRoles(Arrays.asList(userRole));
        List<AccountAccess> accountAccesses1 = new ArrayList<>();
        AccountAccess initiateAndApprove = new AccountAccess();
        initiateAndApprove.setState(AccountAccessState.ACTIVE);
        initiateAndApprove.setUser(user);
        initiateAndApprove.setAccount(account);
        initiateAndApprove.setRight(accountAccessRight);
        accountAccesses1.add(initiateAndApprove);
        securityStore.setAccountAccesses(accountAccesses1);
        ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccess rule =
                new ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccess(securityStore);
        assertEquals(result, rule.permit().getResult());
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountAccessRight.INITIATE_AND_APPROVE, BusinessRule.Result.PERMITTED),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountAccessRight.READ_ONLY, BusinessRule.Result.FORBIDDEN),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountAccessRight.ROLE_BASED, BusinessRule.Result.FORBIDDEN),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountAccessRight.APPROVE, BusinessRule.Result.FORBIDDEN),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountAccessRight.ROLE_BASED, BusinessRule.Result.PERMITTED),
                Arguments.of(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR, AccountAccessRight.ROLE_BASED, BusinessRule.Result.PERMITTED)
        );
    }
}