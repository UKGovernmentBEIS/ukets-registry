package gov.uk.ets.registry.api.account.domain;

import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

/**
 * Represents an account.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"identifier"})
public class Account implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -5024047143003655395L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "account_id_generator", sequenceName = "account_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_generator")
    private Long id;

    /**
     * The account name.
     */
    @Convert(converter = StringTrimConverter.class)
    @Column(name = "account_name")
    private String accountName;

    /**
     * The account status.
     */
    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    /**
     * The check digits.
     */
    @Column(name = "check_digits")
    private Integer checkDigits;

    /**
     * The commitment period code.
     */
    @Column(name = "commitment_period_code")
    private Integer commitmentPeriodCode;

    /**
     * The identifier.
     */
    private Long identifier;

    /**
     * The full account identifier
     */
    @NaturalId
    @Column(name = "full_identifier")
    private String fullIdentifier;

    /**
     * The kyoto account type.
     */
    @Column(name = "kyoto_account_type")
    @Enumerated(EnumType.STRING)
    private KyotoAccountType kyotoAccountType;

    /**
     * The opening date.
     */
    @Column(name = "opening_date")
    private Date openingDate;

    /**
     * The closing date.
     */
    @Column(name = "closing_date")
    private Date closingDate;
    
    /**
     * The closure reason for the account.
     */
    @Column(name = "closure_reason")
    private String closureReason;

    /**
     * The registry account type.
     */
    @Column(name = "registry_account_type")
    @Enumerated(EnumType.STRING)
    private RegistryAccountType registryAccountType;

    /**
     * The registry code.
     */
    @Column(name = "registry_code")
    private String registryCode;

    /**
     * Approval of a second AR is required in transfers.
     */
    @Column(name = "approval_second_ar_required")
    private Boolean approvalOfSecondAuthorisedRepresentativeIsRequired;

    /**
     * Transfers are allowed for accounts outside the TAL.
     */
    @Column(name = "transfers_outside_tal")
    private Boolean transfersToAccountsNotOnTheTrustedListAreAllowed;

    /**
     * Whether a single person approval is required for specific transactions.
     */
    @Column(name = "single_person_approval_required")
    private Boolean singlePersonApprovalRequired;

    /**
     * Whether the billing address is same as the account holder address.
     */
    @Column(name = "billing_address_same_as_account_holder_address")
    private Boolean billingAddressSameAsAccountHolderAddress;

    /**
     * The status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private Status status;

    /**
     * The account holder.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_holder_id")
    private AccountHolder accountHolder;
    
    /**
     * The compliant entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliant_entity_id")
    private CompliantEntity compliantEntity;

    /**
     * The contact address.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    /**
     * The compliance status
     */
    @Column(name = "compliance_status")
    @Enumerated(EnumType.STRING)
    private ComplianceStatus complianceStatus;

    /**
     * The account balance.
     * The total quantity an account holds, regardless the unit type.
     */
    @Column(name = "balance")
    private Long balance;

    /**
     * The account balance unit type.
     * An indication of what unit types an account holds.
     * For example:
     * <ul>
     * <li>MULTIPLE means that the account holds units of multiple types,</li>
     * <li>AAU means that the account holds only AAUs etc.</li>
     * </ul>
     */
    @Column(name = "unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType unitType;

    /**
     * The account accesses
     */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountAccess> accountAccesses;

    /**
     * The account type label
     */
    @Column(name = "type_label")
    private String accountType;
    
    @Embedded
    private SalesContact salesContact;
    
    /**
     * The flag exclude from billing. 
     * An indication of which accounts should not be included in the invoicing process.
     */
    @Column(name = "excluded_from_billing")
    private boolean excludedFromBilling;
    
    /**
     * The reason for excluding an account from the billing process.
     */
    @Convert(converter = StringTrimConverter.class)
    @Column(name = "excluded_from_billing_remarks")
    private String excludedFromBillingRemarks;
}
