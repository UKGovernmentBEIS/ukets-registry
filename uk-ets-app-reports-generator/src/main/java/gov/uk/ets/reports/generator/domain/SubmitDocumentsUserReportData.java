package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author Andreas Karmenis
 * @created 18/01/2023 - 7:11 PM
 * @project uk-ets-app-reports-generator
 */
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SubmitDocumentsUserReportData extends ReportData {
    private String userId;
    private String firstName;
    private String lastName;
    private Long requestDocumentId;
    private LocalDateTime taskCreationDate;
    private LocalDateTime taskLastUpdateDate;
    private String taskStatus;
}
