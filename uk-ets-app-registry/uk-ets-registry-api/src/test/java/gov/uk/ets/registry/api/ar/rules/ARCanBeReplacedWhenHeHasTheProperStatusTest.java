package gov.uk.ets.registry.api.ar.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.uk.ets.registry.api.ar.rules.ARUpdateBRTestHelper.ExpectedARCommand;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Result;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCanBeReplacedWhenHeHasTheProperStatus;
import gov.uk.ets.registry.api.user.domain.UserStatus;

@ExtendWith(MockitoExtension.class)
class ARCanBeReplacedWhenHeHasTheProperStatusTest {
    @Mock
    private BusinessSecurityStore businessSecurityStore;
    @Mock
    private ARBusinessSecurityStoreSlice slice;

    private ARUpdateBRTestHelper helper = new ARUpdateBRTestHelper();

    @Test
    public void permit() {
        // given
        String urid = "UK123213";
        given(slice.getPredecessorUrid()).willReturn(urid);
        given(slice.getAccountARs()).willReturn(List.of(helper.buildAR(ExpectedARCommand.builder()
            .urid(urid)
            .userStatus(UserStatus.ENROLLED)
            .build())));

        given(businessSecurityStore.getArUpdateStoreSlice()).willReturn(slice);
        ARCanBeReplacedWhenHeHasTheProperStatus rule = new ARCanBeReplacedWhenHeHasTheProperStatus(businessSecurityStore);
        // when
        Outcome outcome = rule.permit();
        // then
        assertEquals(Outcome.PERMITTED_OUTCOME, outcome);

        // given
        given(slice.getAccountARs()).willReturn(List.of(helper.buildAR(ExpectedARCommand.builder()
            .urid(urid)
            .userStatus(UserStatus.REGISTERED)
            .build())));
        // when
        outcome = rule.permit();
        // then
        assertEquals(Result.FORBIDDEN, outcome.getResult());
        
        // given
        given(slice.getAccountARs()).willReturn(List.of(helper.buildAR(ExpectedARCommand.builder()
            .urid(urid)
            .userStatus(UserStatus.VALIDATED)
            .build())));
        // when
        outcome = rule.permit();
        // then
        assertEquals(Outcome.PERMITTED_OUTCOME, outcome);
    }
}