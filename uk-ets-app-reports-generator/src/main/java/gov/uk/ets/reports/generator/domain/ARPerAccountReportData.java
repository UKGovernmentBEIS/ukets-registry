package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ARPerAccountReportData extends ReportData {

    private AccountHolder accountHolder;
    private Account account;
    private AccountAccess accountAccess;
    private User user;

}
