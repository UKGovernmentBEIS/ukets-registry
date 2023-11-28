package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.checks.RequiredFieldException;
import gov.uk.ets.registry.api.transaction.domain.RegistryLevel;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.EntitlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for maintaining entitlements.
 */
@Service
public class EntitlementService {

    /**
     * The entitlement repository.
     */
    private final EntitlementRepository entitlementRepository;

    /**
     * The {@link EntitlementService} constructor
     * @param entitlementRepository the entitlementRepository
     */
    public EntitlementService(EntitlementRepository entitlementRepository) {
        this.entitlementRepository = entitlementRepository;
    }

    /**
     *
     * @param transaction
     */
    @Transactional
    public void updateEntitlementsOnFinalisation(Transaction transaction) {

        if (transaction == null) {
            throw new RequiredFieldException("Transaction is required");
        }

        if (TransactionType.TransferToSOPforFirstExtTransferAAU.equals(transaction.getType())) {
            RegistryLevel entitlement = entitlementRepository.findByType(RegistryLevelType.AAU_TRANSFER);
            entitlement.setInitialQuantity(entitlement.getInitialQuantity() + 49 * transaction.getQuantity());
            entitlementRepository.save(entitlement);
        }

    }

}
