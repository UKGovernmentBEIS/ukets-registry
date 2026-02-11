package gov.uk.ets.registry.api.transaction.domain.type;

import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Enumerates the various account types.
 */
@Getter
public enum AccountType {

    /**
     * The Operator holding account (ETS) type.
     */
    OPERATOR_HOLDING_ACCOUNT(
        RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - Operator holding account"),

    /**
     * The Aircraft operator holding account (ETS) type.
     */
    AIRCRAFT_OPERATOR_HOLDING_ACCOUNT(
        RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - Aircraft operator holding account"),

    /**
     * The Maritime operator holding account (ETS) type.
     */
    MARITIME_OPERATOR_HOLDING_ACCOUNT(
            RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
            KyotoAccountType.PARTY_HOLDING_ACCOUNT,
            "ETS - Maritime operator holding account"),

    /**
     * The Trading account (ETS) type.
     */
    TRADING_ACCOUNT(
        RegistryAccountType.TRADING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - Trading account"),

    /**
     * The UK Auction delivery account type.
     */
    UK_AUCTION_DELIVERY_ACCOUNT(
        RegistryAccountType.UK_AUCTION_DELIVERY_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK Auction delivery account"),

    /**
     * The Person holding account (KP) type.
     */
    PERSON_HOLDING_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.PERSON_HOLDING_ACCOUNT,
        "KP - Person holding account",
        true),

    /**
     * Party holding account (KP).
     */
    PARTY_HOLDING_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "KP - Party holding account",
        true),

    /**
     * The Former operator holding account type.
     */
    FORMER_OPERATOR_HOLDING_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.FORMER_OPERATOR_HOLDING_ACCOUNT,
        "KP - Former operator holding account",
        true),

    /**
     * Previous Period Surplus Reserve account (PPSR account).
     */
    PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT,
        "KP - Previous Period Surplus Reserve account (PPSR)",
        true),

    /**
     * Net source cancellation account.
     */
    NET_SOURCE_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.NET_SOURCE_CANCELLATION_ACCOUNT,
        "KP - Net source cancellation account",
        true),

    /**
     * Non-compliance cancellation account.
     */
    NON_COMPLIANCE_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT,
        "KP - Non-compliance cancellation account",
        true),

    /**
     * UK Surrender Account.
     */
    UK_SURRENDER_ACCOUNT(
        RegistryAccountType.UK_SURRENDER_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK Surrender Account"),

    /**
     * UK Total Quantity Account.
     */
    UK_TOTAL_QUANTITY_ACCOUNT(
        RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK Total Quantity Account"),

    /**
     * UK Aviation Total Quantity Account.
     */
    UK_AVIATION_TOTAL_QUANTITY_ACCOUNT(
        RegistryAccountType.UK_AVIATION_TOTAL_QUANTITY_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK Aviation Total Quantity Account"),

    /**
     * UK Auction Account.
     */
    UK_AUCTION_ACCOUNT(
        RegistryAccountType.UK_AUCTION_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK Auction Account"),

    /**
     * UK Allocation Account.
     */
    UK_ALLOCATION_ACCOUNT(
        RegistryAccountType.UK_ALLOCATION_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK Allocation Account"),

    /**
     * UK New Entrants Reserve Account.
     */
    UK_NEW_ENTRANTS_RESERVE_ACCOUNT(
        RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK New Entrants Reserve Account"),

    /**
     * UK Market Stability Mechanism Account.
     */
    UK_MARKET_STABILITY_MECHANISM_ACCOUNT(
        RegistryAccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK Market Stability Mechanism Account"),

    /**
     * UK General Holding Account.
     */
    UK_GENERAL_HOLDING_ACCOUNT(
        RegistryAccountType.UK_GENERAL_HOLDING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK General Holding Account"),

    /**
     * UK Deletion Account.
     */
    UK_DELETION_ACCOUNT(
        RegistryAccountType.UK_DELETION_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - UK Deletion Account"),

    /**
     * National holding account.
     */
    NATIONAL_HOLDING_ACCOUNT(
        RegistryAccountType.NATIONAL_HOLDING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - National holding account"),

    /**
     * The retirement account.
     */
    RETIREMENT_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.RETIREMENT_ACCOUNT,
        "KP - Retirement account",
        true),

    /**
     * The voluntary cancellation account.
     */
    VOLUNTARY_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.VOLUNTARY_CANCELLATION_ACCOUNT,
        "KP - Voluntary cancellation account",
        true),

    /**
     * The mandatory cancellation account.
     */
    MANDATORY_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.MANDATORY_CANCELLATION_ACCOUNT,
        "KP - Mandatory cancellation account",
        true),

    /**
     * The Art37Ter cancellation account.
     */
    ARTICLE_3_7_TER_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT,
        "KP - Art37ter cancellation account",
        true),

    /**
     * The ambition increase cancellation account.
     */
    AMBITION_INCREASE_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT,
        "KP - Ambition increase cancellation account",
        true),

    TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY(
        RegistryAccountType.NONE,
        KyotoAccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY,
        "KP - tCER Replacement account for expiry",
        true,
        Collections.singletonList(UnitType.TCER)),

    LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY(
        RegistryAccountType.NONE,
        KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY,
        "KP - lCER Replacement account for expiry",
        true,
        Collections.singletonList(UnitType.LCER)),

    LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE(
        RegistryAccountType.NONE,
        KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE,
        "KP - lCER Replacement account for reversal of storage",
        true),

    LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT(
        RegistryAccountType.NONE,
        KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT,
        "KP - lCER Replacement account for non-submission of certification report",
        true),

    /**
     * Excess issuance cancellation account.
     */
    EXCESS_ISSUANCE_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.EXCESS_ISSUANCE_CANCELLATION_ACCOUNT,
        "KP - Excess issuance cancellation account",
        true),

    /**
     * CCS net reversal cancellation account.
     */
    CCS_NET_REVERSAL_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.CCS_NET_REVERSAL_CANCELLATION_ACCOUNT,
        "KP - CCS net reversal cancellation account",
        true),

    /**
     * CCS non-submission of verification report cancellation account.
     */
    CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT,
        "KP - CCS non-submission of verification report cancellation account",
        true),

    /**
     * Pending account.
     */
    PENDING_ACCOUNT(
        RegistryAccountType.NONE,
        KyotoAccountType.PENDING_ACCOUNT,
        "KP - Pending Account",
        true),

    /**
     * AAU deposit account.
     */
    AAU_DEPOSIT(
        RegistryAccountType.AAU_DEPOSIT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "ETS - AAU Deposit Account"
    );

    /**
     * The Kyoto account type.
     */
    private KyotoAccountType kyotoType;

    /**
     * The registry account type.
     */
    private RegistryAccountType registryType;

    /**
     * Whether this is a Kyoto account type.
     */
    private Boolean kyoto;

    /**
     * The registry code.
     */
    private String registryCode;

    /**
     * The label.
     */
    private String label;

    /**
     * The unit types allowed to be held by this account.
     */
    private List<UnitType> unitTypes;

    /**
     * Constructor.
     *
     * @param registryType The registry type.
     * @param kyotoType    The Kyoto type.
     * @param label        The label.
     */
    AccountType(RegistryAccountType registryType, KyotoAccountType kyotoType, String label) {
        this(registryType, kyotoType, label, false);
    }

    /**
     * Constructor.
     *
     * @param registryType The registry type.
     * @param kyotoType    The kyoto type.
     * @param label        The label.
     * @param kyoto        Whether this is a Kyoto transaction type.
     */
    AccountType(RegistryAccountType registryType, KyotoAccountType kyotoType, String label, Boolean kyoto) {
        this(registryType, kyotoType, label, kyoto, null);
    }

    /**
     * Constructor.
     *
     * @param registryType The registry type.
     * @param kyotoType    The kyoto type.
     * @param label        The label.
     * @param kyoto        Whether this is a Kyoto transaction type.
     * @param unitTypes    The unit types allowed to be held by this account.
     */
    AccountType(RegistryAccountType registryType, KyotoAccountType kyotoType, String label, Boolean kyoto,
                List<UnitType> unitTypes) {
        this.kyotoType = kyotoType;
        this.registryType = registryType;
        this.kyoto = kyoto;
        this.registryCode = Constants.REGISTRY_CODE;
        if (Boolean.TRUE.equals(kyoto)) {
            this.registryCode = Constants.KYOTO_REGISTRY_CODE;
        }
        this.label = label;
        this.unitTypes = unitTypes;
    }

    /**
     * Finds the {@link AccountType} based on accountType label
     *
     * @param label The label.
     * @return an AccountType entry
     */
    public static AccountType get(String label) {
        return Stream.of(AccountType.values())
                .filter(accountType -> accountType.label.equals(label))
                .findFirst().orElse(null);
    }

    /**
     * Finds the {@link AccountType} which is composed by the passed registryAccountType
     * and kyotoAccountType.
     *
     * @param registryAccountType The {@link KyotoAccountType}
     * @param kyotoAccountType    The {@link RegistryAccountType}
     * @return an AccountType entry
     */
    public static AccountType get(RegistryAccountType registryAccountType,
                                  KyotoAccountType kyotoAccountType) {
        return Stream.of(AccountType.values())
            .filter(accountType -> accountType.registryType == registryAccountType
                && accountType.kyotoType == kyotoAccountType)
            .findFirst().orElse(null);
    }

    /**
     * Returns the Kyoto government types.
     *
     * @return the Kyoto government types.
     */
    public static List<AccountType> getAllKyotoGovernmentTypes() {
        return Stream.of(AccountType.values())
            .filter(accountType -> accountType.kyotoType.isGovernment() && accountType.kyoto).collect(
                Collectors.toList());
    }

    /**
     * Returns the registry government types.
     *
     * @return the registry government types.
     */
    public static List<AccountType> getAllRegistryGovernmentTypes() {
        return Stream.of(AccountType.values())
            .filter(accountType -> accountType.registryType.isGovernment() && !accountType.kyoto)
            .collect(Collectors.toList());
    }

    /**
     * Parses the provided string to an account type.
     *
     * @param input The input string.
     * @return an account type, or null if no matching input is found.
     */
    public static AccountType parse(String input) {
        try {
            return AccountType.valueOf(input);
        } catch (IllegalArgumentException | NullPointerException exc) {
            return null;
        }
    }

    /**
     * Returns all values which refer to the provided kyoto account type.
     *
     * @param kyotoAccountType the kyoto account type
     * @return some account types
     */
    public static List<AccountType> of(KyotoAccountType... kyotoAccountType) {
        return Stream.of(AccountType.values())
            .filter(accountType -> Arrays.asList(kyotoAccountType).contains(accountType.kyotoType))
            .collect(Collectors.toList());
    }

    public static List<AccountType> getCentralTypes() {
        return List.of(AccountType.UK_TOTAL_QUANTITY_ACCOUNT,
            AccountType.UK_AUCTION_ACCOUNT,
            AccountType.UK_ALLOCATION_ACCOUNT,
            AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT,
            AccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT,
            AccountType.UK_GENERAL_HOLDING_ACCOUNT);
    }

    public static List<AccountType> getReadOnlyCentralTypes() {
        return List.of(AccountType.UK_SURRENDER_ACCOUNT,
                       AccountType.UK_DELETION_ACCOUNT);
    }
    
	public static List<AccountType> getTypesWithBillingDetails() {
		return List.of(AccountType.PERSON_HOLDING_ACCOUNT, AccountType.TRADING_ACCOUNT);
	};

	public static List<AccountType> getTypesWithSalesContactDetails() {
		return List.of(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT, AccountType.OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
				AccountType.TRADING_ACCOUNT);
	}

    /**
     * Returns the Kyoto code of the account.
     *
     * @return the Kyoto code of the account.
     */
    public Integer getKyotoCode() {
        return kyotoType.getCode();
    }

    /**
     * Returns whether this is a government account.
     *
     * @return false/true
     */
    public boolean isGovernmentAccount() {
        return kyotoType.isGovernment() && RegistryAccountType.NONE.equals(registryType) || registryType.isGovernment();
    }

    public boolean isOHAorAOHA() {
        return AccountType.OPERATOR_HOLDING_ACCOUNT.equals(this) ||
               AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.equals(this);
    }

    /**
     * Returns all ETS account types.
     *
     * @return a list with ETS account types.
     */
    public static List<AccountType> getEtsAccountTypes() {
        return Stream.of(AccountType.values())
            .filter(at -> at.kyoto == null || !at.kyoto)
            .collect(Collectors.toList());
    }
    
    /**
     * Returns all ETS account types besides the central ones.
     *
     * @return a list with ETS account types.
     */    
    public static List<AccountType> getEtsExceptCentralAccountTypes() {
        return Stream.of(AccountType.values())
            .filter(at -> (at.kyoto == null || !at.kyoto) 
            		&& !getCentralTypes().contains(at) && !getReadOnlyCentralTypes().contains(at))
            .collect(Collectors.toList());
    }

    /**
     * Checks if an account type is CP independent, meaning that it shall be created with CP code 0.
     *
     * @param type the account type.
     * @return true if the account type is CP independent, false otherwise.
     */
    public static boolean isCpIndependent(AccountType type) {
        return getEtsAccountTypes().contains(type) || AccountType.PERSON_HOLDING_ACCOUNT.equals(type);
    }
}
