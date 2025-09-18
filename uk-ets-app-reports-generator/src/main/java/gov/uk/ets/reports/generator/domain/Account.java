package gov.uk.ets.reports.generator.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

    private String number;
    private String publicIdentifier;
    private String name;
    private String status;
    private String type;
    private String accountHolderName;
    private String complianceStatus;
    private Long balance;
    private String regulatorGroup;
    private LocalDateTime openingDate;
    private LocalDateTime closingDate;
    private Integer numberOfARs;
    private String approvalSecondARRequired;
    private String singlePersonApprovalRequired;
    private String transfersOutsideTal;
    // contains billing address
    private Contact contact;
    private boolean excludedFromBilling;
    private String excludedFromBillingRemarks;

}
