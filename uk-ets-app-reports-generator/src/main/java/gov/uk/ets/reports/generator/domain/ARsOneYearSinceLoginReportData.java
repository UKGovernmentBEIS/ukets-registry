package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ARsOneYearSinceLoginReportData extends ReportData {
    private String accountHolderName;
    private String accountId;
    private String accountType;
    private String accountStatus;
    private Integer firstReportingYear;
    private Integer lastReportingYear;
    private String surrenderStatus;
    private String userUrid;
    private String userFirstName;
    private String userLastName;
    private String userStatus;
    private String userEmail;
    private Long weeksSinceRegistered;
    private Long weeksSinceLogin;
    private String primaryContactFirstName;
    private String primaryContactLastName;
    private String primaryContactEmail;
    private String alternativeContactFirstName;
    private String alternativeContactLastName;
    private String alternativeContactEmail;
}
