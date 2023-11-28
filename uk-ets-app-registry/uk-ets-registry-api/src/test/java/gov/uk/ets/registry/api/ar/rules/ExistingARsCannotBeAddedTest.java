package gov.uk.ets.registry.api.ar.rules;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ExistingARsCannotBeAddedRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ExistingARsCannotBeAddedTest {

    private BusinessSecurityStore businessSecurityStore;

    @BeforeEach
    void setUp(){
        businessSecurityStore = new BusinessSecurityStore();
    }

    @Test
    void error(){
        ErrorBody errorBody = new ExistingARsCannotBeAddedRule(businessSecurityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
        assertEquals("The user is already added as an authorised representative on this account",
                errorBody.getErrorDetails().get(0).getMessage());
    }

    @Test
    public void permit() {
        //given
        String urid = "UK123213";
        User user = new User();
        user.setUrid(urid);

        String urid2 = "UK124214";
        User user2 = new User();
        user2.setUrid(urid2);

        String urid3 = "UK123215";
        User user3 = new User();
        user3.setUrid(urid3);

        // Mock POJO of authorized representatives
        List<AccountAccess> accountAccessList = new ArrayList<>();

        AccountAccess firstAr = new AccountAccess();
        firstAr.setUser(user2);
        AccountAccess secondAr = new AccountAccess();
        secondAr.setUser(user3);

        accountAccessList.add(firstAr);
        accountAccessList.add(secondAr);

        ARBusinessSecurityStoreSlice arBusinessSecurityStoreSlice = new ARBusinessSecurityStoreSlice();
        arBusinessSecurityStoreSlice.setAccountARs(accountAccessList);
        arBusinessSecurityStoreSlice.setCandidateUrid(urid);
        businessSecurityStore.setArUpdateStoreSlice(arBusinessSecurityStoreSlice);

        ExistingARsCannotBeAddedRule rule = new ExistingARsCannotBeAddedRule(businessSecurityStore);
        //when
        Outcome outcome = rule.permit();
        //then
        assertEquals(BusinessRule.Result.PERMITTED,outcome.getResult());

        //given
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setUser(user);
        accountAccessList.add(accountAccess);
        arBusinessSecurityStoreSlice.setAccountARs(accountAccessList);
        businessSecurityStore.setArUpdateStoreSlice(arBusinessSecurityStoreSlice);
        //when
        outcome = rule.permit();
        //then
        assertEquals(BusinessRule.Result.FORBIDDEN,outcome.getResult());

    }

}