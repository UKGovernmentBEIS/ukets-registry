package gov.uk.ets.registry.api.account.web.model.search;

import gov.uk.ets.registry.api.account.web.mappers.AccountFilterMapper;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import java.util.Comparator;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.uk.ets.registry.api.common.search.SearchFiltersUtils.ALL_ETS_GOVERNMENT_ACCOUNTS;
import static gov.uk.ets.registry.api.common.search.SearchFiltersUtils.ALL_KP_GOVERNMENT_ACCOUNTS;

@DisplayName("Testing Account filter descriptor test")
class AccountFiltersDescriptorTest {

    @DisplayName("Test account filter build")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1} - {2} - {3}")
    void test_build_account_filter(boolean currentUserIsAdmin, boolean isAuthorityUser,
                                   List<String> expectedStatusOptions,
                                   List<AccountTypeOption> expectedAccountTypeOptions) {

        AccountFiltersDescriptor filtersDescriptor =
            AccountFiltersDescriptor.build(currentUserIsAdmin, isAuthorityUser);

        Assert.assertEquals(expectedStatusOptions.size(), filtersDescriptor.getAccountStatusOptions().size());
        Assert.assertEquals(expectedStatusOptions, filtersDescriptor.getAccountStatusOptions());
        Assert.assertEquals(expectedAccountTypeOptions.size(), filtersDescriptor.getAccountTypeOptions().size());
        Assert.assertEquals(expectedAccountTypeOptions, filtersDescriptor.getAccountTypeOptions());
    }

    static Stream<Arguments> getArguments() {
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

        /* Admin Users */
        List<String> adminFilters = Stream.of(AccountStatus.values())
            .filter(status -> !AccountStatus.REJECTED.equals(status))
            .map(AccountStatus::name)
            .collect(Collectors.toList());
        adminFilters.add(AccountFilterMapper.ALL_EXCEPT_CLOSED);

        List<AccountTypeOption> accountTypeOptionsAdmin = new ArrayList<>(accountTypeOptions);
        accountTypeOptionsAdmin.addAll(List.of(
            AccountTypeOption
                .builder()
                .label("All KP government accounts")
                .value(ALL_KP_GOVERNMENT_ACCOUNTS).build(),
            AccountTypeOption
                .builder()
                .label("All ETS government accounts")
                .value(ALL_ETS_GOVERNMENT_ACCOUNTS).build()));

        /* Non Admin Users */
        List<String> arFilters = Stream.of(AccountStatus.values())
            .filter(status -> !List.of(AccountStatus.CLOSED,
                AccountStatus.SUSPENDED,
                AccountStatus.TRANSFER_PENDING,
                AccountStatus.PROPOSED,
                AccountStatus.REJECTED).contains(status))
            .map(AccountStatus::name)
            .collect(Collectors.toList());
        List<AccountTypeOption> accountTypeOptionsNonAdmin = new ArrayList<>(accountTypeOptions);

        /* Authority Users */
        List<String> authorityFilters = new ArrayList<>(arFilters);
        List<AccountTypeOption> accountTypeOptionsAuthority = Collections.emptyList();

        return Stream.of(
            Arguments.of(true, false, adminFilters, accountTypeOptionsAdmin), // Admin
            Arguments.of(false, false, arFilters, accountTypeOptionsNonAdmin), // AR user
            Arguments.of(false, true, authorityFilters, accountTypeOptionsAuthority) // Authority user
        );
    }
}
