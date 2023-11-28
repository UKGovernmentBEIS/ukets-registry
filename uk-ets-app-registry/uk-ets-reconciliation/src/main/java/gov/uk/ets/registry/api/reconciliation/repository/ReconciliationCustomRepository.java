package gov.uk.ets.registry.api.reconciliation.repository;

import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;

/**
 * Fragment interface which extends the {@link ReconciliationRepository} with custom methods.
 */
public interface ReconciliationCustomRepository {

    /**
     * Fetches the last created completed reconciliation.
     * @return
     */
    Reconciliation fetchLastCompletedReconciliation();

    /**
     * Fetches the most recently created reconciliation
     */
    Reconciliation fetchLatestReconciliation();
}
