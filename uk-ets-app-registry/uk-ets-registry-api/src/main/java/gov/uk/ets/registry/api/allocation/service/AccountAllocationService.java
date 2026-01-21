package gov.uk.ets.registry.api.allocation.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.service.dto.AccountAllocationDTO;
import gov.uk.ets.registry.api.allocation.service.dto.UpdateAllocationCommand;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.allocationtable.services.AllocationTableService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.transaction.domain.BaseTransactionEntity;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for interacting with the account allocation.
 */
@AllArgsConstructor
@Service
public class AccountAllocationService {

    public static final String ALLOCATION_RESTRICTION_LIFTED = "Allocation restriction lifted";
    public static final String ALLOCATION_WITHHELD = "Allocation withheld";
    public static final String FOR_YEAR = " for year ";
    private final AccountRepository accountRepository;
    private final CompliantEntityRepository compliantEntityRepository;
    private final AllocationStatusService allocationStatusService;
    private final AllocationDTOFactory dtoFactory;
    private final EventService eventService;
    private final UserService userService;
    private final AllocationConfigurationService allocationConfigurationService;
    private final TransactionRepository transactionRepository;
    private final AllocationTableService allocationTableService;
    private final RequestAllocationService requestAllocationService;


    /**
     * @param compliantEntityId The compliantEntity id
     * @return  if has allocation or not after a given year
     */
    public boolean hasAllocationsBeforeFYVE(Long compliantEntityId, Integer fyveYear) {
        return allocationStatusService.getAllocationEntries(compliantEntityId)
                .stream()
                .anyMatch(e ->
                        e.getYear() != null &&
                                e.getYear() < fyveYear &&
                                (e.getAllocated() != null && e.getAllocated() > 0
                                        || e.getEntitlement() != null && e.getEntitlement() > 0)
                );
    }

    /**
     * Returns the aggregated information about the account allocation status.
     * @param accountId The account identifier
     * @return The {@link AccountAllocationDTO} account allocation aggregated information.
     */
    public AccountAllocationDTO getAccountAllocation(Long accountId) {
        Optional<Account> optionalAccount = accountRepository.findByIdentifier(accountId);
        Account account = optionalAccount.orElseThrow(() -> new IllegalArgumentException("accountId should not be null"));
        CompliantEntity compliantEntity = account.getCompliantEntity();
        if (compliantEntity == null) {
            return null;
        }
        RegistryAccountType accountType = account.getRegistryAccountType();
        List<AllocationSummary> natOrNavatAllocationSummaries;
        List<AllocationSummary> nerAllocationSummaries = null;
        switch (accountType) {
            case OPERATOR_HOLDING_ACCOUNT: {
                natOrNavatAllocationSummaries = allocationStatusService.getAllocationEntries(compliantEntity.getId(), AllocationType.NAT);
                nerAllocationSummaries = allocationStatusService.getAllocationEntries(compliantEntity.getId(), AllocationType.NER);
                break;
            }
            case AIRCRAFT_OPERATOR_HOLDING_ACCOUNT: {
                natOrNavatAllocationSummaries = allocationStatusService.getAllocationEntries(compliantEntity.getId(), AllocationType.NAVAT);
                break;
            }
            default: {
                return null;
            }
        }
        List<Transaction> pendingReturnExcessTransactions = transactionRepository.findByTransferringAccount_AccountIdentifierAndTypeAndStatusNotIn(
            accountId, TransactionType.ExcessAllocation, TransactionStatus.getFinalStatuses());
        List<String> transactionAttributes = pendingReturnExcessTransactions.stream()
                                                                     .map(BaseTransactionEntity::getAttributes)
                                                                     .collect(Collectors.toList());

        return AccountAllocationDTO.builder()
            .standard(dtoFactory.createAggregatedAllocationDTO(natOrNavatAllocationSummaries, transactionAttributes, false))
            .underNewEntrantsReserve(dtoFactory.createAggregatedAllocationDTO(nerAllocationSummaries, transactionAttributes, true))
            .allocationClassification(compliantEntity.getAllocationClassification())
            .build();
    }

    /**
     * Returns true if there are pending allocation tasks for the account.
     *
     * @param accountId The account identifier.
     * @return boolean result.
     */
    public Boolean getAccountAllocationPendingTaskExists(Long accountId) {
        CompliantEntity compliantEntity = getCompliantEntityByAccountId(accountId);
        if (compliantEntity == null) {
            return Boolean.FALSE;
        }
        return allocationTableService.getEntitiesInPendingAllocationTableUploadTask()
            .contains(compliantEntity.getIdentifier()) ||
            requestAllocationService.getEntitiesInPendingAllocationRequestTaskOrJob()
            .contains(compliantEntity.getIdentifier());
    }
    
    /**
     * Returns the year, status pairs of the account.
     *
     * @param accountId The account identifier.
     * @return The year to status map.
     */
    public Map<Integer, AllocationStatusType> getAccountAllocationStatus(Long accountId) {
        CompliantEntity compliantEntity = getCompliantEntityByAccountId(accountId);
        if (compliantEntity == null) {
            return new HashMap<>();
        }
        return allocationStatusService.getAllocationStatus(compliantEntity.getId()).stream()
            .collect(Collectors.toMap(
                AllocationSummary::getYear, AllocationSummary::getStatus
            ));
    }

    /**
     * Updates the allocation status of the account.
     * @param command The update allocation status command
     */
    @Transactional
    public void updateAllocationStatus(UpdateAllocationCommand command) {
        // Under development
        CompliantEntity compliantEntity = getCompliantEntityByAccountId(command.getAccountId());
        if (compliantEntity == null) {
            throw new IllegalArgumentException("Compliant entity does not exist for account");
        }
        allocationStatusService.updateAllocationStatus(compliantEntity.getId(), command.getChangedStatus());
        if (getAccountAllocationStatus(command.getAccountId()).values()
                                                              .stream()
                                                              .anyMatch(AllocationStatusType.WITHHELD::equals)) {
            compliantEntity.setAllocationWithholdStatus(AllocationStatusType.WITHHELD);
        } else {
            compliantEntity.setAllocationWithholdStatus(AllocationStatusType.ALLOWED);
        }
        compliantEntityRepository.save(compliantEntity);
        generateEvents(command);
    }

    private CompliantEntity getCompliantEntityByAccountId(Long accountId) {
        Account account = accountRepository.findByIdentifier(accountId)
            .orElseThrow(() -> new IllegalArgumentException("no such account"));
        return account.getCompliantEntity();
    }

    private void generateEvents(UpdateAllocationCommand command) {
        String urid = userService.getCurrentUser().getUrid();
        command.getChangedStatus().entrySet().stream().forEach(e -> {
            String actionPrefix = e.getValue()
                .equals(AllocationStatusType.ALLOWED) ?
                ALLOCATION_RESTRICTION_LIFTED : ALLOCATION_WITHHELD;
            eventService.createAndPublishEvent(
                command.getAccountId().toString(),
                urid,
                command.getJustification(),
                EventType.ACCOUNT_ALLOCATION_STATUS_UPDATED,
                actionPrefix + FOR_YEAR + e.getKey()
            );
        });
    }
}
