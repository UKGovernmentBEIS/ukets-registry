package gov.uk.ets.reports.generator.domain;

import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class UserReportData extends ReportData {
    private String userId;
    private String userKeycloakId;
    private LocalDate createdOn;
}
