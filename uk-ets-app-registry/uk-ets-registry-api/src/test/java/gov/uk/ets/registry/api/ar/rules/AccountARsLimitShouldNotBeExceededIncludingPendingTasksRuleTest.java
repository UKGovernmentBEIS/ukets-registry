package gov.uk.ets.registry.api.ar.rules;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.ar.rules.ARUpdateBRTestHelper.ExpectedARCommand;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Result;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 
 * @author P35036
 * @since v3.5.0
 */
@ExtendWith(MockitoExtension.class)
class AccountARsLimitShouldNotBeExceededIncludingPendingTasksRuleTest {

    @Mock
    private BusinessSecurityStore businessSecurityStore;
    @Mock
    private ARBusinessSecurityStoreSlice slice;

    private ARUpdateBRTestHelper helper = new ARUpdateBRTestHelper();
    
    @Test
    void permit() {
        // given
        String urid = "UK123213";
        given(businessSecurityStore.getMaxNumOfARs()).willReturn(2);
        given(slice.getPendingARAddRequests()).willReturn(new ArrayList<>());
		given(slice.getAccountARs()).willReturn(List.of(
				helper.buildAR(ExpectedARCommand.builder().urid(urid).userStatus(UserStatus.ENROLLED).build()),
				helper.buildAR(ExpectedARCommand.builder().urid("UK050566").userStatus(UserStatus.ENROLLED).build())));

        given(businessSecurityStore.getArUpdateStoreSlice()).willReturn(slice);
        AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule rule = new AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule(businessSecurityStore);
        // when
        Outcome outcome = rule.permit();
        // then
        assertEquals(Result.FORBIDDEN, outcome.getResult());

        assertTrue(outcome.getErrorBody().getErrorDetails().stream().noneMatch(error -> error.getMessage().contains("including pending approval tasks")));
        
    }    
    
    @Test
    void permitIncludingPendingApprovalTasks() {
        // given
        String urid = "UK123213";
        given(businessSecurityStore.getMaxNumOfARs()).willReturn(2);
        given(slice.getPendingARAddRequests()).willReturn(new ArrayList<>());
        given(slice.getAccountARs()).willReturn(List.of(helper.buildAR(ExpectedARCommand.builder()
            .urid(urid)
            .userStatus(UserStatus.ENROLLED)
            .build())));

        given(businessSecurityStore.getArUpdateStoreSlice()).willReturn(slice);
        AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule rule = new AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule(businessSecurityStore);
        // when
        Outcome outcome = rule.permit();
        // then
        assertEquals(Result.PERMITTED, outcome.getResult());

        // given
        ARUpdateAction pendingRequest = new ARUpdateAction();
        pendingRequest.setUrid("UK123444");
        pendingRequest.setType(ARUpdateActionType.ADD);
        given(slice.getPendingARAddRequests()).willReturn(List.of(pendingRequest));
        given(slice.getAccountARs()).willReturn(List.of(
            helper.buildAR(ExpectedARCommand.builder()
            .urid(urid)
            .userStatus(UserStatus.REGISTERED)
            .build()),
            helper.buildAR(ExpectedARCommand.builder()
            .urid("UK345556")
            .userStatus(UserStatus.ENROLLED)
            .build())));
        // when
        outcome = rule.permit();
        // then
        assertEquals(Result.FORBIDDEN, outcome.getResult());

        assertTrue(outcome.getErrorBody().getErrorDetails().stream().anyMatch(error -> error.getMessage().contains("including pending approval tasks")));
        
    }    
}
