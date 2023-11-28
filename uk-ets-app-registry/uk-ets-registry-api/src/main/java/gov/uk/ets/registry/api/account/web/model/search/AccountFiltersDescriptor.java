package gov.uk.ets.registry.api.account.web.model.search;

import gov.uk.ets.registry.api.account.web.mappers.AccountFilterMapper;
import gov.uk.ets.registry.api.common.search.SearchFiltersUtils;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public class AccountFiltersDescriptor {
    /**
     * Builds a filtering description.
     * @param adminFilters The flag that indicates if the descriptor must be built for administrators
     * @param isAuthorityUser The flag that indicates if the descriptor must be built for the authority user
     * @return the filter descriptor
     */
    public static AccountFiltersDescriptor build(boolean adminFilters, boolean isAuthorityUser) {
        AccountFiltersDescriptor filtersDescriptor = new AccountFiltersDescriptor();
        filtersDescriptor.accountTypeOptions = SearchFiltersUtils.getAccountTypeOptions(adminFilters, isAuthorityUser);
        filtersDescriptor.accountStatusOptions = getAccountStatusOptions(adminFilters);
        return filtersDescriptor;
    }

    private static List<String> getAccountStatusOptions(boolean adminFilters) {
        if (adminFilters) {
            List<String> accountStatusOptions = Stream.of(AccountStatus.values())
                .filter(status -> !AccountStatus.REJECTED.equals(status))
                    .map(AccountStatus::name)
                    .collect(Collectors.toList());
            accountStatusOptions.add(AccountFilterMapper.ALL_EXCEPT_CLOSED);
            return accountStatusOptions;
        } else {
            return Stream.of(AccountStatus.values())
                    .filter(status -> !List.of(AccountStatus.CLOSED, 
                    		AccountStatus.SUSPENDED,
                            AccountStatus.TRANSFER_PENDING,
                            AccountStatus.PROPOSED,
                            AccountStatus.REJECTED).contains(status))
                    .map(AccountStatus::name)
                    .collect(Collectors.toList());
        }
    }

    private List<AccountTypeOption> accountTypeOptions = new ArrayList<>();
    private List<String> accountStatusOptions = new ArrayList<>();
}
