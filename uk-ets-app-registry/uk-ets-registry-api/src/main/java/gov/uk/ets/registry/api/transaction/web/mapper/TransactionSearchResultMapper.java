package gov.uk.ets.registry.api.transaction.web.mapper;

import org.springframework.stereotype.Component;

import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.transaction.domain.AccountProjection;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.web.model.TransactionSearchResult;
import gov.uk.ets.registry.api.transaction.web.model.TransactionSearchResult.AccountInfo;
import gov.uk.ets.registry.api.transaction.web.model.TransactionSearchResult.UnitsInfo;
import lombok.RequiredArgsConstructor;

/**
 * Mapper which is responsible for mapping a {@link TransactionProjection} to a {@link
 * TransactionSearchResult}
 */
@Component
@RequiredArgsConstructor
public class TransactionSearchResultMapper {

    private final AccountAccessService accountAccessService;

    /**
     * Maps a {@link TransactionProjection} to a {@link TransactionSearchResult}.
     * @param projection The {@link TransactionProjection}
     * @return {@link TransactionSearchResult}
     */
    public TransactionSearchResult map(TransactionProjection projection) {
        return TransactionSearchResult.builder()
        .id(projection.getIdentifier())
        .type(getEnumMemberName(projection.getType()))
        .transferringAccount(mapToAccountInfo(projection.getTransferringAccount()))
        .acquiringAccount(mapToAccountInfo(projection.getAcquiringAccount()))
        .units(UnitsInfo.builder()
            .quantity(projection.getQuantity())
            .type(getEnumMemberName(projection.getUnitType()))
            .build())
        .status(getEnumMemberName(projection.getStatus()))
        .lastUpdated(projection.getLastUpdated())
        .runningBalance(UnitsInfo.builder()
            .quantity(projection.getRunningBalanceQuantity())
            .type(getEnumMemberName(projection.getRunningBalanceUnitType()))
            .build())
        .reversedByIdentifier(projection.getReversedBy())
        .reversesIdentifier(projection.getReverses())
        .build();
    }

  private <E extends Enum<E>> String getEnumMemberName(E member) {
    return member != null ? member.name() : null;
  }

  private AccountInfo mapToAccountInfo(AccountProjection account) {
    return AccountInfo.builder()
        .title(account.isGovernmentAccount() && !account.isExternalAccount() ? account.getUkRegistryAccountName()
            : account.getAccountFullIdentifier())
        .ukRegistryIdentifier(account.getUkRegistryAccountIdentifier())
        .userHasAccess(accountAccessService.checkAccountAccess(account.getUkRegistryAccountIdentifier()))
        .ukRegistryFullIdentifier(account.getAccountFullIdentifier())
        .isExternalAccount(account.isExternalAccount())
        .accountStatus(account.getAccountStatus())
        .build();
  }
}
