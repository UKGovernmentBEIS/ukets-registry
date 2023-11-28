package gov.uk.ets.registry.api.transaction.web.model;

import gov.uk.ets.registry.api.account.web.model.search.AccountTypeOption;
import gov.uk.ets.registry.api.account.web.model.search.TransactionTypeOption;
import gov.uk.ets.registry.api.common.search.SearchFiltersUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class TransactionFiltersDescriptor {
    /**
     * Builds a filtering description
     *
     * @param adminFilters The flag that indicates if the descriptor must be built for administrators
     * @return the transaction filters descriptor
     */
    public static TransactionFiltersDescriptor build(boolean adminFilters, boolean isAuthorityUser) {
        TransactionFiltersDescriptor filtersDescriptor = new TransactionFiltersDescriptor();
        filtersDescriptor.accountTypeOptions = SearchFiltersUtils.getAccountTypeOptions(adminFilters, isAuthorityUser);
        filtersDescriptor.transactionTypeOptions =
            SearchFiltersUtils.getTransactionTypeOptions(adminFilters, isAuthorityUser);

        return filtersDescriptor;
    }

    private List<AccountTypeOption> accountTypeOptions = new ArrayList<>();
    private List<TransactionTypeOption> transactionTypeOptions = new ArrayList<>();
}
