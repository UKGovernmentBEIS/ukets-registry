package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class RegulatorNoticeSearchReportData extends ReportData {

    private RegulatorNotice regulatorNotice;
    private AccountHolder accountHolder;
    private CompliantEntity compliantEntity;
}
