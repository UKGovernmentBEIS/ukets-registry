package uk.gov.ets.transaction.log.domain.type;

import java.util.Arrays;
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
        "Operator holding account"),

    /**
     * The Aircraft operator holding account (ETS) type.
     */
    AIRCRAFT_OPERATOR_HOLDING_ACCOUNT(
        RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "Aircraft operator holding account"),

    /**
     * The Trading account (ETS) type.
     */
    TRADING_ACCOUNT(
        RegistryAccountType.TRADING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "Trading account"),

    /**
     * The UK Auction delivery account type.
     */
    UK_AUCTION_DELIVERY_ACCOUNT(
        RegistryAccountType.UK_AUCTION_DELIVERY_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK Auction delivery account"),

    /**
     * UK Surrender Account.
     */
    UK_SURRENDER_ACCOUNT(
        RegistryAccountType.UK_SURRENDER_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK Surrender Account"),    
    
    /**
     * UK Total Quantity Account.
     */
    UK_TOTAL_QUANTITY_ACCOUNT(
        RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK Total Quantity Account"),

    /**
     * UK Aviation Total Quantity Account.
     */
    UK_AVIATION_TOTAL_QUANTITY_ACCOUNT(
        RegistryAccountType.UK_AVIATION_TOTAL_QUANTITY_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK Aviation Total Quantity Account"),

    /**
     * UK Auction Account.
     */
    UK_AUCTION_ACCOUNT(
        RegistryAccountType.UK_AUCTION_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK Auction Account"),

    /**
     * UK Allocation Account.
     */
    UK_ALLOCATION_ACCOUNT(
        RegistryAccountType.UK_ALLOCATION_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK Allocation Account"),

    /**
     * UK New Entrants Reserve Account.
     */
    UK_NEW_ENTRANTS_RESERVE_ACCOUNT(
        RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK New Entrants Reserve Account"),

    /**
     * UK Market Stability Mechanism Account.
     */
    UK_MARKET_STABILITY_MECHANISM_ACCOUNT(
        RegistryAccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK Market Stability Reserve Account"),

    /**
     * UK General Holding Account.
     */
    UK_GENERAL_HOLDING_ACCOUNT(
        RegistryAccountType.UK_GENERAL_HOLDING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "UK General Holding Account"),

    /**
     * National holding account.
     */
    NATIONAL_HOLDING_ACCOUNT(
        RegistryAccountType.NATIONAL_HOLDING_ACCOUNT,
        KyotoAccountType.PARTY_HOLDING_ACCOUNT,
        "National holding account");

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
     * Constructor.
     *
     * @param registryType The registry type.
     * @param kyotoType    The Kyoto type.
     */
    AccountType(RegistryAccountType registryType, KyotoAccountType kyotoType, String label) {
        this(registryType, kyotoType, label, false);
    }

    /**
     * Constructor.
     *
     * @param registryType The registry type.
     * @param kyotoType    The kyoto type.
     * @param kyoto        Whether this is a Kyoto type.
     */
    AccountType(RegistryAccountType registryType, KyotoAccountType kyotoType, String label, Boolean kyoto) {
        this.kyotoType = kyotoType;
        this.registryType = registryType;
        this.kyoto = kyoto;
        this.registryCode = Constants.REGISTRY_CODE;
        if (Boolean.TRUE.equals(kyoto)) {
            this.registryCode = Constants.KYOTO_REGISTRY_CODE;
        }
        this.label = label;
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

    /**
     * Returns whether the account name should be displayed or not. The Account name should be displayed
     * instead of number, if the account is a national or central account.
     *
     * @return true or false
     */
    public boolean isEndUserAccount() {

        if (this.registryType == null) {
            return false;
        }

        return this == AccountType.UK_AUCTION_DELIVERY_ACCOUNT ||
            this == AccountType.OPERATOR_HOLDING_ACCOUNT || this == AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ||
            this == AccountType.TRADING_ACCOUNT;
    }
}
