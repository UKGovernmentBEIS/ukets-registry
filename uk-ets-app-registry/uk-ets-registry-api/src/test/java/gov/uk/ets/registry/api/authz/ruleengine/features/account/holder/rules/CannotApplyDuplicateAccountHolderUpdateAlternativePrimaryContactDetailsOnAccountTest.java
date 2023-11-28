package gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.AccountHolderSecurityStoreSlice;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CannotApplyDuplicateAccountHolderUpdateAlternativePrimaryContactDetailsOnAccountTest {
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
        t.setType(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE);
        given(slice.getTasksByAccountHolder()).willReturn(List.of());

        given(businessSecurityStore.getAccountHolderSecurityStoreSlice()).willReturn(slice);
        CannotApplyDuplicateAccountHolderUpdateAlternativePrimaryContactDetailsOnAccount rule =
                new CannotApplyDuplicateAccountHolderUpdateAlternativePrimaryContactDetailsOnAccount(businessSecurityStore);
        // when
        BusinessRule.Outcome outcome = rule.permit();
        // then
        assertEquals(BusinessRule.Outcome.PERMITTED_OUTCOME, outcome);

        // given
        given(slice.getTasksByAccountHolder()).willReturn(List.of(t));
        // when
        outcome = rule.permit();
        // then
        assertEquals(BusinessRule.Result.FORBIDDEN, outcome.getResult());

        t.setType(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE);
        given(slice.getTasksByAccountHolder()).willReturn(List.of(t));
        // when
        outcome = rule.permit();
        // then
        assertEquals(BusinessRule.Result.FORBIDDEN, outcome.getResult());

        t.setType(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD);
        given(slice.getTasksByAccountHolder()).willReturn(List.of(t));
        // when
        outcome = rule.permit();
        // then
        assertEquals(BusinessRule.Result.FORBIDDEN, outcome.getResult());
    }
}
