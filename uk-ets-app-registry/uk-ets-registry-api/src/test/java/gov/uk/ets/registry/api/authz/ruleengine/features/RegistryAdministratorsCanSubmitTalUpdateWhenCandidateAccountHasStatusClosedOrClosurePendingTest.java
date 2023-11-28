package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

public class RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePendingTest {

    private final Account trustedAccountCandidate = new Account();
    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
    }

    @Test
    void error() {
        ErrorBody errorBody =
            new RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending(
                securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void forbiddenOutcome1() {
        trustedAccountCandidate.setAccountStatus(AccountStatus.CLOSURE_PENDING);
        securityStore.setTrustedAccountCandidate(trustedAccountCandidate);
        securityStore.setUserRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending rule =
            new RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void forbiddenOutcome2() {
        trustedAccountCandidate.setAccountStatus(AccountStatus.CLOSED);
        securityStore.setTrustedAccountCandidate(trustedAccountCandidate);
        securityStore.setUserRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending rule =
            new RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void permittedOutcome() {
        trustedAccountCandidate.setAccountStatus(AccountStatus.OPEN);
        securityStore.setTrustedAccountCandidate(trustedAccountCandidate);
        securityStore.setUserRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));
        RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending rule =
            new RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
    @Test
    void notApplicableOutcome() {
        trustedAccountCandidate.setAccountStatus(AccountStatus.CLOSURE_PENDING);
        securityStore.setTrustedAccountCandidate(trustedAccountCandidate);
        securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));
        RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending rule =
            new RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());
    }
}
