package gov.uk.ets.reports.model.criteria;


import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The Account search criteria as they received from web client.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AccountSearchCriteria implements ReportCriteria {

    /**
     * The account id or the account name.
     **/
    @Size(min = 3)
    private String accountIdOrName;
    /**
     * The account status.
     */
    private String accountStatus;
    /**
     * The account type.
     */
    private String accountType;
    /**
     * The account holder name.
     */
    @Size(min = 3)
    private String accountHolderName;
    /**
     * The compliance status of account.
     */
    private String complianceStatus;
    /**
     * The permit identifier of account installation(s) or
     * the monitoring plan id of account aircraft operator(s).
     */
    @Size(min = 3)
    private String permitOrMonitoringPlanIdentifier;
    /**
     * The business identifier of the user of authorized representative of account.
     */
    @Size(min = 3)
    private String authorizedRepresentativeUrid;
    /**
     * The regulator type of account.
     */
    private String regulatorType;
}
