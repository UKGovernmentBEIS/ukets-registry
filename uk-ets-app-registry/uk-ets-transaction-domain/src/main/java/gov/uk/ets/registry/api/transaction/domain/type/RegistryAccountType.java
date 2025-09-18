package gov.uk.ets.registry.api.transaction.domain.type;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumerates the various registry account types.
 */
public enum RegistryAccountType {

    /**
     * The UK total quantity account.
     */
    UK_TOTAL_QUANTITY_ACCOUNT(true),

    /**
     * The UK aviation total quantity account.
     */
    UK_AVIATION_TOTAL_QUANTITY_ACCOUNT,

    /**
     * The UK auction account.
     */
    UK_AUCTION_ACCOUNT(true),

    /**
     * The UK allocation account.
     */
    UK_ALLOCATION_ACCOUNT(true),

    /**
     * The UK new entrants reserve account.
     */
    UK_NEW_ENTRANTS_RESERVE_ACCOUNT(true),

    /**
     * The UK aviation allocation account.
     */
    UK_AVIATION_ALLOCATION_ACCOUNT(true),

    /**
     * The UK deletion account.
     */
    UK_DELETION_ACCOUNT(true),

    /**
     * The UK surrender account.
     */
    UK_SURRENDER_ACCOUNT(true),

    /**
     * The UK supply adjustment mechanism account.
     */
    UK_MARKET_STABILITY_MECHANISM_ACCOUNT(true),

    /**
     * The UK general holding account.
     */
    UK_GENERAL_HOLDING_ACCOUNT(true),

    /**
     * The UK auction delivery account.
     */
    UK_AUCTION_DELIVERY_ACCOUNT,

    /**
     * The operator holding account.
     */
    OPERATOR_HOLDING_ACCOUNT,

    /**
     * The aircraft operator holding account.
     */
    AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,

    /**
     * The maritime operator holding account.
     */
    MARITIME_OPERATOR_HOLDING_ACCOUNT,

    /**
     * The national holding account.
     */
    NATIONAL_HOLDING_ACCOUNT(true),

    /**
     * The trading account.
     */
    TRADING_ACCOUNT,

    /**
     * The aau deposit account.
     */
    AAU_DEPOSIT,

    /**
     * None; used for pure Kyoto accounts.
     */
    NONE;

    /**
     * Whether this type refers to government accounts.
     */
    private Boolean government = false;

    /**
     * Constructor.
     */
    RegistryAccountType() {
        // nothing to implement here
    }

    /**
     * Constructor.
     *
     * @param government Whether this type refers to government accounts.
     */
    RegistryAccountType(Boolean government) {
        this.government = government;
    }

    public Boolean isGovernment() {
        return government;
    }

    /**
     * Parses the input string to an enumeration value.
     *
     * @param input The input string
     * @return an enumeration value or null if the input does not correspond to a value
     */
    public static RegistryAccountType parse(String input) {
        RegistryAccountType result;
        try {
            result = RegistryAccountType.valueOf(input);
        } catch (Exception exc) {
            // nothing to log here
            result = null;
        }
        return result;
    }

    public static List<RegistryAccountType> getETSRegistryAccountTypes() {
        return Stream.of(RegistryAccountType.values())
            .filter(registryAccountType -> !registryAccountType.equals(NONE))
            .collect(Collectors.toList());
    }

}
