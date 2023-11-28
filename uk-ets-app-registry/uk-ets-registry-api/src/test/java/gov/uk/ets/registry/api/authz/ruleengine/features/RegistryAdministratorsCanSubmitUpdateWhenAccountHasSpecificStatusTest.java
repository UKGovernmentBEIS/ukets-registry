package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.Account;
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

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatusTest {

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
        ErrorBody errorBody = new RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @DisplayName("Test rule RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1} - {2}")
    void permit(UserRole userRole, AccountStatus accountStatus, BusinessRule.Result result) {
        Account account = new Account();
        account.setAccountStatus(accountStatus);
        securityStore.setAccount(account);
        securityStore.setUserRoles(Arrays.asList(userRole));
        RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus rule =
                new RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus(securityStore);
        assertEquals(result, rule.permit().getResult());
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.OPEN, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.ALL_TRANSACTIONS_RESTRICTED, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.SOME_TRANSACTIONS_RESTRICTED, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.SUSPENDED_PARTIALLY, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.SUSPENDED, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.TRANSFER_PENDING, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.CLOSED, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.PROPOSED, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.AUTHORISED_REPRESENTATIVE, AccountStatus.REJECTED, BusinessRule.Result.NOT_APPLICABLE),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.CLOSED, BusinessRule.Result.FORBIDDEN),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.PROPOSED, BusinessRule.Result.FORBIDDEN),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.OPEN, BusinessRule.Result.PERMITTED),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.ALL_TRANSACTIONS_RESTRICTED, BusinessRule.Result.PERMITTED),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.SOME_TRANSACTIONS_RESTRICTED, BusinessRule.Result.PERMITTED),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.SUSPENDED_PARTIALLY, BusinessRule.Result.PERMITTED),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.SUSPENDED, BusinessRule.Result.PERMITTED),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.TRANSFER_PENDING, BusinessRule.Result.PERMITTED),
                Arguments.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, AccountStatus.REJECTED, BusinessRule.Result.PERMITTED)
        );
    }
}
