package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.reconciliation.domain.Reconciliation;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ETS transactions during a pending ETS reconciliation process are not allowed.
 */
@Service("check3016")
@AllArgsConstructor
public class CheckPendingReconciliation extends ParentBusinessCheck {

    /**
     * Repository for reconciliations.
     */
    private ReconciliationRepository reconciliationRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        if (context.getTransactionType().isKyoto()) {
            return;
        }

        Optional<Reconciliation> reconciliation = reconciliationRepository.findFirstByStatusIn(
            ReconciliationStatus.getPendingStatuses());

        if (reconciliation.isPresent()) {
            addError(context, "ETS transactions during a pending ETS reconciliation process are not allowed");
        }
    }

}
