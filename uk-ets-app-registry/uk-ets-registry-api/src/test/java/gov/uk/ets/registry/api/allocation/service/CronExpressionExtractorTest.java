package gov.uk.ets.registry.api.allocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

class CronExpressionExtractorTest {

    @Test
    void testExtractNextExecutionTime_everyDayAtTenThirty() {
        // Fixed time: 2026-09-10 10:25
        Clock fixedClock = Clock.fixed(
            Instant.parse("2026-09-10T10:25:00Z"),
            ZoneId.of("UTC")
        );

        CronExpressionExtractor extractor = new CronExpressionExtractor(fixedClock);

        // Cron: 10:30 every day
        String cron = "0 30 10 * * *";

        String result = extractor.extractNextExecutionTime(cron);

        // Expected next execution: 10 Sep 2026 at 10:30AM
        assertEquals("10 Sep 2026.10:30AM", result);
    }

    @Test
    void testExtractNextExecutionTime_nextDay() {
        // Fixed time: 2026-09-10 23:59
        Clock fixedClock = Clock.fixed(
            Instant.parse("2026-09-10T23:59:00Z"),
            ZoneId.of("UTC")
        );

        CronExpressionExtractor extractor = new CronExpressionExtractor(fixedClock);

        // Cron: 10:00 every day
        String cron = "0 0 10 * * *";

        String result = extractor.extractNextExecutionTime(cron);

        // Expected next execution: 11 Sep 2026 at 10:00am
        assertEquals("11 Sep 2026.10:00AM", result);
    }
}
