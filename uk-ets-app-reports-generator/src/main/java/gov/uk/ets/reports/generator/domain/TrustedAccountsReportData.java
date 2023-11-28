package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TrustedAccountsReportData extends ReportData {

    private Account account;
    private TrustedAccount trustedAccount;
}
