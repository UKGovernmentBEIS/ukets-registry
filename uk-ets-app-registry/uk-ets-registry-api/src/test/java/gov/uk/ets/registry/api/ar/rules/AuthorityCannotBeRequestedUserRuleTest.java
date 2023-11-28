package gov.uk.ets.registry.api.ar.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Result;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.AuthorityCannotBeRequestedUserRule;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AuthorityCannotBeRequestedUserRuleTest {

    @Mock
    private BusinessSecurityStore businessSecurityStore;
    @Mock
    private ARBusinessSecurityStoreSlice slice;

    @Test
    void permit() {
        // given
        given(slice.getCandidateUserRoles()).willReturn(List.of(UserRole.AUTHORITY_USER));
        given(businessSecurityStore.getArUpdateStoreSlice()).willReturn(slice);
        AuthorityCannotBeRequestedUserRule rule = new AuthorityCannotBeRequestedUserRule(businessSecurityStore);
        // when
        Outcome outcome = rule.permit();
        // then
        assertEquals(Result.FORBIDDEN, outcome.getResult());

        // given
        given(slice.getCandidateUserRoles()).willReturn(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        rule = new AuthorityCannotBeRequestedUserRule(businessSecurityStore);
        // when
        outcome = rule.permit();
        // then
        assertEquals(Outcome.PERMITTED_OUTCOME, outcome);
    }
}
