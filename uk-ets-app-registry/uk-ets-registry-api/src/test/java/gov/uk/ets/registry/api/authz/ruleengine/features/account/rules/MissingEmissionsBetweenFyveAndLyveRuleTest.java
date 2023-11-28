package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.AccountSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.compliance.web.model.VerifiedEmissionsDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class MissingEmissionsBetweenFyveAndLyveRuleTest {

    private BusinessSecurityStore securityStore;
    private List<VerifiedEmissionsDTO> vel;
    private Installation installation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        vel = new ArrayList<>();
        installation = new Installation();
        installation.setEndYear(2021);
        Account account = new Account();
        account.setCompliantEntity(installation);
        securityStore.setAccount(account);
    }

    @Test
    void error() {
        ErrorBody errorBody = new NonZeroAccountBalanceRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void notApplicableOutcome() {
        securityStore.getAccount().setCompliantEntity(null);
        MissingEmissionsBetweenFyveAndLyveRule rule = new MissingEmissionsBetweenFyveAndLyveRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
    }

    @Test
    void permitWithoutExcludedEmissionsOutcome() {

        VerifiedEmissionsDTO dto = new VerifiedEmissionsDTO(installation.getIdentifier(),
                                                            Long.valueOf(installation.getEndYear()),
                                                            "20", LocalDateTime.now());
        vel.add(dto);
        AccountSecurityStoreSlice slice = new AccountSecurityStoreSlice();
        slice.setVerifiedEmissionsList(vel);
        securityStore.setAccountSecurityStoreSlice(slice);

        MissingEmissionsBetweenFyveAndLyveRule rule = new MissingEmissionsBetweenFyveAndLyveRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }

    @Test
    void permitWithExcludedEmissionsOutcome() {

        VerifiedEmissionsDTO dto = new VerifiedEmissionsDTO(installation.getIdentifier(),
                                                            Long.valueOf(installation.getEndYear()),
                                                            "EXCLUDED", LocalDateTime.now());
        vel.add(dto);
        AccountSecurityStoreSlice slice = new AccountSecurityStoreSlice();
        slice.setVerifiedEmissionsList(vel);
        securityStore.setAccountSecurityStoreSlice(slice);

        MissingEmissionsBetweenFyveAndLyveRule rule = new MissingEmissionsBetweenFyveAndLyveRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }

    @Test
    void forbiddenOutcome() {

        VerifiedEmissionsDTO dto = new VerifiedEmissionsDTO(installation.getIdentifier(),
                                                            Long.valueOf(installation.getEndYear()),
                                                            null, null);
        vel.add(dto);
        AccountSecurityStoreSlice slice = new AccountSecurityStoreSlice();
        slice.setVerifiedEmissionsList(vel);
        securityStore.setAccountSecurityStoreSlice(slice);

        MissingEmissionsBetweenFyveAndLyveRule rule = new MissingEmissionsBetweenFyveAndLyveRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }
}
