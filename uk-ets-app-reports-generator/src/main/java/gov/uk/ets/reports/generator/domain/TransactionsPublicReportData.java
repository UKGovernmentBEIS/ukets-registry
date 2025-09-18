package gov.uk.ets.reports.generator.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;


@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionsPublicReportData extends ReportData {

    private String transferIdentifier;
    private String transferringAccountHolderName;
    private String transferringAccountType;
    private String transferringAccountIdentifier; // public account identifier

    private String receivingAccountHolderName;
    private String receivingAccountType;
    private String receivingAccountIdentifier; // public account identifier

    private Integer numberOfUKAllowancesTransferred;
    private String typeOfTransfer;

    private String dateTransferCompleted;
    private String timeTransferCompleted;

}

