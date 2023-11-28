package gov.uk.ets.registry.api.transaction.domain.type;

import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * Encapsulates the transaction configuration.
 */
@Builder
@Getter
public class TransactionConfiguration implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -6821528070714929061L;

    /**
     * The primary code.
     */
    private final Integer primaryCode;

    /**
     * The supplementary code.
     */
    private final Integer supplementaryCode;

    /**
     * The unit types allowed during the transaction.
     */
    @Singular("unit")
    private final List<UnitType> units;

    /**
     * The original commitment period of the units.
     */
    private final CommitmentPeriod originalPeriod;

    /**
     * The applicable commitment period of the units.
     */
    private final CommitmentPeriod applicablePeriod;

    /**
     * The account types that are allowed to send the transaction. Specifying none means no
     * transferring account exists (e.g. in Issuance).
     */
    @Singular("transferring")
    private final List<AccountType> transferringAccountTypes;

    /**
     * The account types that are allowed to receive the transaction. Specifying none means
     * predefined external acquiring accounts are mandated by the business (e.g. CDM SOP account in
     * Transfer to SOP).
     */
    @Singular("acquiring")
    private final List<AccountType> acquiringAccountTypes;

    /**
     * How the acquiring account is specified during this transaction.
     */
    private final TransactionAcquiringAccountMode acquiringAccountMode;

    /**
     * The commitment period of the acquiring account.
     */
    private final CommitmentPeriod acquiringAccountPeriod;

    /**
     * External predefined account.
     */
    private final ExternalPredefinedAccount externalPredefinedAccount;

    /**
     * Whether this is a Kyoto transaction.
     */
    private final boolean kyoto;

    /**
     * Whether this transaction type has ITL Notification.
     */
    private final boolean hasITLNotification;

    /**
     * A description of the transaction type.
     */
    private final String description;

    /**
     * A hint for what the transaction type represents.
     */
    private final String hint;

    /**
     * Whether a delay applies to this transaction type.
     */
    private final boolean delayApplies;

    /**
     * Whether this transaction type can be reversed.
     */
    private final boolean reversalAllowed;

    /**
     * Whether the reversed transaction type has a time limit.
     */
    private final boolean hasReversalTimeLimit;

    /**
     * Whether the proposal of this transaction is enabled.
     */
    private final boolean proposalEnabled;

    /**
     * Whether the proposal is accessible to Authorised Representatives.
     */
    private final boolean isAccessibleToAR;

    /**
     * Whether this transaction type will be hidden from transaction proposal wizard.
     */
    private final boolean hideFromProposalWizard;

    /**
     * The ordering value.
     */
    private final Integer order;

    /**
     * Whether an approval is required for this transaction (it overrides the common
     * business rules that govern the approval configuration).
     */
    private final Boolean approvalRequired;

    /**
     * Whether a single person approval is required for specific transactions.
     */
    private final boolean singlePersonApprovalRequired;

    /**
     * Whether this transaction type is subject to SOP.
     */
    private final Boolean subjectToSOP;

    /**
     * Whether this transaction type is an available filter option for Admin in transaction page.
     */
    private final boolean optionAvailableToAdmin;

    /**
     * Whether this transaction type is an available filter option for Authorised Representatives in transaction page.
     */
    private final boolean optionAvailableToAR;

    /**
     * Whether this transaction type is an available filter option for Authority in transaction page.
     */
    private final boolean optionAvailableToAuthority;

    /**
     * Only units of this country code is available for this transaction type.
     */
    private final String originatingCountryCode;

    /**
     * Indicates a legacy transaction type migrated from CSEUCR.
     */
    private final boolean legacy;

    /**
     * Indicates if the transaction type is allowed.
     */
    private final boolean isAllowedFromPartiallyTransactionRestrictedAccount;

    /**
     * Indicates if the transaction type is allowed.
     */
    private final boolean isAllowedFromTransferPendingAccount;

    /**
     * Indicatees if the transaction type is allowed.
     */
    private final boolean isAllowedFromSuspendedOrPartiallySuspendedAccount;
}

