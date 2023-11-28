package gov.uk.ets.registry.api.transaction.checks;

import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * The service for executing business checks.
 */
@Service
@RequiredArgsConstructor
public class BusinessCheckService {

    /**
     * The checks repository.
     */
    private final BusinessCheckFactory businessCheckFactory;

    /**
     * Performs business checks on the proposed transaction.
     *
     * @param transaction        The proposed transaction.
     * @param businessCheckGroup The business check group.
     * @param hasExtendedScope   Whether the initiator has the appropriate permission.
     */
    public void performChecks(TransactionSummary transaction, BusinessCheckGroup businessCheckGroup,
                              boolean hasExtendedScope) {
        if (transaction == null) {
            throw new RequiredFieldException("Transaction input is required");
        }
        final TransactionType type = transaction.getType();
        if (type == null) {
            throw new RequiredFieldException("Transaction type is required");
        }

        BusinessCheckContext context = new BusinessCheckContext(transaction);
        context.store(BusinessCheckContext.PERMISSION, hasExtendedScope);

        for (BusinessCheck command : businessCheckFactory.getCommonChecks()) {
            if (command.belongsToGroup(businessCheckGroup) && !context.hasError()) {
                command.execute(context);
            }
        }

        TransactionType checkType =
            Constants.isInboundTransaction(transaction) ? TransactionType.InboundTransfer : type;
        for (BusinessCheck command : businessCheckFactory.getChecksForTransactionType(checkType)) {
            if (command.belongsToGroup(businessCheckGroup) && !context.hasError()) {
                command.execute(context);
            }
        }

        final List<BusinessCheckError> errors = context.getErrors();
        if (!CollectionUtils.isEmpty(errors)) {
            BusinessCheckErrorResult businessCheckErrorResult = new BusinessCheckErrorResult();
            businessCheckErrorResult.setErrors(errors);
            throw new BusinessCheckException(businessCheckErrorResult);
        }
    }

    /**
     * Performs business checks on the proposed transaction.
     *
     * @param transaction      The proposed transaction.
     * @param hasExtendedScope Whether the initiator has the appropriate permission.
     */
    public void performChecks(TransactionSummary transaction, boolean hasExtendedScope) {
        performChecks(transaction, null, hasExtendedScope);
    }

    /**
     * Performs business checks on the proposed transaction.
     *
     * @param transaction The proposed transaction.
     */
    public void performChecks(TransactionSummary transaction) {
        performChecks(transaction, null, false);
    }

    /**
     * Performs specific business checks based on specific business check group.
     *
     * @param context            The business check context.
     * @param businessCheckGroup The business check group.
     * @param hasExtendedScope   Whether the initiator has the appropriate permission.
     */
    public void performChecks(BusinessCheckContext context,
                              BusinessCheckGroup businessCheckGroup,
                              boolean hasExtendedScope) {

        context.store(BusinessCheckContext.PERMISSION, hasExtendedScope);
        businessCheckFactory.getCommonChecks()
            .stream()
            .filter(command -> command.belongsToGroup(businessCheckGroup))
            .takeWhile(v -> !context.hasError())
            .forEach(command -> command.execute(context));

        final List<BusinessCheckError> errors = context.getErrors();
        if (!CollectionUtils.isEmpty(errors)) {
            BusinessCheckErrorResult businessCheckErrorResult = new BusinessCheckErrorResult();
            businessCheckErrorResult.setErrors(errors);
            throw new BusinessCheckException(businessCheckErrorResult);
        }
    }
}
