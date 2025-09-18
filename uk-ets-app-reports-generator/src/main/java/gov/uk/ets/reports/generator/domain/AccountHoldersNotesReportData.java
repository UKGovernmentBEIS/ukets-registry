package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountHoldersNotesReportData extends ReportData {
    private String accountHolderName;
    private String accountHolderId;
    private LocalDateTime creationDate;
    private String createdBy;
    private String note;
    private String userKnownAs;
    private String userFirstName;
    private String userLastName;
}
