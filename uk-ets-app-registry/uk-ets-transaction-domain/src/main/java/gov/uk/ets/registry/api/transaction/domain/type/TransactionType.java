package gov.uk.ets.registry.api.transaction.domain.type;

import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumerates the various transaction types.
 */
@SuppressWarnings("java:S115")
public enum TransactionType {

    /**
     * Issuance of Kyoto units.
     */
    IssueOfAAUsAndRMUs(TransactionConfiguration.builder()
        .description("Issue AAU or RMU")
        .primaryCode(1)
        .supplementaryCode(0)
        .kyoto(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .acquiring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.EXPLICIT)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Issuance of UK allowances.
     */
    IssueAllowances(TransactionConfiguration.builder()
        .description("Issuance of allowances")
        .primaryCode(1)
        .supplementaryCode(40)
        .unit(UnitType.ALLOWANCE)
        .acquiring(AccountType.UK_TOTAL_QUANTITY_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .acquiringAccountPeriod(CommitmentPeriod.CP0)
        .approvalRequired(true)
        .isAccessibleToAR(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Transfer to SOP for First External Transfer.
     */
    TransferToSOPforFirstExtTransferAAU(TransactionConfiguration.builder()
        .description("Transfer to SOP for First External Transfer of AAU")
        .order(12)
        .primaryCode(3)
        .approvalRequired(true)
        .supplementaryCode(47)
        .kyoto(true)
        .unit(UnitType.AAU)
        .originalPeriod(CommitmentPeriod.CP2)
        .applicablePeriod(CommitmentPeriod.CP2)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_OUTSIDE_REGISTRY)
        .externalPredefinedAccount(ExternalPredefinedAccount.CDM_SOP_ACCOUNT)
        .subjectToSOP(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()
    ),

    /**
     * Retirement.
     */
    Retirement(TransactionConfiguration.builder()
        .description("Retirement")
        .order(17)
        .primaryCode(5)
        .supplementaryCode(0)
        .kyoto(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .transferring(AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT)
        .acquiring(AccountType.RETIREMENT_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Voluntary cancellation.
     */
    CancellationKyotoUnits(TransactionConfiguration.builder()
        .description("Voluntary cancellation of KP units")
        .order(2)
        .primaryCode(4)
        .supplementaryCode(0)
        .kyoto(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.VOLUNTARY_CANCELLATION_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .isAccessibleToAR(true)
        .optionAvailableToAdmin(false)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(true)
        .build()),

    /**
     * Mandatory cancellation.
     */
    MandatoryCancellation(TransactionConfiguration.builder()
        .description("Mandatory cancellation of KP units")
        .order(3)
        .primaryCode(4)
        .supplementaryCode(48)
        .kyoto(true)
        .hasITLNotification(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.MANDATORY_CANCELLATION_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Expiry Date Change.
     */
    ExpiryDateChange(TransactionConfiguration.builder()
        .description("Expiry Date Change of tCER or lCER")
        .order(21)
        .primaryCode(8)
        .supplementaryCode(0)
        .kyoto(true)
        .hasITLNotification(true)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .transferring(AccountType.RETIREMENT_ACCOUNT)
        .transferring(AccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY)
        .transferring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY)
        .transferring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE)
        .transferring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT)
        .acquiring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.RETIREMENT_ACCOUNT)
        .acquiring(AccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY)
        .acquiring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY)
        .acquiring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE)
        .acquiring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.SAME_AS_TRANSFERRING)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Replacement of tCER or lCER.
     */
    Replacement(TransactionConfiguration.builder()
        .description("Replacement of tCER or lCER")
        .order(22)
        .primaryCode(6)
        .supplementaryCode(0)
        .kyoto(true)
        .hasITLNotification(true)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.ERU_FROM_AAU)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY)
        .acquiring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY)
        .acquiring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE)
        .acquiring(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .optionAvailableToAdmin(false)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Art.37 cancellation.
     */
    Art37Cancellation(TransactionConfiguration.builder()
        .description("Art. 3.7ter cancellation of AAU")
        .order(4)
        .primaryCode(4)
        .supplementaryCode(45)
        .kyoto(true)
        .unit(UnitType.AAU)
        .originalPeriod(CommitmentPeriod.CP2)
        .applicablePeriod(CommitmentPeriod.CP2)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .acquiringAccountPeriod(CommitmentPeriod.CP2)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Ambition increase cancellation.
     */
    AmbitionIncreaseCancellation(TransactionConfiguration.builder()
        .description("Ambition increase cancellation of AAU")
        .order(5)
        .primaryCode(4)
        .supplementaryCode(46)
        .kyoto(true)
        .unit(UnitType.AAU)
        .originalPeriod(CommitmentPeriod.CP2)
        .applicablePeriod(CommitmentPeriod.CP2)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .acquiringAccountPeriod(CommitmentPeriod.CP2)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    ConversionCP1(TransactionConfiguration.builder()
        .description("Conversion CP1")
        .order(23)
        .primaryCode(2)
        .supplementaryCode(0)
        .kyoto(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .originalPeriod(CommitmentPeriod.CP1)
        .applicablePeriod(CommitmentPeriod.CP1)
        .originatingCountryCode(Constants.KYOTO_REGISTRY_CODE)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.SAME_AS_TRANSFERRING)
        .acquiringAccountPeriod(CommitmentPeriod.CP2)
        .build()),

    ConversionA(TransactionConfiguration.builder()
        .description("Conversion of AAUs or RMUs to ERUs prior to Transfer to SOP (Conversion A)")
        .order(18)
        .primaryCode(2)
        .supplementaryCode(56)
        .kyoto(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .originalPeriod(CommitmentPeriod.CP2)
        .applicablePeriod(CommitmentPeriod.CP2)
        .originatingCountryCode(Constants.KYOTO_REGISTRY_CODE)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.SAME_AS_TRANSFERRING)
        .acquiringAccountPeriod(CommitmentPeriod.CP2)
        .build()),

    ConversionB(TransactionConfiguration.builder()
        .description("Conversion of AAUs or RMUs to ERUs after the Transfer to SOP (Conversion B)")
        .order(20)
        .primaryCode(2)
        .supplementaryCode(57)
        .kyoto(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .originalPeriod(CommitmentPeriod.CP2)
        .applicablePeriod(CommitmentPeriod.CP2)
        .originatingCountryCode(Constants.KYOTO_REGISTRY_CODE)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.SAME_AS_TRANSFERRING)
        .acquiringAccountPeriod(CommitmentPeriod.CP2)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    TransferToSOPForConversionOfERU(TransactionConfiguration.builder()
        .description("Transfer to SOP for Conversion of ERU")
        .order(19)
        .primaryCode(3)
        .approvalRequired(true)
        .supplementaryCode(49)
        .kyoto(true)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .subjectToSOP(true)
        .originalPeriod(CommitmentPeriod.CP2)
        .applicablePeriod(CommitmentPeriod.CP2)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_OUTSIDE_REGISTRY)
        .externalPredefinedAccount(ExternalPredefinedAccount.CDM_SOP_ACCOUNT)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Internal Transfer.
     */
    InternalTransfer(TransactionConfiguration.builder()
        .description("Transfer KP units")
        .order(1)
        .primaryCode(10)
        .supplementaryCode(0)
        .proposalEnabled(true)
        .kyoto(true)
        .delayApplies(true)
        .hasITLNotification(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.EXPLICIT)
        .isAccessibleToAR(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(true)
        .build()),

    /**
     * External Transfer.
     */
    ExternalTransfer(TransactionConfiguration.builder()
        .description("Transfer KP units")
        .order(1)
        .primaryCode(3)
        .approvalRequired(true)
        .supplementaryCode(0)
        .proposalEnabled(true)
        .kyoto(true)
        .delayApplies(true)
        .hasITLNotification(true)
        .unit(UnitType.AAU)
        .unit(UnitType.RMU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .transferring(AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT)
        .transferring(AccountType.PENDING_ACCOUNT)
        .acquiring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.NET_SOURCE_CANCELLATION_ACCOUNT)
        .acquiring(AccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT)
        .acquiring(AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT)
        .acquiring(AccountType.EXCESS_ISSUANCE_CANCELLATION_ACCOUNT)
        .acquiring(AccountType.CCS_NET_REVERSAL_CANCELLATION_ACCOUNT)
        .acquiring(AccountType.CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.EXPLICIT)
        .isAccessibleToAR(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(true)
        .build()),

    /**
     * Carry-over AAU.
     */
    CarryOver_AAU(TransactionConfiguration.builder()
        .description("Carry-over AAU")
        .order(10)
        .primaryCode(7)
        .supplementaryCode(39)
        .kyoto(true)
        .unit(UnitType.AAU)
        .originalPeriod(CommitmentPeriod.CP1)
        .applicablePeriod(CommitmentPeriod.CP1)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .acquiringAccountPeriod(CommitmentPeriod.CP2)
        .proposalEnabled(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Carry-over CER / ERU.
     */
    CarryOver_CER_ERU_FROM_AAU(TransactionConfiguration.builder()
        .description("Carry-over CER or ERU from AAU units")
        .order(11)
        .primaryCode(7)
        .supplementaryCode(38)
        .kyoto(true)
        .unit(UnitType.CER)
        .unit(UnitType.ERU_FROM_AAU)
        .originalPeriod(CommitmentPeriod.CP1)
        .applicablePeriod(CommitmentPeriod.CP1)
        .transferring(AccountType.PARTY_HOLDING_ACCOUNT)
        .transferring(AccountType.PERSON_HOLDING_ACCOUNT)
        .transferring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.PARTY_HOLDING_ACCOUNT)
        .acquiring(AccountType.PERSON_HOLDING_ACCOUNT)
        .acquiring(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.SAME_AS_TRANSFERRING)
        .acquiringAccountPeriod(CommitmentPeriod.CP2)
        .proposalEnabled(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Surrender of Allowances.
     */
    SurrenderAllowances(TransactionConfiguration.builder()
        .description("Surrender allowances")
        .hint("Surrender of UK ETS allowances against emissions to meet a " +
            "UK ETS compliance obligation. Completes immediately.")
        .primaryCode(10)
        .supplementaryCode(2)
        .proposalEnabled(true)
        .kyoto(false)
        .delayApplies(false)
        .unit(UnitType.ALLOWANCE)
        .reversalAllowed(true)
        .singlePersonApprovalRequired(true)
        .transferring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.UK_SURRENDER_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .isAccessibleToAR(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(true)
        .isAllowedFromPartiallyTransactionRestrictedAccount(true)
        .build()),

    /**
     * Reversal of surrender of allowances.
     */
    ReverseSurrenderAllowances(TransactionConfiguration.builder()
        .description("Reversal of surrender of allowances")
        .primaryCode(10)
        .supplementaryCode(82)
        .proposalEnabled(true)
        .kyoto(false)
        .delayApplies(false)
        .hasReversalTimeLimit(true)
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.UK_SURRENDER_ACCOUNT)
        .acquiring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.REVERSED)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .hideFromProposalWizard(true)
        .build()),

    /**
     * Inbound transfer.
     */
    InboundTransfer(TransactionConfiguration.builder()
        .description("Inbound transfer")
        .primaryCode(10)
        .supplementaryCode(100)
        .hideFromProposalWizard(true)
        .optionAvailableToAdmin(false)
        .optionAvailableToAuthority(false)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Central transfer of allowances.
     */
    CentralTransferAllowances(TransactionConfiguration.builder()
        .description("Central transfer")
        .order(14)
        .primaryCode(10)
        .supplementaryCode(51)
        .proposalEnabled(true)
        .kyoto(false)
        .delayApplies(false)
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.UK_TOTAL_QUANTITY_ACCOUNT)
        .transferring(AccountType.UK_AUCTION_ACCOUNT)
        .transferring(AccountType.UK_ALLOCATION_ACCOUNT)
        .transferring(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT)
        .transferring(AccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT)
        .transferring(AccountType.UK_GENERAL_HOLDING_ACCOUNT)
        .acquiring(AccountType.UK_TOTAL_QUANTITY_ACCOUNT)
        .acquiring(AccountType.UK_AUCTION_ACCOUNT)
        .acquiring(AccountType.UK_ALLOCATION_ACCOUNT)
        .acquiring(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT)
        .acquiring(AccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT)
        .acquiring(AccountType.UK_GENERAL_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.EXPLICIT)
        .isAccessibleToAR(true)
        .approvalRequired(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Closure transfer of allowances.
     */
    ClosureTransfer(TransactionConfiguration.builder()
        .description("Closure transfer")
        .hint("Transfers UK ETS allowances to the General Holding Account. "
            + "Completes immediately after approval from 2 Senior Registry Administrators.")
        .primaryCode(10)
        .supplementaryCode(91)
        .proposalEnabled(true)
        .kyoto(false)
        .delayApplies(false)
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.TRADING_ACCOUNT)
        .transferring(AccountType.UK_AUCTION_DELIVERY_ACCOUNT)
        .acquiring(AccountType.UK_GENERAL_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .isAccessibleToAR(false)
        .approvalRequired(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(false)
        .build()),
    
    /**
     * Transfer of allowances.
     */
    TransferAllowances(TransactionConfiguration.builder()
        .description("Transfer allowances")
        .hint("A transfer of UK ETS allowances from one UK ETS Registry account to another. " +
            "This is usually used for trading, and is subject to transaction restrictions and delays.")
        .order(13)
        .primaryCode(10)
        .supplementaryCode(50)
        .proposalEnabled(true)
        .kyoto(false)
        .delayApplies(true)
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.UK_GENERAL_HOLDING_ACCOUNT)
        .transferring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.TRADING_ACCOUNT)
        .transferring(AccountType.UK_AUCTION_DELIVERY_ACCOUNT)
        .acquiring(AccountType.UK_GENERAL_HOLDING_ACCOUNT)
        .acquiring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.TRADING_ACCOUNT)
        .acquiring(AccountType.UK_AUCTION_DELIVERY_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.EXPLICIT)
        .isAccessibleToAR(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(true)
        .build()),

    /**
     * Allocation of allowances.
     */
    AllocateAllowances(TransactionConfiguration.builder()
        .description("Allocation of allowances")
        .order(16)
        .primaryCode(10)
        .supplementaryCode(40)
        .proposalEnabled(true)
        .kyoto(false)
        .delayApplies(false)
        .unit(UnitType.ALLOWANCE)
        .reversalAllowed(true)
        .transferring(AccountType.UK_ALLOCATION_ACCOUNT)
        .transferring(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT)
        .acquiring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.EXPLICIT)
        .approvalRequired(false)
        .hideFromProposalWizard(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Return of excess allocation.
     */
    ExcessAllocation(TransactionConfiguration.builder()
        .description("Return of excess allocation")
        .primaryCode(10)
        .supplementaryCode(42)
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .hideFromProposalWizard(true)
        .isAccessibleToAR(true)
        .singlePersonApprovalRequired(true)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .acquiring(AccountType.UK_ALLOCATION_ACCOUNT)
        .acquiring(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT)
        .isAllowedFromPartiallyTransactionRestrictedAccount(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(true)
        .build()),

    /**
     * Return of excess auction.
     */
    ExcessAuction(TransactionConfiguration.builder()
        .description("Return of excess auction")
        .primaryCode(10)
        .supplementaryCode(43)
        .proposalEnabled(true)
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.UK_AUCTION_DELIVERY_ACCOUNT)
        .isAccessibleToAR(true)
        .isAllowedFromPartiallyTransactionRestrictedAccount(true)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .acquiring(AccountType.UK_AUCTION_ACCOUNT)
        .optionAvailableToAdmin(true)
        .optionAvailableToAR(true)
        .optionAvailableToAuthority(true)
        .build()),

    /**
     * Transfer of allowances to auction delivery account.
     */
    AuctionDeliveryAllowances(TransactionConfiguration.builder()
        .description("Transfer of allowances to auction delivery account")
        .order(15)
        .primaryCode(10)
        .supplementaryCode(37)
        .proposalEnabled(true)
        .kyoto(false)
        .delayApplies(false)
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.UK_AUCTION_ACCOUNT)
        .acquiring(AccountType.UK_AUCTION_DELIVERY_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .acquiringAccountPeriod(CommitmentPeriod.CP0)
        .isAccessibleToAR(true)
        .optionAvailableToAdmin(false)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(false)
        .build()),

    /**
     * Reversal of allocation of allowances.
     */
    ReverseAllocateAllowances(TransactionConfiguration.builder()
        .description("Reversal of allocation of allowances")
        .primaryCode(10)
        .supplementaryCode(41)
        .kyoto(false)
        .unit(UnitType.ALLOWANCE)
        .acquiringAccountMode(TransactionAcquiringAccountMode.REVERSED)
        .acquiring(AccountType.UK_ALLOCATION_ACCOUNT)
        .transferring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .hideFromProposalWizard(true)
        .isAllowedFromPartiallyTransactionRestrictedAccount(true)
        .build()),

    /**
     * Deletion of Allowances.
     */
    DeletionOfAllowances(TransactionConfiguration.builder()
        .description("Deletion of allowances")
        .hint("Permanently removes UK ETS allowances from your Registry account. " +
            "This must not be used to surrender against UK ETS emissions or for trading. " +
            "Completes immediately after approval from 2 Authorised Representatives.")
        .primaryCode(10)
        .supplementaryCode(90)
        .kyoto(false)
        .reversalAllowed(true)
        .unit(UnitType.ALLOWANCE)
        .acquiringAccountMode(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)
        .transferring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .transferring(AccountType.TRADING_ACCOUNT)
        .acquiring(AccountType.UK_DELETION_ACCOUNT)
        .isAccessibleToAR(true)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .optionAvailableToAR(true)
        .delayApplies(false)
        .approvalRequired(true)
        .build()),

    /**
     * Reversal of deletion of Allowances.
     */
    ReverseDeletionOfAllowances(TransactionConfiguration.builder()
        .description("Reversal of deletion of allowances")
        .primaryCode(10)
        .supplementaryCode(190)
        .proposalEnabled(true)
        .kyoto(false)
        .delayApplies(false)
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.UK_DELETION_ACCOUNT)
        .acquiring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.TRADING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.REVERSED)
        .optionAvailableToAdmin(true)
        .optionAvailableToAuthority(true)
        .hideFromProposalWizard(true)
        .build()),

    /**
     * Balance installation transfer of allowances.
     */
    BalanceInstallationTransferAllowances(TransactionConfiguration.builder()
        .primaryCode(10)
        .supplementaryCode(99)
        .approvalRequired(false)
        .hideFromProposalWizard(true)
        .proposalEnabled(false)
        .optionAvailableToAdmin(true)
        .description("Balance installation transfer allowances")
        .unit(UnitType.ALLOWANCE)
        .transferring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .acquiring(AccountType.OPERATOR_HOLDING_ACCOUNT)
        .acquiringAccountMode(TransactionAcquiringAccountMode.EXPLICIT)
        .build()),

    /**
     * Legacy CSEUCR transactions.
     */
    IssuanceDecoupling(TransactionConfiguration.builder()
        .description("Issuance Decoupling")
        .primaryCode(1)
        .supplementaryCode(32)
        .kyoto(true)
        .legacy(true)
        .build()),
    ExternalTransferCP0(TransactionConfiguration.builder()
        .description("External Transfer CP0")
        .primaryCode(3)
        .supplementaryCode(21)
        .kyoto(true)
        .legacy(true)
        .build()),
    AllocationOfFormerEUA(TransactionConfiguration.builder()
        .description("Allocation of former EUA")
        .primaryCode(10)
        .supplementaryCode(53)
        .kyoto(true)
        .legacy(true)
        .build()),
    IssuanceCP0(TransactionConfiguration.builder()
        .description("Issuance CP0")
        .primaryCode(1)
        .supplementaryCode(51)
        .kyoto(true)
        .legacy(true)
        .build()),
    RetirementCP0(TransactionConfiguration.builder()
        .description("Retirement CP0")
        .primaryCode(4)
        .supplementaryCode(3)
        .kyoto(true)
        .legacy(true)
        .build()),
    IssuanceOfFormerEUA(TransactionConfiguration.builder()
        .description("Issuance of former EUA")
        .primaryCode(10)
        .supplementaryCode(52)
        .kyoto(true)
        .legacy(true)
        .build()),
    CancellationCP0(TransactionConfiguration.builder()
        .description("Cancellation CP0")
        .primaryCode(10)
        .supplementaryCode(1)
        .kyoto(true)
        .legacy(true)
        .build()),
    RetirementOfSurrenderedFormerEUA(TransactionConfiguration.builder()
        .description("Retirement of surrendered former EUA")
        .primaryCode(5)
        .supplementaryCode(1)
        .kyoto(true)
        .legacy(true)
        .build()),
    ConversionOfSurrenderedFormerEUA(TransactionConfiguration.builder()
        .description("Conversion of surrendered former EUA")
        .primaryCode(10)
        .supplementaryCode(61)
        .kyoto(true)
        .legacy(true)
        .build()),
    CorrectiveTransactionForReversal_1(TransactionConfiguration.builder()
        .description("Corrective transaction for reversal 1")
        .primaryCode(10)
        .supplementaryCode(92)
        .kyoto(true)
        .legacy(true)
        .build()),
    Correction(TransactionConfiguration.builder()
        .description("Correction")
        .primaryCode(10)
        .supplementaryCode(55)
        .kyoto(true)
        .legacy(true)
        .build()),
    SurrenderKyotoUnits(TransactionConfiguration.builder()
        .description("Surrender Kyoto units")
        .primaryCode(3)
        .supplementaryCode(2)
        .kyoto(true)
        .legacy(true)
        .build()),
    CancellationAgainstDeletion(TransactionConfiguration.builder()
        .description("Cancellation against deletion")
        .primaryCode(4)
        .supplementaryCode(91)
        .kyoto(true)
        .legacy(true)
        .build()),
    ReversalSurrenderKyoto(TransactionConfiguration.builder()
        .description("Reversal surrender Kyoto")
        .primaryCode(3)
        .supplementaryCode(82)
        .kyoto(true)
        .legacy(true)
        .build()),
    SetAside(TransactionConfiguration.builder()
        .description("Set aside")
        .primaryCode(3)
        .supplementaryCode(75)
        .kyoto(true)
        .legacy(true)
        .build());
    /**
     * The transaction configuration.
     */
    private final TransactionConfiguration transactionConfiguration;

    /**
     * Constructor.
     *
     * @param transactionConfiguration The transaction configuration.
     */
    TransactionType(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    /**
     * Returns the primary code.
     *
     * @return the primary code.
     */
    public Integer getPrimaryCode() {
        return transactionConfiguration.getPrimaryCode();
    }

    /**
     * Returns the supplementary code.
     *
     * @return the supplementary code.
     */
    public Integer getSupplementaryCode() {
        return transactionConfiguration.getSupplementaryCode();
    }

    /**
     * Returns the unit types allowed during this transaction type.
     *
     * @return some unit types.
     */
    public List<UnitType> getUnits() {
        return transactionConfiguration.getUnits();
    }

    /**
     * Returns the account types allowed to send the transaction.
     *
     * @return some account types.
     */
    public List<AccountType> getTransferringAccountTypes() {
        return transactionConfiguration.getTransferringAccountTypes();
    }

    /**
     * Returns the account types allowed to receive the transaction.
     *
     * @return some account types.
     */
    public List<AccountType> getAcquiringAccountTypes() {
        return transactionConfiguration.getAcquiringAccountTypes();
    }

    /**
     * Returns whether this transaction is a Kyoto one.
     *
     * @return false/true
     */
    public boolean isKyoto() {
        return transactionConfiguration.isKyoto();
    }

    /**
     * Returns whether this transaction has ITL Notification.
     *
     * @return false/true
     */
    public boolean hasITLNotification() {
        return transactionConfiguration.isHasITLNotification();
    }

    /**
     * Returns whether this transaction is Subject to SOP.
     *
     * @return false/true/null
     */
    public Boolean isSubjectToSOP() {
        return transactionConfiguration.getSubjectToSOP();
    }

    /**
     * Returns the original commitment period of the units.
     *
     * @return a commitment period.
     */
    public CommitmentPeriod getUnitsOriginalCommitmentPeriod() {
        return transactionConfiguration.getOriginalPeriod();
    }

    /**
     * Returns the applicable commitment period of the units.
     *
     * @return a commitment period.
     */
    public CommitmentPeriod getUnitsApplicableCommitmentPeriod() {
        return transactionConfiguration.getApplicablePeriod();
    }

    /**
     * Returns whether the acquiring account of this transaction type is hosted outside the
     * registry.
     *
     * @return false / true
     */
    public boolean isExternal() {
        return transactionConfiguration.getPrimaryCode() == 3;
    }

    /**
     * Returns a description of the transaction type.
     *
     * @return a description
     */
    public String getDescription() {
        return transactionConfiguration.getDescription();
    }

    /**
     * Returns a hint for what the transaction type represents.
     *
     * @return a hint.
     */
    public String getHint() {
        return transactionConfiguration.getHint();
    }

    /**
     * Returns whether a delay applies.
     *
     * @return false/true
     */
    public Boolean getDelayApplies() {
        return transactionConfiguration.isDelayApplies();
    }

    /**
     * Returns whether this transaction type can be reversed.
     *
     * @return false/true
     */
    public boolean getReversalAllowed() {
        return transactionConfiguration.isReversalAllowed();
    }

    /**
     * Returns whether this reversed transaction type has a time limit.
     *
     * @return false/true
     */
    public boolean hasReversalTimeLimit() {
        return transactionConfiguration.isHasReversalTimeLimit();
    }

    /**
     * Returns whether this transaction type is enabled.
     *
     * @return false/true
     */
    public Boolean getProposalEnabled() {
        return transactionConfiguration.isProposalEnabled();
    }

    /**
     * Returns whether this transaction type is also Accessible to Authorised Representatives.
     *
     * @return false/true
     */
    public Boolean isAccessibleToAR() {
        return transactionConfiguration.isAccessibleToAR();
    }

    /**
     * Returns whether this transaction type should not be available to the transaction proposal wizard.
     *
     * @return false/true
     */
    public boolean hideFromProposalWizard() {
        return transactionConfiguration.isHideFromProposalWizard();
    }

    /**
     * Returns the sorting order of this transaction type.
     *
     * @return a number.
     */
    public Integer getOrder() {
        return transactionConfiguration.getOrder();
    }

    /**
     * Whether the transaction is configured with thi mode.
     *
     * @param input The acquiring account mode.
     * @return false/true
     */
    public boolean has(TransactionAcquiringAccountMode input) {
        return transactionConfiguration.getAcquiringAccountMode().equals(input);
    }

    /**
     * Returns the external predefined account associated with this transaction.
     *
     * @return a predefined account.
     */
    public ExternalPredefinedAccount getExternalAccount() {
        return transactionConfiguration.getExternalPredefinedAccount();
    }

    /**
     * Returns the predefined account commitment period.
     *
     * @return a commitment period.
     */
    public CommitmentPeriod getPredefinedAccountCommitmentPeriod() {
        return transactionConfiguration.getAcquiringAccountPeriod();
    }

    /**
     * Identifies the transaction type based on the primary and supplementary codes.
     *
     * @param primaryCode       The primary code.
     * @param supplementaryCode The supplementary code.
     * @return a transaction type.
     */
    public static TransactionType of(int primaryCode, int supplementaryCode) {
        TransactionType result = null;
        Optional<TransactionType> optional = Stream.of(values())
            .filter(type ->
                type.getPrimaryCode().equals(primaryCode) &&
                    type.getSupplementaryCode().equals(supplementaryCode))
            .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }

    /**
     * Returns a {@link Set} of the transaction types that are used in admin scope/have false limited scope.
     *
     * @return The collection of the transaction types.
     */
    public static Set<TransactionType> tasksAccessibleOnlyToAdmin() {
        return Stream.of(TransactionType.values())
            .filter(Predicate.not(TransactionType::isAccessibleToAR))
            .collect(Collectors.toSet());
    }

    /**
     * Returns a {@link Set} of the transaction types displayed to Admin pick list in transactions page.
     *
     * @return The collection of the transaction types.
     */
    public static Set<TransactionType> transactionTypesAvailableToAdmin() {
        return Stream.of(TransactionType.values())
            .filter(TransactionType::isOptionAvailableToAdmin)
            .collect(Collectors.toSet());
    }

    /**
     * Returns a {@link Set} of the transaction types displayed to AR pick list in transactions page.
     *
     * @return The collection of the transaction types.
     */
    public static Set<TransactionType> transactionTypesAvailableToAR() {
        return Stream.of(TransactionType.values())
            .filter(TransactionType::isOptionAvailableToAR)
            .collect(Collectors.toSet());
    }

    /**
     * Returns a {@link Set} of the transaction types displayed to Authority pick list in transactions page.
     *
     * @return The collection of the transaction types.
     */
    public static Set<TransactionType> transactionTypesAvailableToAuthority() {
        return Stream.of(TransactionType.values())
            .filter(TransactionType::isOptionAvailableToAuthority)
            .collect(Collectors.toSet());
    }

    /**
     * Whether an approval is required.
     *
     * @return false/true.
     */
    public Boolean getApprovalRequired() {
        return transactionConfiguration.getApprovalRequired();
    }

    /**
     * Whether a single person approval is required for specific transactions.
     *
     * @return true/false.
     */
    public boolean isSinglePersonApprovalRequired() {
        return transactionConfiguration.isSinglePersonApprovalRequired();
    }

    /**
     * Whether the type is IssueOfAAUsAndRMUs or IssueAllowances.
     *
     * @return true/false
     */
    public boolean isIssuance() {
        return TransactionType.IssueOfAAUsAndRMUs.equals(this) || TransactionType.IssueAllowances.equals(this);
    }

    /**
     * Returns whether this transaction type is available option for Administrator.
     *
     * @return false/true
     */
    public boolean isOptionAvailableToAdmin() {
        return transactionConfiguration.isOptionAvailableToAdmin();
    }

    /**
     * Returns whether this transaction type is available option for Authority.
     *
     * @return false/true
     */
    public boolean isOptionAvailableToAuthority() {
        return transactionConfiguration.isOptionAvailableToAuthority();
    }

    /**
     * Returns whether this transaction type is available option for AR.
     *
     * @return false/true
     */
    public boolean isOptionAvailableToAR() {
        return transactionConfiguration.isOptionAvailableToAR();
    }


    /**
     * Returns whether this transaction type is available option for Surrender AR.
     *
     * @return false/true
     */
    public boolean isOptionAvailableToSurrenderAR() {
        return ExcessAllocation.equals(this) || SurrenderAllowances.equals(this);
    }

    /**
     * Returns the originating country code of this transaction type i.e GB
     *
     * @return The country code
     */
    public String getOriginatingCountryCode() {
        return transactionConfiguration.getOriginatingCountryCode();
    }

    /**
     * Returns true if the transaction is migrated from CSEUCR
     *
     * @return false/true
     */
    public boolean isLegacy() {
        return transactionConfiguration.isLegacy();
    }

    /**
     * Returns true/false if the transaction is allowed for Blocked accounts.
     *
     * @return true/false
     */
    public boolean isAllowedFromPartiallyTransactionRestrictedAccount() {
        return transactionConfiguration.isAllowedFromPartiallyTransactionRestrictedAccount();
    }

    public boolean isAllowedFromTransferPendingAccount() {
        return transactionConfiguration.isAllowedFromTransferPendingAccount();
    }

    public boolean isAllowedFromSuspendedOrPartiallySuspendedAccount() {
        return transactionConfiguration.isAllowedFromSuspendedOrPartiallySuspendedAccount();
    }

    public static boolean hasAllocationBasedAttribute(TransactionType type) {
        return ExcessAllocation.equals(type) || ReverseAllocateAllowances.equals(type);
    }

    public static List<TransactionType> getReversalTransactionTypes() {
        return Stream.of(TransactionType.values())
        		     .filter(TransactionType::isReversalTransaction)
        		     .collect(Collectors.toList());
    }

    public static boolean isReversalTransaction(TransactionType transactionType) {
        return TransactionAcquiringAccountMode.REVERSED.equals(
            transactionType.transactionConfiguration.getAcquiringAccountMode());
    }

    public static List<TransactionType> showAccountNameInsteadOfNumber() {
        return List.of(SurrenderAllowances, InboundTransfer, ExcessAllocation, ReverseAllocateAllowances,
            ExcessAuction, CentralTransferAllowances, DeletionOfAllowances);
    }

    public TransactionAcquiringAccountMode getAcquiringAccountMode() {
        return transactionConfiguration.getAcquiringAccountMode();
    }
}
