package gov.uk.ets.registry.api.allocation.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


class CronExpressionExtractorTest {

    @Test
    void shouldExtractTime() {
        CronExpressionExtractor cronExpressionExtractor = new CronExpressionExtractor();
        String time = cronExpressionExtractor.extractNextExecutionTime("0 30 1 * * *");
        assertThat(time.split("\\.")[1]).isEqualTo("01:30am");
    }

}
