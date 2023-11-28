package gov.uk.ets.registry.api.authz.ruleengine.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;


public class CannotSubmitRequestWhenCandidateAccountAlreadyInTalRuleTest {
    private final Account trustedAccountCandidate = new Account();
    private BusinessSecurityStore securityStore;
    private List<TrustedAccount> trustedAccounts = new ArrayList<TrustedAccount>();
    
    private static final String FULL_IDENTIFIER1 = "UK-123-456-789";
    private static final String FULL_IDENTIFIER2 = "UK-987-654-321";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        TrustedAccount ta1 = new TrustedAccount();
        ta1.setTrustedAccountFullIdentifier(FULL_IDENTIFIER1);
        ta1.setStatus(TrustedAccountStatus.ACTIVE);
        trustedAccounts.add(ta1);
        TrustedAccount ta2 = new TrustedAccount();
        ta2.setTrustedAccountFullIdentifier(FULL_IDENTIFIER2);
        trustedAccounts.add(ta2);
    }

    @Test
    void error() {
        ErrorBody errorBody = new CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void forbiddenOutcome() {
        trustedAccountCandidate.setFullIdentifier(FULL_IDENTIFIER1);
        securityStore.setTrustedAccountCandidate(trustedAccountCandidate);
        securityStore.setTrustedAccounts(trustedAccounts);
        CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule rule =
            new CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void permittedOutcome1() {
        trustedAccountCandidate.setFullIdentifier(FULL_IDENTIFIER2);
        securityStore.setTrustedAccountCandidate(trustedAccountCandidate);
        securityStore.setTrustedAccounts(trustedAccounts);
        CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule rule =
            new CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }

    @Test
    void permittedOutcome2() {
        trustedAccountCandidate.setFullIdentifier("random");
        securityStore.setTrustedAccountCandidate(trustedAccountCandidate);
        securityStore.setTrustedAccounts(trustedAccounts);
        CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule rule =
            new CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}
