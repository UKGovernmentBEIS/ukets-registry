package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthorityUserRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.task.web.model.AllTransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.AllowancesIssuanceTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueAllowancesTransactionTypeTaskService
    implements TransactionTypeTaskService<AllowancesIssuanceTaskDetailsDTO> {

    private final TransactionProposalService transactionProposalService;

    private final AccountService accountService;

    @Override
    public Set<TransactionType> appliesFor() {
        return Set.of(TransactionType.IssueAllowances);
    }

    @Override
    public AllowancesIssuanceTaskDetailsDTO getDetails(AllTransactionTaskDetailsDTO taskDetailsDTO,
                                                       Transaction transaction) {
        AllowancesIssuanceTaskDetailsDTO allowancesIssuanceTaskDetailsDTO =
            new AllowancesIssuanceTaskDetailsDTO(taskDetailsDTO, transaction.getReference());
        List<TransactionBlockSummary> availableUnits =
            transactionProposalService.getAvailableUnits(null, taskDetailsDTO.getTrType());
        TransactionBlock transactionBlock = transaction.getBlocks().get(0);
        availableUnits.stream().filter(a -> a.getYear().equals(transactionBlock.getYear()))
            .forEach(a -> a.setQuantity(transactionBlock.getQuantity().toString()));
        allowancesIssuanceTaskDetailsDTO.setBlocks(availableUnits);

        AccountInfo accountInfo =
            accountService.getAccountInfo(transaction.getAcquiringAccount().getAccountIdentifier());

        AcquiringAccountInfo acquiringAccountInfo =
            AcquiringAccountInfo.acquiringAccountInfoBuilder()
                .identifier(accountInfo.getIdentifier())
                .fullIdentifier(accountInfo.getFullIdentifier())
                .accountName(accountInfo.getAccountName()).build();

        allowancesIssuanceTaskDetailsDTO.setAcquiringAccount(acquiringAccountInfo);
        return allowancesIssuanceTaskDetailsDTO;
    }

    @Protected({
        AuthorityUserRule.class,
        FourEyesPrincipleRule.class
    })
    @Override
    public void checkForInvalidCompletePermissions() {
        // implemented for being able to apply permissions using annotations
    }
}
