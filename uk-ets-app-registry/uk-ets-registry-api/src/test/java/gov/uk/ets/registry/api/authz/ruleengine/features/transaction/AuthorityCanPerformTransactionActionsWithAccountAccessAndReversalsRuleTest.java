package gov.uk.ets.registry.api.authz.ruleengine.features.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleRunner;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.DontPermitIfOneRuleReturnsForbiddenBusinessRuleRunner;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.AuthorityCanPerformTransactionActionsWithAccountAccessRule;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AuthorityCanPerformTransactionActionsWithAccountAccessAndReversalsRuleTest {

    private User user;
    private Account transferringAccount;
    private Account acquiringAccount;
    private BusinessSecurityStore securityStore;
    private TransactionBusinessSecurityStoreSlice slice;
    private BusinessRuleRunner businessRuleRunner;
    private List<BusinessRule> rules;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        transferringAccount = new Account();
        acquiringAccount = new Account();
        transferringAccount.setIdentifier(100002L);
        acquiringAccount.setIdentifier(100003L);
        slice = new TransactionBusinessSecurityStoreSlice();
        slice.setTransferringAccount(transferringAccount);
        slice.setAcquiringAccount(acquiringAccount);
        businessRuleRunner = new DontPermitIfOneRuleReturnsForbiddenBusinessRuleRunner();
        securityStore = new BusinessSecurityStore();
    }

    @Test
    @DisplayName("An authority user shall perform Issuance transaction with acquiring account access.")
    void testSeniorAdminCanPerformIssuanceTransactionActionsWithAcquiringAccess() {

        securityStore.setUserRoles(Collections.singletonList(UserRole.AUTHORITY_USER));
        slice.setTransactionType(TransactionType.IssueAllowances);
        securityStoreWith(null,
                          AccountAccessRight.INITIATE_AND_APPROVE);
        addRules();
        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertEquals(BusinessRule.Result.PERMITTED, actualOutcome.getResult());
    }

    @Test
    @DisplayName("An authority user shall not perform Issuance transaction with role based acquiring account access.")
    void testSeniorAdminCannotPerformIssuanceTransactionActionsWithRoleBasedAcquiringAccess() {

        securityStore.setUserRoles(Collections.singletonList(UserRole.AUTHORITY_USER));
        slice.setTransactionType(TransactionType.IssueAllowances);
        securityStoreWith(null,
                          AccountAccessRight.ROLE_BASED);
        addRules();
        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertTrue(actualOutcome.isForbidden());
    }

    @Test
    @DisplayName("An authority user shall not perform Central Transfer transaction without transferring account access.")
    void testSeniorAdminCannotPerformSurrenderTransactionActionsWithoutTransferringAccess() {

        securityStore.setUserRoles(Collections.singletonList(UserRole.AUTHORITY_USER));
        slice.setTransactionType(TransactionType.CentralTransferAllowances);
        securityStoreWith(null,
                          AccountAccessRight.ROLE_BASED);
        addRules();
        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertTrue(actualOutcome.isForbidden());
    }

    @Test
    @DisplayName("An authority user shall not perform Issuance transaction without acquiring account access.")
    void testSeniorAdminCannotPerformIssuanceTransactionActionsWithoutAcquiringAccess() {

        securityStore.setUserRoles(Collections.singletonList(UserRole.AUTHORITY_USER));
        slice.setTransactionType(TransactionType.IssueAllowances);
        securityStoreWith(AccountAccessRight.ROLE_BASED,
                          null);
        addRules();
        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertTrue(actualOutcome.isForbidden());
    }

    @Test
    @DisplayName("A senior admin is not applied to this rule.")
    void testARNotApplicableOutcome() {

        securityStore.setUserRoles(Collections.singletonList(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        slice.setTransactionType(TransactionType.InternalTransfer);
        securityStoreWith(null, AccountAccessRight.ROLE_BASED);
        addRules();
        BusinessRule.Outcome actualOutcome = rules.get(0).permit();
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, actualOutcome.getResult());
    }

    private void securityStoreWith(AccountAccessRight transferringAccountAccessRight,
                                   AccountAccessRight acquiringAccountAccessRight) {

        List<AccountAccess> accountAccesses = new ArrayList<>();

        if (transferringAccountAccessRight != null) {
            AccountAccess transferringAccountAccess = new AccountAccess();
            transferringAccountAccess.setAccount(transferringAccount);
            transferringAccountAccess.setUser(user);
            transferringAccountAccess.setRight(transferringAccountAccessRight);
            transferringAccountAccess.setState(AccountAccessState.ACTIVE);
            accountAccesses.add(transferringAccountAccess);
        }

        if (acquiringAccountAccessRight != null) {
            AccountAccess acquiringAccountAccess = new AccountAccess();
            acquiringAccountAccess.setAccount(acquiringAccount);
            acquiringAccountAccess.setUser(user);
            acquiringAccountAccess.setRight(acquiringAccountAccessRight);
            acquiringAccountAccess.setState(AccountAccessState.ACTIVE);
            accountAccesses.add(acquiringAccountAccess);
        }

        securityStore.setAccountAccesses(accountAccesses);
        securityStore.setTransactionBusinessSecurityStoreSlice(slice);
        securityStore.setUser(user);
    }

    private void addRules() {
        BusinessRule rule;
        rules = new ArrayList<>();
        rule = new AuthorityCanPerformTransactionActionsWithAccountAccessRule(securityStore);
        rules.add(rule);
    }
}
