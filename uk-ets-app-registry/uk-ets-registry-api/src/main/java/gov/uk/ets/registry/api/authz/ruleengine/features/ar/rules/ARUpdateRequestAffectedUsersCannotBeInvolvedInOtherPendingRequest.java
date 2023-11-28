package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.ARBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import java.util.List;
import java.util.function.Predicate;

/**
 * Business Rule that checks if the user that is going to be affected or replaced by other AR is involved in another
 * pending AR update request of this account.
 */
public class ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest extends AbstractARBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("Users involved in another pending AR update request cannot participate in a new request.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        ARBusinessSecurityStoreSlice slice = getSlice();
        List<ARUpdateAction> pendingRequests = slice.getPendingARUpdateRequests();
        String candidateUrid = slice.getCandidateUrid();
        String predecessorUrid = slice.getPredecessorUrid();

        Predicate<ARUpdateAction> predicate = predecessorUrid == null ?
            r -> userParticipatesInARUpdateAction(candidateUrid, r)
            : r -> userParticipatesInARUpdateAction(candidateUrid, r) || userParticipatesInARUpdateAction(
                predecessorUrid, r);

        boolean permitted = pendingRequests.stream().noneMatch(predicate);
        return permitted ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
    }

    private boolean userParticipatesInARUpdateAction(String urid, ARUpdateAction arUpdateAction) {
        return urid.equals(arUpdateAction.getUrid()) || urid.equals(arUpdateAction.getToBeReplacedUrid());
    }
}
