package gov.uk.ets.registry.api.tal.service;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosed;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending;
import org.springframework.stereotype.Component;

/**
 * Utility class to apply the business rules,
 * due to the limitation of self-invocation method calls.
 */
@Component
public class TrustedAccountCandidateRuleApplier {

    @Protected({
        ARsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosed.class,
        RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending.class
    })
    public void applyTalAccountCandidateBusinessRules(
        @RuleInput(RuleInputType.TRUSTED_ACCOUNT_FULL_ID) String trustedAccountCandidateFullIdentifier) {
    }
}
