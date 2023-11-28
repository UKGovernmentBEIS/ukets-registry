package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthorityUserRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.task.web.model.AllTransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionDeliveryAllowancesTransactionTypeTaskService
    implements TransactionTypeTaskService<TransactionTaskDetailsDTO> {

    private final TransactionWithTaskService transactionWithTaskService;

    @Override
    public Set<TransactionType> appliesFor() {
        return Set.of(TransactionType.AuctionDeliveryAllowances);
    }

    @Override
    public TransactionTaskDetailsDTO getDetails(AllTransactionTaskDetailsDTO taskDetailsDTO,
                                                Transaction transaction) {
        return transactionWithTaskService.getTransactionTaskDetails(taskDetailsDTO, transaction);
    }

    @Protected( {
        FourEyesPrincipleRule.class,
        AuthorityUserRule.class
    })
    @Override
    public void checkForInvalidCompletePermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected( {
        AuthorityUserRule.class
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected( {
        AuthorityUserRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }
}
