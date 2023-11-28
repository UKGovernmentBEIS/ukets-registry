package gov.uk.ets.registry.api.ar.rules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.ar.rules.ARUpdateBRTestHelper.ExpectedARCommand;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Result;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCandidateShouldBeAccountAR;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCandidateShouldBeAccountActiveAR;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ARCandidateShouldBeAccountARTest {
    @Mock
    private BusinessSecurityStore businessSecurityStore;
    @Mock
    private ARBusinessSecurityStoreSlice slice;

    private ARUpdateBRTestHelper helper = new ARUpdateBRTestHelper();

    @Test
    public void permit() {
        // given
        String urid = "UK123213";
        given(slice.getCandidateUrid()).willReturn(urid);
        given(slice.getAccountARs()).willReturn(List.of(helper.buildAR(ExpectedARCommand.builder()
            .urid(urid)
            .arState(AccountAccessState.ACTIVE)
            .build())));

        given(businessSecurityStore.getArUpdateStoreSlice()).willReturn(slice);
        ARCandidateShouldBeAccountAR rule = new ARCandidateShouldBeAccountAR(businessSecurityStore);
        // when
        Outcome outcome = rule.permit();
        // then
        assertEquals(Outcome.PERMITTED_OUTCOME, outcome);

        // given
        given(slice.getAccountARs()).willReturn(List.of(helper.buildAR(ExpectedARCommand.builder()
            .urid(urid + "OTHER")
            .arState(AccountAccessState.REJECTED)
            .build())));
        // when
        outcome = rule.permit();
        // then
        assertEquals(Result.FORBIDDEN, outcome.getResult());
    }
}