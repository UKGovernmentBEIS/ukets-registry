package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.search.SearchFiltersUtils;
import gov.uk.ets.registry.api.task.shared.EndUserSearch;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.shared.TransactionSearchCriteria;
import gov.uk.ets.registry.api.user.service.UserService;
import org.springframework.stereotype.Component;

/**
 * Factory class for creating {@link TransactionFilter} value object
 */
@Component
public class TransactionFilterFactory {
    private AuthorizationService authorizationService;
    private UserService userService;

    public TransactionFilterFactory(
        AuthorizationService authorizationService,
        UserService userService) {
        this.authorizationService = authorizationService;
        this.userService = userService;
    }

    /**
     * Creates a {@link TransactionFilter} value object
     *
     * @param criteria The {@link TransactionSearchCriteria} criteria
     * @return The produced {@link TransactionFilter} value object
     */
    public TransactionFilter createTransactionFilter(TransactionSearchCriteria criteria) {
        EndUserSearch endUserSearch = new EndUserSearch();
        String authorizedRepresentativeUrid = null;
        boolean enrolledNonAdmin = authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN);

        if (enrolledNonAdmin) {
            authorizedRepresentativeUrid = userService.getCurrentUser().getUrid();
        } else if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            endUserSearch.setAdminSearch(Boolean.TRUE);
            endUserSearch.setIamIdentifier(authorizationService.getClaim(OAuth2ClaimNames.SUBJECT));
        }

        return TransactionFilter.builder()
            .transactionId(criteria.getTransactionId())
            .transactionType(
                SearchFiltersUtils.mapToEnum(TransactionType.values(), criteria.getTransactionType()))
            .transactionStatus(SearchFiltersUtils
                .mapToEnum(TransactionStatus.values(), criteria.getTransactionStatus()))
            .transactionLastUpdateDateFrom(criteria.getTransactionLastUpdateDateFrom())
            .transactionLastUpdateDateTo(criteria.getTransactionLastUpdateDateTo())
            .transferringAccountNumber(criteria.getTransferringAccountNumber())
            .acquiringAccountNumber(criteria.getAcquiringAccountNumber())
            .acquiringAccountTypes(
                SearchFiltersUtils.mapToAccountTypes(criteria.getAcquiringAccountType()))
            .transferringAccountTypes(
                SearchFiltersUtils.mapToAccountTypes(criteria.getTransferringAccountType()))
            .unitType(SearchFiltersUtils.mapToEnum(UnitType.values(), criteria.getUnitType()))
            .initiatorUserId(criteria.getInitiatorUserId())
            .approverUserId(criteria.getApproverUserId())
            .transactionalProposalDateFrom(criteria.getTransactionalProposalDateFrom())
            .transactionalProposalDateTo(criteria.getTransactionalProposalDateTo())
            .authorizedRepresentativeUrid(authorizedRepresentativeUrid)
            .enrolledNonAdmin(enrolledNonAdmin)
            .endUserSearch(endUserSearch)
            .acquiringAccountTypeOption(criteria.getAcquiringAccountType())
            .transferringAccountTypeOption(criteria.getTransferringAccountType())
            .showRunningBalances(criteria.isShowRunningBalances())
            .reversed(criteria.getReversed())
            .build();
    }
}
