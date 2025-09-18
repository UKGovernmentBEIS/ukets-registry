package gov.uk.ets.registry.api.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DateUtilsTest {

    @Test
    @DisplayName("Test format BST")
    void testFormatZonedTimeInBST() {
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Europe/Athens")));
    	cal.set(2025, 5, 8, 14, 5, 06);
    	String comment = DateUtils.formatLondonZonedTime(cal.getTime());
		assertThat(comment).isEqualTo("12:05:06 (BST)");
    }
    
    @Test
    @DisplayName("Test format GMT")
    void testFormatZonedTimeInGMT() {
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Europe/Athens")));
    	cal.set(2025, 2, 14, 17, 6, 33);
    	String comment = DateUtils.formatLondonZonedTime(cal.getTime());
		assertThat(comment).isEqualTo("15:06:33 (GMT)");
       
    }
    
}