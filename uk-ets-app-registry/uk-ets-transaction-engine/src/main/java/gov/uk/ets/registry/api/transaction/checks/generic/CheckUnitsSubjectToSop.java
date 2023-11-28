package gov.uk.ets.registry.api.transaction.checks.generic;

import static java.util.function.Predicate.not;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGrouping;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * The selected unit blocks must be "Subject to SOP".
 */
@Log4j2
@Service("check3015")
@BusinessCheckGrouping(groups = BusinessCheckGroup.UNITS)
public class CheckUnitsSubjectToSop extends ParentBusinessCheck {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {

        List<TransactionBlockSummary> blocks = context.getBlocks();

        if (blocks.stream().anyMatch(not(TransactionBlockSummary::getSubjectToSop))) {
            addError(context, "The selected unit blocks must be \"Subject to SOP\"");
            log.info("Business check error: The selected unit blocks of the transaction {} must be \"Subject to SOP\"", context.getTransaction().getIdentifier());
        }
    }
}
