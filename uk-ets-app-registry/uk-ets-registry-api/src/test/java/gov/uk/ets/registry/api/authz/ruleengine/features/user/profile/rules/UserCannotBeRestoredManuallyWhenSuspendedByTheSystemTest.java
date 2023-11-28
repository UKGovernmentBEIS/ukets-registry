package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.StatusChangeSecurityStoreSlice;

@ExtendWith(MockitoExtension.class)
class UserCannotBeRestoredManuallyWhenSuspendedByTheSystemTest {
    @Mock
    private BusinessSecurityStore businessSecurityStore;
    @Mock
    private StatusChangeSecurityStoreSlice slice;

    @Test
    void permit() {
        // given
        given(slice.isUserSuspendedByTheSystem()).willReturn(false);

        given(businessSecurityStore.getStatusChangeSecurityStoreSlice()).willReturn(slice);
        UserCannotBeRestoredManuallyWhenSuspendedByTheSystem rule =
                new UserCannotBeRestoredManuallyWhenSuspendedByTheSystem(businessSecurityStore);
        // when
        BusinessRule.Outcome outcome = rule.permit();
        // then
        assertEquals(BusinessRule.Outcome.PERMITTED_OUTCOME, outcome);

        // given

        given(slice.isUserSuspendedByTheSystem()).willReturn(true);
        // when
        outcome = rule.permit();
        // then
        assertEquals(BusinessRule.Result.FORBIDDEN, outcome.getResult());
    }
}
