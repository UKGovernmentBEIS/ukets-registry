package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.SeniorAdminCanByAssigneeOfTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.JuniorAdminCannotClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.task.web.model.AllTransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.KpIssuanceTaskDetailsDto;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.RegistryLevelRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class IssueOfAAUsAndRMUsTransactionTypeTaskService
    implements TransactionTypeTaskService<KpIssuanceTaskDetailsDto> {

    private final RegistryLevelRepository registryLevelRepository;

    private final AccountService accountService;

    @Override
    public Set<TransactionType> appliesFor() {
        return Set.of(TransactionType.IssueOfAAUsAndRMUs);
    }


    /**
     * Retrieves the task details for a KP Issuance Request task.
     *
     * @param taskDetailsDTO the generic task details.
     * @param transaction    the transaction details.
     * @return the KP Issuance Request task details.
     */
    @Override
    @Protected(
        {
            SeniorAdminRule.class
        }
    )
    public KpIssuanceTaskDetailsDto getDetails(AllTransactionTaskDetailsDTO taskDetailsDTO, Transaction transaction) {
        KpIssuanceTaskDetailsDto kpIssuanceTaskDetailsDTO = new KpIssuanceTaskDetailsDto(
            taskDetailsDTO, transaction.getReference());
        TransactionBlock transactionBlock = transaction.getBlocks().get(0);
        RegistryLevelRepository.RegistryLevelProjection registryLevelProjection = registryLevelRepository
            .findByTypeAndUnitTypeAndEnvironmentalActivityAndPeriod(
                RegistryLevelType.ISSUANCE_KYOTO_LEVEL,
                transactionBlock.getType(), transactionBlock.getEnvironmentalActivity(),
                transactionBlock.getApplicablePeriod());
        kpIssuanceTaskDetailsDTO
            .setInitialQuantity(registryLevelProjection.getInitialQuantity());
        kpIssuanceTaskDetailsDTO
            .setConsumedQuantity(registryLevelProjection.getConsumedQuantity());
        kpIssuanceTaskDetailsDTO
            .setPendingQuantity(registryLevelProjection.getPendingQuantity());
        kpIssuanceTaskDetailsDTO.setQuantity(transactionBlock.getQuantity());
        kpIssuanceTaskDetailsDTO.setTransactionType(transaction.getType().getDescription());
        kpIssuanceTaskDetailsDTO
            .setCommitmentPeriod(transactionBlock.getOriginalPeriod().getCode());
        kpIssuanceTaskDetailsDTO
            .setUnitType(transactionBlock.getType().name());
        kpIssuanceTaskDetailsDTO
            .setEnvironmentalActivity(transactionBlock.getEnvironmentalActivity());
        AccountBasicInfo acquiringAccount = transaction.getAcquiringAccount();
        AccountDTO accountDTO = accountService.getAccountDTO(acquiringAccount.getAccountIdentifier());
        kpIssuanceTaskDetailsDTO
            .setAcquiringAccount(String.format("%s - %s", acquiringAccount.getAccountFullIdentifier(),
                accountDTO.getAccountDetails().getName()));

        return kpIssuanceTaskDetailsDTO;
    }


    @Protected({
        JuniorAdminCannotClaimTaskRule.class,
        SeniorAdminCanClaimTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
        SeniorAdminCanByAssigneeOfTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
        SeniorAdminCanClaimTaskInitiatedByAdminRule.class,
        FourEyesPrincipleRule.class
    })
    @Override
    public void checkForInvalidCompletePermissions() {
        // implemented for being able to apply permissions using annotations
    }
}
