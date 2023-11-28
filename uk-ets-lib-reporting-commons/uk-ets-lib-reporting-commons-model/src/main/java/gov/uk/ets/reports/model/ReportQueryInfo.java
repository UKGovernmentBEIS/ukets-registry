package gov.uk.ets.reports.model;

import java.util.Date;
import javax.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;


@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ReportQueryInfo {
    private String query;
    
    /**
     * Filter reports with year
     */
    @Digits(integer = 4, fraction = 0, message = "Not a valid year.")
    private Long year;
    
    /**
     * Filter reports with date (from)
     */
    @DateTimeFormat(iso = ISO.DATE)
    private Date from;

    /**
     * Filter reports with date (until)
     */
    @DateTimeFormat(iso = ISO.DATE)
    private Date to;
    
    /**
     * SEF reports with kyoto cp
     */
    @Digits(integer = 1, fraction = 0, message = "Not a valid Kyoto commitment period.")
    private Long commitmentPeriod;

    private String cutOffTime;
    
    private String transactionIdentifier;
    
    private String accountFullIdentifier;

    private String urid;
}
