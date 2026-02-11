package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.io.Serializable;
import java.util.Date;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import lombok.Getter;
import lombok.Setter;

/**
 * The account details transfer object.
 */
@Getter
@Setter
public class AccountDetailsDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 1279881045502179729L;

    /**
     * The name.
     */
    @NotNull
    @Size(max = 256, message = "Account name must not exceed 256 characters.")
    private String name;

    /**
     * The account type. Duplicate of account.type?
     * 
     */
    private String accountType;

    /**
     * The full account number.
     */
    private String accountNumber;

    /**
     * The public account identifier
     */
    private String publicAccountIdentifier;

    /**
     * The account holder identifier.
     */
    private String accountHolderId;

    /**
     * The account's compliance status.
     */
    private ComplianceStatus complianceStatus;

    /**
     * The account holder name for the account.
     */
    private String accountHolderName;

    /**
     * The account status.
     */
    private AccountStatus accountStatus;

    /**
     * The address.
     */
    @Valid
    private AddressDTO address;

    /**
     * The account's opening date.
     * */
    private Date openingDate;

    /**
     * The account's closing date.
     * */
    private Date closingDate;
    
    /**
     * The account's closure reason.
     */
    private String closureReason;

    /**
     * The account's type as enumeration
     * */
    private AccountType accountTypeEnum;

    /**
     * The billing contact details.
     */
    @Valid
    private BillingContactDetailsDTO billingContactDetails;

    /**
     * A billing email.
     * This is not used any more but history tasks will still have this info.
     */
    @Email
    @Size(max = 256, message = "Email must not exceed 256 characters.")
    private String billingEmail1;

    /**
     * A second billing email.
     * This is not used any more but history tasks will still have this info.
     */
    @Email
    @Size(max = 256, message = "Email must not exceed 256 characters.")
    private String billingEmail2;

    /**
     * The flag that indicates whether the account is selling allowances.
     */
    private boolean sellingAllowances;

    /**
     * The sales contact details.
     */
    @Valid
    private SalesContactDetailsDTO salesContactDetails;
    
    /**
     * The flag exclude from billing. 
     * An indication of which accounts should not be included in the invoicing process.
     */
    private boolean excludedFromBilling;
    
    /**
     * The reason for excluding an account from the billing process.
     */
    @Size(max = 256, message = "Exclude from billing remarks must not exceed 256 characters.")
    private String excludedFromBillingRemarks;

    private boolean accountDetailsSameBillingAddress;
}
