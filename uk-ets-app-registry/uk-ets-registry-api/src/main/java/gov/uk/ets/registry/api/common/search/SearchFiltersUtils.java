package gov.uk.ets.registry.api.common.search;

import gov.uk.ets.registry.api.account.web.model.search.AccountTypeOption;
import gov.uk.ets.registry.api.account.web.model.search.TransactionTypeOption;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class with help methods related to search filters.
 */
public class SearchFiltersUtils {
    public static final String ALL_KP_GOVERNMENT_ACCOUNTS = "ALL_KP_GOVERNMENT_ACCOUNTS";
    public static final String ALL_ETS_GOVERNMENT_ACCOUNTS = "ALL_ETS_GOVERNMENT_ACCOUNTS";

    /**
     * Constructor.
     */
    private SearchFiltersUtils() {
        // nothing to implement here
    }

    /***
     * Creates and returns a list of {@link AccountTypeOption} options.
     * @param adminFilters The flag that indicates if the account type options are returned to an administrator or not
     * @param isAuthorityUser The flag that indicates if the account type options are returned to an authority user
     * @return The List of account type options
     */
    public static List<AccountTypeOption> getAccountTypeOptions(boolean adminFilters, boolean isAuthorityUser) {
        List<AccountTypeOption> accountTypeOptions = Stream.of(
            AccountType.OPERATOR_HOLDING_ACCOUNT,
            AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
            AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
            AccountType.TRADING_ACCOUNT,
            AccountType.UK_AUCTION_DELIVERY_ACCOUNT,
            AccountType.PERSON_HOLDING_ACCOUNT,
            AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT
        ).sorted(Comparator.comparing(AccountType::getKyoto).thenComparing(AccountType::getLabel))
            .map(AccountTypeOption::of).collect(Collectors.toList());

        if (adminFilters) {
            accountTypeOptions.addAll(List.of(
                AccountTypeOption
                    .builder()
                    .label("All KP government accounts")
                    .value(ALL_KP_GOVERNMENT_ACCOUNTS).build(),
                AccountTypeOption
                    .builder()
                    .label("All ETS government accounts")
                    .value(ALL_ETS_GOVERNMENT_ACCOUNTS).build()));
        }
        if (isAuthorityUser) {
            accountTypeOptions.clear();
        }
        return accountTypeOptions;
    }

    /**
     * Maps the option String to a list of {@link AccountType} objects
     *
     * @param option The String that is going to be mapped to the account type list
     * @return The account type list
     */
    public static List<AccountType> mapToAccountTypes(String option) {
        if (option == null) {
            return null;
        }
        if (option.equals(ALL_KP_GOVERNMENT_ACCOUNTS)) {
            return AccountType.getAllKyotoGovernmentTypes();
        }
        if (option.equals(ALL_ETS_GOVERNMENT_ACCOUNTS)) {
            return AccountType.getAllRegistryGovernmentTypes();
        }
        return List.of(mapToEnum(AccountType.values(), option));
    }

    /**
     * Maps a String to an Enum member of the passed enum values
     *
     * @param values The enumeration members
     * @param name   The String which should be matched with the name of one of the enumeration members
     * @param <E>    The enumeration member type
     * @return The matched enumeration member
     * @throws IllegalArgumentException if the values argument is null or empty
     * @throws IllegalStateException    if no one of the enumeration members name matches with the name passed argument
     */
    public static <E extends Enum<E>> E mapToEnum(E[] values, String name) {
        if (name == null) {
            return null;
        }
        Optional.ofNullable(values).orElseThrow(() -> new IllegalArgumentException("values should not be null"));
        if (values.length == 0) {
            throw new IllegalArgumentException("There are no Enumeration members for matching the passed name");
        }
        return Stream.of(values).filter(value -> value.name().equals(name)).findAny().orElseThrow(() ->
            new IllegalStateException(
                "The " + name + " does not correspond to any enum of type of " + values[0].getClass()));
    }


    /***
     * Creates and returns a list of {@link TransactionTypeOption} options.
     * @param isAdmin The flag that indicates if the account type options are returned to an administrator or not
     * @param isAuthorityUser The flag that indicates if the account type options are returned to an authority user
     * @return The List of transaction type options
     */
    public static List<TransactionTypeOption> getTransactionTypeOptions(boolean isAdmin, boolean isAuthorityUser) {
        final List<TransactionTypeOption> transactionTypeOptions = new ArrayList<>();

        if (isAdmin) {
            TransactionType.transactionTypesAvailableToAdmin().forEach(type -> transactionTypeOptions.add(
                TransactionTypeOption.builder().label(type.getDescription()).value(type.name()).build()));
        } else if (isAuthorityUser) {
            TransactionType.transactionTypesAvailableToAuthority().forEach(type -> transactionTypeOptions.add(
                TransactionTypeOption.builder().label(type.getDescription()).value(type.name()).build()));
        } else {
            TransactionType.transactionTypesAvailableToAR().forEach(type -> transactionTypeOptions.add(
                TransactionTypeOption.builder().label(type.getDescription()).value(type.name()).build()));
        }
        return transactionTypeOptions;
    }
}
