package gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Result;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.AccountHolderSecurityStoreSlice;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CannotApplyDuplicateAccountHolderUpdateDetailsOnAccountTest {
    @Mock
    private BusinessSecurityStore businessSecurityStore;
    @Mock
    private AccountHolderSecurityStoreSlice slice;

    @Test
    void permit() {
        // given
        Long accountHolderIdentifier = 123L;
        Task t = new Task();
        t.setRequestId(12345L);
        t.setType(RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS);
        given(slice.getTasksByAccountHolder()).willReturn(List.of());

        given(businessSecurityStore.getAccountHolderSecurityStoreSlice()).willReturn(slice);
        CannotApplyDuplicateAccountHolderUpdateDetailsOnAccount rule = new CannotApplyDuplicateAccountHolderUpdateDetailsOnAccount(businessSecurityStore);
        // when
        Outcome outcome = rule.permit();
        // then
        assertEquals(Outcome.PERMITTED_OUTCOME, outcome);

        // given

        given(slice.getTasksByAccountHolder()).willReturn(List.of(t));
        // when
        outcome = rule.permit();
        // then
        assertEquals(Result.FORBIDDEN, outcome.getResult());
    }
}