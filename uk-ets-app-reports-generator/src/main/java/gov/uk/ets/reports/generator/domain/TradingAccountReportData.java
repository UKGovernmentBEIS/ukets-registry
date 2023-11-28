package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TradingAccountReportData extends ReportData {
    private String accountHolderName;
    private String registrationNumber;
    private String address;
    private String city;
    private String stateOrProvince;
    private String postcode;
    private String country;
    private String accountStatus;
    private int openYear;
    private String close;
    private String salesContactEmail;
    private String salesContactPhone;
}
