package gov.uk.ets.registry.api.account.web.model;

import static org.hamcrest.core.Is.is;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class DateInfoTest {
    @Test
    public void toDate() throws ParseException {
        DateInfo dateInfo = DateInfo.builder()
                .day("1")
                .month("4")
                .year("1990")
                .build();
        Date date = dateInfo.toDate();
        assertThat(date, is(new SimpleDateFormat("dd/MM/yyyy").parse("01/04/1990")));
    }

    @Test
    public void test_date_info_factory_method() throws ParseException {
        String day = "23";
        String month = "02";
        String year = "1997";
        Date date  = new SimpleDateFormat("dd/MM/yyyy").parse(day + "/" + month + "/" + year);
        DateInfo dateInfo = DateInfo.of(date);
        assertThat(dateInfo.getDay(), is(day));
        assertThat(dateInfo.getMonth(), is(month));
        assertThat(dateInfo.getYear(), is(year));
    }
}