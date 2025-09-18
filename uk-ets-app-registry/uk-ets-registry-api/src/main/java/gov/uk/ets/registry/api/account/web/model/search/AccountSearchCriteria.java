package gov.uk.ets.registry.api.account.web.model.search;

import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * The Account search criteria as they received from web client.
 */
@Getter
@Setter
public class AccountSearchCriteria {

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

    /**
     * The allocation status.
     */
    private AllocationClassification allocationStatus;

    /**
     * The allocation exclusion status.
     */
    private AllocationStatusType allocationWithholdStatus;
    /**
     * The Operator ID (Installation/Aircraft/Maritime)
     */
    @Size(min = 3)
    private String operatorId;

    /**
     * The year of the exclusion from emissions.
     */
    @Digits(integer = 4, fraction = 0, message = "Not a valid year.")
    private Long excludedForYear;

    /**
     * Company IMO number
     */
    private String imo;
}
