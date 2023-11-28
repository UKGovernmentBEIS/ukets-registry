package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import java.util.List;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserStatus;

/**
 * Business rule that checks if the AR user that is going to be replaced has the proper user status.
 */
public class ARCanBeReplacedWhenHeHasTheProperStatus extends AbstractARBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input.
     */
    public ARCanBeReplacedWhenHeHasTheProperStatus(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("The Authorised Representative that is going to be replaced is not enrolled or validated.");
    }

    /**
     * Checks if the AR that is going to be replaced has the proper user status.
     * {@link Outcome} value.
     *
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        ARBusinessSecurityStoreSlice slice = getSlice();
        boolean predecessorIsEnrolledAR = slice.getAccountARs().stream()
            .map(ar -> ar.getUser())
            .filter(u -> List.of(UserStatus.ENROLLED, UserStatus.VALIDATED).contains(u.getState()))
            .anyMatch(eu -> eu.getUrid().equals(slice.getPredecessorUrid()));
        return predecessorIsEnrolledAR ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
    }
}
