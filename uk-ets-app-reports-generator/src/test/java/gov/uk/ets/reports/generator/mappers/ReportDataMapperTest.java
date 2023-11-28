package gov.uk.ets.reports.generator.mappers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReportDataMapperTest {

    ReportDataMapper mapper = new ReportDataMapper() {
        @Override
        public List mapData(ReportCriteria criteria) {
            return null;
        }

        @Override
        public List mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
            return null;
        }
    };

    @Test
    public void shouldParseDateWithoutMillis() {

        LocalDateTime localDateTime = mapper.parseDate("2021-03-23 13:17:10");

        assertDate(localDateTime);
    }

    @Test
    public void shouldParseDateWithoutFraction1() {

        LocalDateTime localDateTime = mapper.parseDate("2021-03-23 13:17:10.1");

        assertDate(localDateTime);
        assertThat(localDateTime.getNano()).isEqualTo(100000000);
    }

    @Test
    public void shouldParseDateWithoutFraction2() {

        LocalDateTime localDateTime = mapper.parseDate("2021-03-23 13:17:10.12");

        assertDate(localDateTime);
        assertThat(localDateTime.getNano()).isEqualTo(120000000);
    }

    @Test
    public void shouldParseDateWithoutFraction3() {

        LocalDateTime localDateTime = mapper.parseDate("2021-03-23 13:17:10.123");

        assertDate(localDateTime);
        assertThat(localDateTime.getNano()).isEqualTo(123000000);
    }

    @Test
    public void shouldParsePrettyDate() {
        String prettyDate = mapper.prettyDate("2021-03-03 13:17:10.123");

        assertThat(prettyDate).isEqualTo("3 March 2021");
    }

    private void assertDate(LocalDateTime localDateTime) {
        assertThat(localDateTime.getYear()).isEqualTo(2021);
        assertThat(localDateTime.getMonth()).isEqualTo(Month.MARCH);
        assertThat(localDateTime.getDayOfMonth()).isEqualTo(23);
        assertThat(localDateTime.getHour()).isEqualTo(13);
        assertThat(localDateTime.getMinute()).isEqualTo(17);
        assertThat(localDateTime.getSecond()).isEqualTo(10);
    }
}
