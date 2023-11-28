package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author Andreas Karmenis
 * @created 27/01/2023 - 8:47 PM
 * @project uk-ets-app-reports-generator
 */
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class SubmitDocumentsAccountHolderReportData extends ReportData {
    private Long accountHolderId;
    private String accountHolderName;
    private Long requestDocumentId;
    private LocalDateTime taskCreationDate;
    private LocalDateTime taskLastUpdateDate;
    private String taskStatus;
}
