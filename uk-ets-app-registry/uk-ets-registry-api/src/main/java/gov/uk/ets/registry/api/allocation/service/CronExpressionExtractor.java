package gov.uk.ets.registry.api.allocation.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

@Service
public class CronExpressionExtractor {

    private final Clock clock;

    public CronExpressionExtractor() {
        this.clock = Clock.systemUTC();
    }

    // Constructor for tests
    CronExpressionExtractor(Clock clock) {
        this.clock = clock;
    }

    /**
     * Returns the next execution date/time for the given cron expression, formatted as 'dd MMM yyyy.hh:mma' (e.g. '10 Sep 2026.10:30AM').
     */
    public String extractNextExecutionTime(String cronExpression) {
        final CronExpression expr = CronExpression.parse(cronExpression);
        final DateTimeFormatter sdf =
            DateTimeFormatter.ofPattern("dd MMM yyyy.hh:mma", Locale.ENGLISH);

        final LocalDateTime now = LocalDateTime.now(clock);
        final LocalDateTime nextExecutionDate = expr.next(now);

        return sdf.format(nextExecutionDate);
    }
}

