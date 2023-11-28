package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.InstallationOwnership;
import gov.uk.ets.registry.api.account.domain.InstallationOwnershipStatus;
import gov.uk.ets.registry.api.account.messaging.UKTLAccountOpeningAnswer;
import gov.uk.ets.registry.api.account.repository.InstallationOwnershipRepository;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class BalanceTransferService {

    private final TransactionService transactionService;

    private final UnitBlockRepository unitBlockRepository;

    private final InstallationOwnershipRepository installationOwnershipRepository;

    private final AccountService accountService;

    public boolean createBalanceTransferTransaction(UKTLAccountOpeningAnswer accountOpeningAnswer) {

        if (!verifyOldAccount(accountOpeningAnswer)) {
           log.error("Old account {} does not exist or is invalid", accountOpeningAnswer.getOldIdentifier());
           return false;
        }
        Long quantity = unitBlockRepository.calculateAvailableQuantity(accountOpeningAnswer.getOldIdentifier(),
                                                                       UnitType.ALLOWANCE);
        if (quantity == null || quantity <= 0) {
            accountService.handleInstallationTransferActions(accountOpeningAnswer.getOldIdentifier());
            return true;
        }

        TransactionSummary summary = constructTransactionSummary(accountOpeningAnswer.getOldIdentifier(),
                                                                 accountOpeningAnswer.getFullIdentifier(),
                                                                 quantity);
        BusinessCheckResult result = new BusinessCheckResult();
        try {
            result = transactionService.proposeTransaction(summary, true);

        } catch (BusinessCheckException exc) {
            result.setErrors(exc.getErrors());
        }
        log.info("Proposed balance transfer from={}, to={}, quantity={}, success={}, identifier={}, errors={}",
                 accountOpeningAnswer.getOldIdentifier(), accountOpeningAnswer.getFullIdentifier(), quantity,
                 result.success(), result.getTransactionIdentifier(), result.getErrors());
        return true;
    }

    private boolean verifyOldAccount(UKTLAccountOpeningAnswer accountOpeningAnswer) {

        InstallationOwnership newInstallationOwnership =
            getNewInstallationOwnership(accountOpeningAnswer.getIdentifier());
        InstallationOwnership previousInstallationOwnership =
            getOldInstallationOwnership(newInstallationOwnership.getInstallation());

        return Objects.nonNull(previousInstallationOwnership.getAccount()) &&
               Objects.equals(previousInstallationOwnership.getAccount().getIdentifier(),
                              accountOpeningAnswer.getOldIdentifier());
    }

    private InstallationOwnership getOldInstallationOwnership(Installation installation) {

        List<InstallationOwnership> oldOwnership = installationOwnershipRepository
            .findByInstallationAndStatusOrderByOwnershipDateDesc(installation, InstallationOwnershipStatus.INACTIVE);

        return oldOwnership.isEmpty() ? new InstallationOwnership() : oldOwnership.get(0);
    }

    private InstallationOwnership getNewInstallationOwnership(Long accountIdentifier) {

        List<InstallationOwnership> newOwnership = installationOwnershipRepository
            .findByAccountIdentifierAndStatus(accountIdentifier, InstallationOwnershipStatus.ACTIVE);

        return newOwnership.isEmpty() ? new InstallationOwnership() : newOwnership.get(0);
    }

    private TransactionSummary constructTransactionSummary(Long transferringAccountIdentifier,
                                                           String acquiringAccountFullIdentifier,
                                                           Long quantity) {

        TransactionSummary summary = new TransactionSummary();
        summary.setType(TransactionType.BalanceInstallationTransferAllowances);
        summary.setTransferringAccountIdentifier(transferringAccountIdentifier);
        summary.setAcquiringAccountFullIdentifier(acquiringAccountFullIdentifier);

        TransactionBlockSummary block = new TransactionBlockSummary();
        block.setType(UnitType.ALLOWANCE);
        block.setQuantity(String.valueOf(quantity));
        summary.setBlocks(Collections.singletonList(block));

        return summary;
    }
}
