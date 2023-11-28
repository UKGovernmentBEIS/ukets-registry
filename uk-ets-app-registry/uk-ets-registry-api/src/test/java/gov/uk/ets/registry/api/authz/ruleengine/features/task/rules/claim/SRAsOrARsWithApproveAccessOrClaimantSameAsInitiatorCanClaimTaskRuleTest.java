package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule.Outcome;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SRAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRuleTest {

    private final Account account = new Account();

    private BusinessSecurityStore securityStore;

    private RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule cut;

    @BeforeEach
    void setUp() {
        User initiatedBy = new User();
        initiatedBy.setUrid("UK123");
        TaskBusinessRuleInfo taskInfo = new TaskBusinessRuleInfo();
        Task task = new Task();
        task.setInitiatedBy(initiatedBy);
        task.setAccount(account);
        task.setTransactionIdentifiers(List.of());
        taskInfo.setTask(task);

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(List.of(taskInfo));

        securityStore = new BusinessSecurityStore();
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        securityStore.setUserScopes(Set.of());
        securityStore.setUser(initiatedBy);

        AccountAccess access = new AccountAccess();
        access.setRight(AccountAccessRight.APPROVE);
        access.setState(AccountAccessState.ACTIVE);
        access.setAccount(account);
        securityStore.setAccountAccesses(List.of(access));

    }

    @Test
    void shouldForbidWhenNotAdminOrAr() {
        cut = new RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule(securityStore);

        Outcome outcome = cut.permit();

        assertThat(outcome.getResult()).isEqualTo(BusinessRule.Result.FORBIDDEN);
    }

    @Test
    void shouldPermitWhenAdmin() {
        securityStore.setUserScopes(Set.of("urn:uk-ets-registry-api:actionForAnyAdmin"));
        cut = new RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule(securityStore);

        Outcome outcome = cut.permit();

        assertThat(outcome.getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldPermitWhenArHasApprove() {

        User user = new User();
        user.setUrid("UK567");
        securityStore.setUser(user);
        securityStore.setUserScopes(Set.of("urn:uk-ets-registry-api:task:transaction-proposal:generic:ar:complete"));
        cut = new RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule(securityStore);

        Outcome outcome = cut.permit();

        assertThat(outcome.getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldPermitWhenArIsInitiator() {

        securityStore.setUserScopes(Set.of("urn:uk-ets-registry-api:task:transaction-proposal:generic:ar:complete"));
        securityStore.setAccountAccesses(List.of());
        cut = new RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule(securityStore);

        Outcome outcome = cut.permit();

        assertThat(outcome.getResult()).isEqualTo(BusinessRule.Result.PERMITTED);
    }

    @Test
    void shouldForbidWhenArButNoAccessAndNotInitiator() {
        User user = new User();
        user.setUrid("UK567");
        securityStore.setUser(user);
        securityStore.setAccountAccesses(List.of());
        securityStore.setUserScopes(Set.of("urn:uk-ets-registry-api:task:transaction-proposal:generic:ar:complete"));
        cut = new RAsOrARsWithApproveAccessOrClaimantSameAsInitiatorCanClaimTaskRule(securityStore);

        Outcome outcome = cut.permit();

        assertThat(outcome.getResult()).isEqualTo(BusinessRule.Result.FORBIDDEN);
    }
}
