package gov.uk.ets.registry.api.ar.rules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.ar.rules.ARUpdateBRTestHelper.ExpectedARCommand;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Result;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCandidateShouldBeAccountActiveAR;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCandidateShouldBeAccountSuspendedAR;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ARCandidateShouldBeAccountSuspendedARTest {

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
            .arState(AccountAccessState.SUSPENDED)
            .build())));

        given(businessSecurityStore.getArUpdateStoreSlice()).willReturn(slice);
        ARCandidateShouldBeAccountSuspendedAR rule = new ARCandidateShouldBeAccountSuspendedAR(businessSecurityStore);
        // when
        Outcome outcome = rule.permit();
        // then
        assertEquals(Outcome.PERMITTED_OUTCOME, outcome);

        // given
        given(slice.getAccountARs()).willReturn(List.of(helper.buildAR(ExpectedARCommand.builder()
            .urid(urid)
            .arState(AccountAccessState.ACTIVE)
            .build())));
        // when
        outcome = rule.permit();
        // then
        assertEquals(Result.FORBIDDEN, outcome.getResult());
    }
}