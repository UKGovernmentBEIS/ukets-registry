package gov.uk.ets.registry.api.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class UtilsTest {

    @Test
    void isLong() {
        assertFalse(Utils.isLong(null));
        assertFalse(Utils.isLong(""));
        assertFalse(Utils.isLong("zzz"));
        assertTrue(Utils.isLong("10000"));
    }

    @Test
    void concat() {
        assertEquals("First, Middle, Last", Utils.concat(", ", "First", "Middle", "Last"));
        assertEquals("First Middle Last", Utils.concat("", "First", "Middle", "Last"));
        assertEquals("First Middle Last", Utils.concat(null, "First", "Middle", "Last"));
        assertEquals("", Utils.concat(null, null));
        assertEquals("", Utils.concat(null, new String[0]));
        assertEquals("one two", Utils.concat(null, "one", null, "two"));
    }

    @Test
    void format() {
        assertEquals("27/03/2020", Utils.format(
            Date.from(LocalDateTime.of(2020, 03, 27, 10, 10, 10)
                .atZone(ZoneId.systemDefault()
                ).toInstant())));
        assertEquals(null, Utils.format(null));


    }

    @Test
    void formatDay() {
        assertEquals("27 Mar 2020", Utils.formatDay(LocalDateTime.of(2020, 03, 27, 18, 35)));
        assertEquals("27 March 2020", Utils.formatDay(LocalDateTime.of(2020, 03, 27, 18, 35), true));
        assertEquals("27 December 2020", Utils.formatDay(LocalDateTime.of(2020, 12, 27, 18, 35), true));
    }

    @Test
    void formatTime() {
        assertEquals("06:35pm", Utils.formatTime(LocalDateTime.of(2020, 03, 27, 18, 35)));

    }

    @Test
    public void testMaskFullIdentifier() {
        Assert.assertEquals("UK-100-10XXXX56-0-42", Utils.maskFullIdentifier("UK-100-10000256-0-42"));
    }
}
