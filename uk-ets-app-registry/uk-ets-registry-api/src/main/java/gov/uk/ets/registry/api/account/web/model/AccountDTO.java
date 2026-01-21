package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsContactDTO;
import gov.uk.ets.registry.api.account.web.model.accountcontact.RegistryContactDTO;
import gov.uk.ets.registry.api.ar.service.dto.ARUpdateActionDTO;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static gov.uk.ets.commons.logging.RequestParamType.ACCOUNT_ID;

/**
 * Represents an account transfer object.
 */
@Getter
@Setter
@ToString
public class AccountDTO {

    @MDCParam(ACCOUNT_ID)
    private Long identifier;

    /**
     * The account type.
     */
    @NotNull(message = "The account type is mandatory")
    private String accountType;

    /**
     * The account holder.
     */
    @Valid
    @NotNull(message = "The account holder is mandatory")
    private AccountHolderDTO accountHolder;

    /**
     * The old account holder, needed for the update holder reset function in task details.
     */
    private AccountHolderDTO oldAccountHolder;

    /**
     * The account holder from which an installation is being transferred to another (The last installation owner).
     */
    private AccountHolderDTO transferringAccountHolder;
    private AccountHolderContactInfoDTO transferringAccountHolderContactInfo;


    /**
     * The legal representatives.
     *
     * @deprecated Replaced by primary and alternative contacts
     */
    @Deprecated(since = "v0.7.1")
    private List<AccountHolderRepresentativeDTO> legalRepresentatives;

    /**
     * The primary and alternative contacts of account holder.
     */
    @Valid
    private AccountHolderContactInfoDTO accountHolderContactInfo;

    /**
     * The old account holder contact info, needed for the update holder reset function in task details.
     */
    private AccountHolderContactInfoDTO oldAccountHolderContactInfo;

    /**
     * The account details.
     */
    @Valid
    @NotNull
    private AccountDetailsDTO accountDetails;

    /**
     * Whether the billing address is same.
     */
    private boolean accountDetailsSameBillingAddress;

    /**
     * The rules regarding trusted accounts.
     */
    @NotNull
    private TrustedAccountListRulesDTO trustedAccountListRules;

    /**
     * The attributes regarding the installation, aircraft or maritime operator.
     */
    @Valid
    private OperatorDTO operator;

    /**
     * The installationToBeTransferred is only used for installation transfers.
     */
    private OperatorDTO installationToBeTransferred;

    /**
     * THe authorised representatives.
     */
    private List<AuthorisedRepresentativeDTO> authorisedRepresentatives;

    /**
     * THe changed account holder ID.
     */
    //TODO: Change this to oldAccountHolderId in order to keep state of the old value and reset if needed.
    private Long changedAccountHolderId;

    /**
     * The old account status.
     */
    private AccountStatus oldAccountStatus;

    /**
     * The account unit balance.
     */
    private Long balance;

    /**
     * The unit type of the account balance.
     */
    private UnitType unitType;

    /**
     * Whether this is a government account.
     */
    private boolean governmentAccount;

    /**
     * Whether the account is KP or not.
     */
    private boolean kyotoAccountType;

    /**
     * Whether transactions are allowed.
     */
    private boolean transactionsAllowed;

    /**
     * Whether an account closure request can be initiated.
     */
    private boolean canBeClosed;

    /**
     * The pending AR update requests.
     */
    private List<ARUpdateActionDTO> pendingARRequests;
    
    /* UKETS-6851 */
    /**
     * ARs added since account opening.
     */
    private Integer addedARs;
    
    /* UKETS-6851 */
    /**
     * ARs removed since account opening.
     */
    private Integer removedARs;

    /**
     * METS Contacts of the account
     */
    private List<MetsContactDTO> metsContacts = new ArrayList<>();

    /**
     * Registry Contacts of the account
     */
    private List<RegistryContactDTO> registryContacts = new ArrayList<>();

    /**
     * Account claim code
     */
    private String accountClaimCode;
}
