package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleRunner;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.DontPermitIfOneRuleReturnsForbiddenBusinessRuleRunner;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ARsAccountAccessTests {

    private User user;
    private Account account;
    private AccountAccess accountAccess;
    private BusinessSecurityStore securityStore;
    private BusinessRuleRunner ruleRunner;
    private List<BusinessRule> rules;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        account = new Account();
        account.setId(1L);
        account.setIdentifier(1L);
        ruleRunner = new DontPermitIfOneRuleReturnsForbiddenBusinessRuleRunner();
        securityStore = new BusinessSecurityStore();
    }

    @Test
    @DisplayName("An ENROLLED user can access an ACTIVE account where he is a ACTIVE authorised representative.")
    void testEnrolledUserCanAccessActiveAccountAsANonSuspendedAr() {
        // given
        securityStoreWith(UserStatus.ENROLLED, AccountStatus.OPEN, AccountAccessState.ACTIVE);
        addRules();
        // when the rules run
        BusinessRule.Outcome actualOutcome = ruleRunner.run(rules);
        // then the result is permitted
        assertEquals(BusinessRule.Outcome.PERMITTED_OUTCOME, actualOutcome);
    }

    @Test
    @DisplayName("An ENROLLED user cannot access an ACTIVE account where he is a SUSPENDED authorised representative.")
    void testEnrolledUserCannotAccessActiveAccountAsASuspendedAr() {
        // given
        securityStoreWith(UserStatus.ENROLLED, AccountStatus.OPEN, AccountAccessState.SUSPENDED);
        addRules();
        // when the rules run
        BusinessRule.Outcome actualOutcome = ruleRunner.run(rules);
        // then the result is permitted
        assertTrue(actualOutcome.isForbidden());
    }

    @Test
    @DisplayName("An SUSPENDED user cannot access an ACTIVE account where he is a ACTIVE authorised representative.")
    void testSuspendedUserCannotAccessActiveAccountAsASuspendedAr() {
        // given
        securityStoreWith(UserStatus.SUSPENDED, AccountStatus.OPEN, AccountAccessState.ACTIVE);
        addRules();
        // when the rules run
        BusinessRule.Outcome actualOutcome = ruleRunner.run(rules);
        // then the result is permitted
        assertTrue(actualOutcome.isForbidden());
    }

    @Test
    @DisplayName("An SUSPENDED user cannot access an ACTIVE account where he is a ACTIVE authorised representative.")
    void testEnrolledUserCannotAccessActiveAccountWhereHeIsNotAr() {
        // given
        securityStoreWith(UserStatus.ENROLLED, AccountStatus.OPEN, AccountAccessState.ACTIVE);
        securityStore.setAccountAccesses(new ArrayList<>()); // remove all access
        addRules();
        // when the rules run
        BusinessRule.Outcome actualOutcome = ruleRunner.run(rules);
        // then the result is permitted
        assertTrue(actualOutcome.isForbidden());
    }

    private void securityStoreWith(UserStatus userStatus,
                                   AccountStatus accountStatus,
                                   AccountAccessState accountAccessState) {
        user.setState(userStatus);
        account.setAccountStatus(accountStatus);
        accountAccess = new AccountAccess();
        accountAccess.setAccount(account);
        accountAccess.setUser(user);
        accountAccess.setRight(AccountAccessRight.READ_ONLY);
        accountAccess.setState(accountAccessState);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        securityStore.setAccount(account);
        securityStore.setUser(user);
        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));

    }

    private void addRules() {
        BusinessRule rule;
        rules = new ArrayList<>();
        rule = new ARsCanViewRequestsOnlyForAccountsWithAccess(securityStore);
        rules.add(rule);
        rule = new ARsCanViewAccountWhenUserStatusIsEnrolled(securityStore);
        rules.add(rule);
        rule = new ARsCanViewAccountWhenAccountHasSpecificStatus(securityStore);
        rules.add(rule);
        rule = new ARsCanViewAccountWhenAccountAccessIsNotSuspended(securityStore);
        rules.add(rule);
    }
}
