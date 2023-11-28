package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Data transfer object for exchanging date info of structure of {day, month, year}
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateInfo implements Serializable {

    /**
     * Factory method for creating a {@link DateInfo} object from a {@link Date} object
     *
     * @param date The {@link Date}
     * @return The {@link DateInfo} corresponded to the date parameter
     */
    public static DateInfo of(Date date) {
        return DateInfo.builder()
            .day(new SimpleDateFormat("dd").format(date))
            .month(new SimpleDateFormat("MM").format(date))
            .year(new SimpleDateFormat("yyyy").format(date))
            .build();
    }

    @NotNull
    private String day;
    @NotNull
    private String month;
    @NotNull
    private String year;

    /**
     * Returns the {@link Date} which is built from the day, month and year fields
     *
     * @return The corresponded {@link Date} to this
     * @throws ParseException When the combination of day, month, year is invalid
     */
    public Date toDate() throws ParseException {
        String dayStr = StringUtils.leftPad(day, 2, '0');
        String monthStr = StringUtils.leftPad(month, 2, '0');
        String yearStr = StringUtils.leftPad(year, 4, '0');
        String dateStr = dayStr + "/" + monthStr + "/" + yearStr;
        return new SimpleDateFormat(Constants.DATE_FORMAT).parse(dateStr);
    }

    @Override
    public String toString() {
        return "DateInfo{" +
            "day='" + day + '\'' +
            ", month='" + month + '\'' +
            ", year='" + year + '\'' +
            '}';
    }
}
