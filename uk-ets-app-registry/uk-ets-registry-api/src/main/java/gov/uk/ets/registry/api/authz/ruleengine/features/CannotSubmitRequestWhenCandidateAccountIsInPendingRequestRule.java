package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import java.util.Set;

public class CannotSubmitRequestWhenCandidateAccountIsInPendingRequestRule extends AbstractBusinessRule {

    private static final Set<TrustedAccountStatus> PENDING_STATUSES = Set.of(
        TrustedAccountStatus.PENDING_ACTIVATION,
        TrustedAccountStatus.PENDING_ADDITION_APPROVAL,
        TrustedAccountStatus.PENDING_REMOVAL_APPROVAL);

    public CannotSubmitRequestWhenCandidateAccountIsInPendingRequestRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("The specified account is part of a pending update request.");
    }

    @Override
    public Outcome permit() {
        if (trustedAccounts == null || trustedAccountCandidate == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (trustedAccounts.stream().anyMatch(acc -> acc.getTrustedAccountFullIdentifier().equals(
                trustedAccountCandidate.getFullIdentifier())
                && PENDING_STATUSES.contains(acc.getStatus()))) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
