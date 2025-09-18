package gov.uk.ets.registry.api.allocation.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

@Service
public class CronExpressionExtractor {

    /**
     * Returns the next execution time in the form: 'hh:mm' given a cron expression.
     */
    public String extractNextExecutionTime(String cronExpression) {
        final CronExpression expr = CronExpression.parse(cronExpression);
        final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd MMM yyyy.hh:mma", Locale.UK);
        final LocalDateTime nextExecutionDate = expr.next(LocalDateTime.now());
        return sdf.format(nextExecutionDate);
    }
}
