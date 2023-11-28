package gov.uk.ets.registry.api.allocation.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;

@Service
public class CronExpressionExtractor {

    /**
     * Returns the next execution time in the form: 'hh:mm' given a cron expression.
     */
    public String extractNextExecutionTime(String cronExpression) {
        final CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
        final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy.hh:mma", Locale.UK);
        final Date nextExecutionDate = generator.next(new Date());
        return sdf.format(nextExecutionDate);
    }
}
