package gov.uk.ets.registry.api.transaction.checks.generic;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * The requested quantity exceeds the current account balance for the unit type being transferred.
 */
@Service("check3005")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
public class CheckQuantityAvailableInBalance extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        AccountSummary transferringAccount = getTransferringAccount(context);
        if (transferringAccount == null || Constants.isInboundTransaction(context.getTransaction())) {
            return;
        }

        List<TransactionBlockSummary> blocks = context.getBlocks();
        for (TransactionBlockSummary block : blocks) {
            Long currentQuantity = accountHoldingService.getQuantity(transferringAccount.getIdentifier(), block);
            if (block.calculateQuantity() > currentQuantity) {
                addError(context,
                    "The requested quantity exceeds the current account balance for the unit type being transferred");
                return;
            }
        }
        
        if(TransactionType.Replacement.equals(context.getTransaction().getType())) {
            Optional<AccountSummary> toBeReplacedUnitsAccount = getToBeReplacedUnitsAccount(context);
            if (toBeReplacedUnitsAccount.isEmpty()) {
                return;
            }
            
            Long totalToBeReplaced = context.getBlocks().stream().collect(Collectors.summingLong(TransactionBlockSummary::calculateQuantity));
            String projectNumber = getITLNotice(context).getProjectNumber();
            //Case of REVERSAL_OF_STORAGE_FOR_CDM_PROJECT or NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT
            if(Optional.ofNullable(projectNumber).isPresent()) {
                Long totalHoldingsByProject = accountHoldingService.getHoldingsToBeReplacedForTransactionReplacement(toBeReplacedUnitsAccount.get().getIdentifier(),projectNumber).
                stream().
                collect(Collectors.summingLong(TransactionBlockSummary::getAvailableQuantity));
                
                if(totalToBeReplaced > totalHoldingsByProject) {
                    addError(context,
                            "The requested quantity of lCERs or tCERs for the project to replace exceeds the to be replaced account balance");
                }
            }

        }
    }

}
