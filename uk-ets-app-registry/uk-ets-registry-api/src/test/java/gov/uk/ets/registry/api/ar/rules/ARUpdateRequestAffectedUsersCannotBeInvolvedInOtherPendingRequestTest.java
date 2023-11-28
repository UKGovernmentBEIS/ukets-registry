package gov.uk.ets.registry.api.ar.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Result;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest;
import java.util.List;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequestTest {

    @Mock
    private BusinessSecurityStore businessSecurityStore;

    private String urid = "UK213213213";

    @Test
    void permit() {
        verifyForbidden(getOutcome(ExpectedData.builder()
            .candidateUrid(urid)
            .pendingCandidateUrid(urid)
            .build()));

        verifyForbidden(getOutcome(ExpectedData.builder()
            .candidateUrid(urid)
            .pendingPredecessorUrid(urid)
            .build()));

        verifyForbidden(getOutcome(ExpectedData.builder()
            .candidateUrid(urid + "different id")
            .predecessorUrid(urid)
            .pendingPredecessorUrid(urid)
            .build()));

        verifyForbidden(getOutcome(ExpectedData.builder()
            .candidateUrid(urid + "different id")
            .predecessorUrid(urid)
            .pendingCandidateUrid(urid)
            .build()));

        verifyPermitted(getOutcome(ExpectedData.builder()
            .candidateUrid("A")
            .predecessorUrid("B")
            .pendingPredecessorUrid("C")
            .pendingCandidateUrid("D")
            .build()));

    }

    private Outcome getOutcome(ExpectedData data) {
        ARUpdateAction pendingRequest = new ARUpdateAction();
        pendingRequest.setUrid(data.pendingCandidateUrid);
        pendingRequest.setToBeReplacedUrid(data.pendingPredecessorUrid);
        ARBusinessSecurityStoreSlice slice = new ARBusinessSecurityStoreSlice();
        slice.setPendingARUpdateRequests(List.of(pendingRequest));
        slice.setCandidateUrid(data.candidateUrid);
        slice.setPredecessorUrid(data.predecessorUrid);
        given(businessSecurityStore.getArUpdateStoreSlice()).willReturn(slice);
        ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest rule = new ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest(
            businessSecurityStore);

        return rule.permit();
    }

    private void verifyForbidden(Outcome outcome) {
        assertEquals(Result.FORBIDDEN, outcome.getResult());
    }

    private void verifyPermitted(Outcome outcome) {
        assertEquals(Outcome.PERMITTED_OUTCOME, outcome);
    }

    @Builder
    private static class ExpectedData {

        private String candidateUrid;
        private String predecessorUrid;
        private String pendingCandidateUrid;
        private String pendingPredecessorUrid;
    }
}