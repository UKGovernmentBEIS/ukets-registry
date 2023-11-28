package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.TransactionBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SeniorAdministratorOrAuthorisedRepresentativeInitiateRuleTest {

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

        ErrorBody errorBody = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        securityStore.setAccount(null);
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setAccount(account);
        securityStore.setUserRoles(Arrays.asList(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.READONLY_ADMINISTRATOR, UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.SENIOR_REGISTRY_ADMINISTRATOR, UserRole.JUNIOR_REGISTRY_ADMINISTRATOR));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));
        securityStore.setAccountAccesses(new ArrayList<>());
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        securityStore.setAccount(null);
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(AccountAccessRight.APPROVE);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(AccountAccessRight.INITIATE);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.SUSPENDED);
        accountAccess.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.REJECTED);
        accountAccess.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());

        accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(AccountAccessRight.READ_ONLY);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void permitSurrenderArWithTransactionTypeSurrenderAllowances() {
        // given
        TransactionBusinessSecurityStoreSlice slice = new TransactionBusinessSecurityStoreSlice();
        slice.setTransactionType(TransactionType.SurrenderAllowances);
        securityStore.setTransactionBusinessSecurityStoreSlice(slice);
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));

        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertEquals(BusinessRule.Result.PERMITTED, result.getResult());
    }

    @Test
    void permitSurrenderArWithoutTransactionType() {
        // given
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));

        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertEquals(BusinessRule.Result.PERMITTED, result.getResult());
    }

    @Test
    void permitSurrenderArWithTransactionTypeTransferAllowances() {
        // given
        TransactionBusinessSecurityStoreSlice slice = new TransactionBusinessSecurityStoreSlice();
        slice.setTransactionType(TransactionType.TransferAllowances);
        securityStore.setTransactionBusinessSecurityStoreSlice(slice);
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setState(AccountAccessState.ACTIVE);
        accountAccess.setRight(AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE);
        accountAccess.setUser(user);
        accountAccess.setAccount(account);
        securityStore.setAccountAccesses(Arrays.asList(accountAccess));
        securityStore.setUserRoles(Arrays.asList(UserRole.AUTHORISED_REPRESENTATIVE));

        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule rule = new SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(securityStore);

        // when
        BusinessRule.Outcome result = rule.permit();

        // then
        assertEquals(BusinessRule.Result.FORBIDDEN, result.getResult());
    }
}