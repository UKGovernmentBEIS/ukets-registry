package gov.uk.ets.registry.api.ar.rules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Result;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.RegistryAdministratorsCanSubmitRequest;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistryAdministratorsCanSubmitRequestTest {
    @Mock
    private BusinessSecurityStore businessSecurityStore;

    @Test
    void permit() {
        // given
        given(businessSecurityStore.getUserRoles()).willReturn(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        RegistryAdministratorsCanSubmitRequest rule = new RegistryAdministratorsCanSubmitRequest(businessSecurityStore);
        // when
        Outcome outcome = rule.permit();
        // then
        assertEquals(Outcome.PERMITTED_OUTCOME, outcome);

        // given
        given(businessSecurityStore.getUserRoles()).willReturn(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        rule = new RegistryAdministratorsCanSubmitRequest(businessSecurityStore);
        // when
        outcome = rule.permit();
        // then
        assertEquals(Result.FORBIDDEN, outcome.getResult());


    }
}