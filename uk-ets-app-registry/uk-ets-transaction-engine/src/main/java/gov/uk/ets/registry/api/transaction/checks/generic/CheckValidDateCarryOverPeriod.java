package gov.uk.ets.registry.api.transaction.checks.generic;

import static gov.uk.ets.registry.api.transaction.common.LocalDateTimeUtils.isWithinPeriodLimits;
import static gov.uk.ets.registry.api.transaction.common.LocalDateTimeUtils.toDateTime;
import static gov.uk.ets.registry.api.transaction.domain.util.Constants.DATE_TIME_FORMAT_24H_WITH_SECONDS;

import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.checks.ParentBusinessCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The current date must be inside the valid carry over period.
 */
@Service("check3002")
public class CheckValidDateCarryOverPeriod extends ParentBusinessCheck {

    @Value("${business.property.transaction.carry.over.startDate:01/01/2020 00:00:01}")
    private String startDate;

    @Value("${business.property.transaction.carry.over.endDate:31/12/2029 23:59:59}")
    private String endDate;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(BusinessCheckContext context) {

        if (!isWithinPeriodLimits(toDateTime(startDate, DATE_TIME_FORMAT_24H_WITH_SECONDS),
                                  toDateTime(endDate, DATE_TIME_FORMAT_24H_WITH_SECONDS))) {
            addError(context, "The current date must be inside the valid carry over period");
        }
    }
}
