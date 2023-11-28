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
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.ARsCanViewOnlyTransactionsOfAccountsWithAccess;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransactionDetailsAccessTest {

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
    @DisplayName("A user can access a transaction as an AR of the acquiring account.")
    void testAcquiringAccountArUserCanAccessTransactionDetails() {

        securityStoreWith(AccountAccessState.REMOVED,
                          AccountAccessState.ACTIVE);
        addRules();
        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertEquals(BusinessRule.Outcome.PERMITTED_OUTCOME, actualOutcome);
    }

    @Test
    @DisplayName("A user can access a transaction as an AR of the transferring account.")
    void testTransferringAccountArUserCanAccessTransactionDetails() {

        securityStoreWith(AccountAccessState.ACTIVE,
                          AccountAccessState.REJECTED);
        addRules();
        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertEquals(BusinessRule.Outcome.PERMITTED_OUTCOME, actualOutcome);
    }

    @Test
    @DisplayName("A user can access a transaction as an AR of both transferring and acquiring accounts.")
    void testTransferringAndAcquiringAccountArUserCanAccessTransactionDetails() {

        securityStoreWith(AccountAccessState.ACTIVE,
                          AccountAccessState.ACTIVE);
        addRules();
        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertEquals(BusinessRule.Outcome.PERMITTED_OUTCOME, actualOutcome);
    }

    @Test
    @DisplayName("A user cannot access a transaction if he is not an AR of the transaction's accounts.")
    void testNonArUserCannotAccessTransactionDetails() {

        securityStoreWith(AccountAccessState.REJECTED,
                          null);
        addRules();
        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertTrue(actualOutcome.isForbidden());
    }

    private void securityStoreWith(AccountAccessState transferringAccountAccessState,
                                   AccountAccessState acquiringAccountAccessState) {

        AccountAccess transferringAccountAccess = new AccountAccess();
        transferringAccountAccess.setAccount(transferringAccount);
        transferringAccountAccess.setUser(user);
        transferringAccountAccess.setRight(AccountAccessRight.READ_ONLY);
        transferringAccountAccess.setState(transferringAccountAccessState);

        AccountAccess acquiringAccountAccess = new AccountAccess();
        acquiringAccountAccess.setAccount(acquiringAccount);
        acquiringAccountAccess.setUser(user);
        acquiringAccountAccess.setRight(AccountAccessRight.READ_ONLY);
        acquiringAccountAccess.setState(acquiringAccountAccessState);

        securityStore.setAccountAccesses(Arrays.asList(transferringAccountAccess,acquiringAccountAccess));
        securityStore.setTransactionBusinessSecurityStoreSlice(slice);
        securityStore.setUser(user);
        securityStore.setUserRoles(Collections.singletonList(UserRole.AUTHORISED_REPRESENTATIVE));
    }

    private void addRules() {
        BusinessRule rule;
        rules = new ArrayList<>();
        rule = new ARsCanViewOnlyTransactionsOfAccountsWithAccess(securityStore);
        rules.add(rule);
    }
}
