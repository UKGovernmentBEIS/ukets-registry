package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotApplicableToETSGovernmentAccountRuleTest {

    @Mock
    private Account account;

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(account);
    }

    @Test
    void error() {
        ErrorBody errorBody = new NotApplicableToETSGovernmentAccountRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {

        AccountType.getAllRegistryGovernmentTypes().forEach(type -> {
            Account account = new Account();
            account.setRegistryAccountType(type.getRegistryType());
            account.setKyotoAccountType(type.getKyotoType());
            securityStore.setAccount(account);

            NotApplicableToETSGovernmentAccountRule rule = new NotApplicableToETSGovernmentAccountRule(securityStore);
            assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
        });

        AccountType.getAllKyotoGovernmentTypes().forEach(type -> {
            Account account = new Account();
            account.setRegistryAccountType(type.getRegistryType());
            account.setKyotoAccountType(type.getKyotoType());
            securityStore.setAccount(account);

            NotApplicableToETSGovernmentAccountRule rule = new NotApplicableToETSGovernmentAccountRule(securityStore);
            assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
        });
    }
}
