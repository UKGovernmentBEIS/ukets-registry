package gov.uk.ets.registry.api.transaction.checks.generic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Transactions to return excess allocations cannot be made while a return for the same year and allocation table is pending.
 */
@Service("check3018")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
@RequiredArgsConstructor
public class CheckSameYearSameTableReturnExcessPendingTask extends ParentBusinessCheck {

    private final AllocationCalculationService allocationCalculationService;
    private final ObjectMapper objectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {
        TransactionSummary transaction = context.getTransaction();

        AccountSummary transferringAccount = getTransferringAccount(context);
        Optional<AllocationSummary> allocationEntry = allocationCalculationService.getAllocationEntry(
            transferringAccount.getIdentifier(), transaction.getAllocationType(), transaction.getAllocationYear());
        List<Transaction> pendingReturnExcessTransactions = transactionRepository.findByTransferringAccount_AccountIdentifierAndTypeAndStatusNotIn(
            transferringAccount.getIdentifier(), TransactionType.ExcessAllocation, TransactionStatus.getFinalStatuses());
        boolean cannotRequestReturnOfExcess = false;
        if (allocationEntry.isPresent()) {
            cannotRequestReturnOfExcess =
                pendingReturnExcessTransactions.stream()
                   .anyMatch(prt ->
                                 Integer.valueOf(mapValuefromAttributes(prt.getAttributes(), "AllocationYear")).equals(allocationEntry.get().getYear()) &&
                                 AllocationType.parse(mapValuefromAttributes(prt.getAttributes(), "AllocationType")).equals(transaction.getAllocationType()));
        }
        if (cannotRequestReturnOfExcess) {
            addError(context, "Transactions to return excess allocations cannot be made while a return for the same year and allocation table is pending.");
        }
    }

    private String mapValuefromAttributes(String transactionAttributes, String property) {
        try {
            JsonNode node = objectMapper.readTree(transactionAttributes);
            return node.get(property).asText();
        } catch (IOException e) {
            throw new IllegalStateException("Not Serializable " + property + " field");
        }
    }
}
