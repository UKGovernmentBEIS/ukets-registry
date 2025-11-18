import { createFeatureSelector, createSelector } from '@ngrx/store';
import { accountFeatureKey, AccountState } from './account.reducer';
import { AccountType } from '@shared/model/account';
import { empty } from '@shared/shared.util';
import { selectState } from '@registry-web/auth/auth.selector';
import { MenuItemEnum } from './model';

const selectAccountState =
  createFeatureSelector<AccountState>(accountFeatureKey);

export const selectAccount = createSelector(
  selectAccountState,
  (state) => state.account
);
export const selectAccountHistory = createSelector(
  selectAccountState,
  (state) => state.accountHistory
);
export const selectHasOperatorUpdatePendingApproval = createSelector(
  selectAccountState,
  (state) => state.hasOperatorUpdatePendingApproval
);
export const selectAccountId = createSelector(
  selectAccountState,
  (state) => state.accountId
);
export const selectAccountStatus = createSelector(
  selectAccountState,
  (state) => state.account.accountDetails.accountStatus
);

export const selectAuthorisedRepresentatives = createSelector(
  selectAccountState,
  (state) => state.account?.authorisedRepresentatives
);

export const selectPendingARRequests = createSelector(
  selectAccountState,
  (state) => state.account?.pendingARRequests
);

export const selectTransactionListPageParameters = createSelector(
  selectAccountState,
  (state) => ({
    page: (state.transactionPagination?.currentPage || 1) - 1,
    pageSize: state.transactionPagination?.pageSize || 10,
  })
);

export const selectAccountHoldings = createSelector(
  selectAccountState,
  (state) => state.accountHoldingsResult
);
export const selectSideMenuItems = createSelector(
  selectAccountState,
  selectState,
  (state, authState) => {
    if (!authState.isAdmin) {
      return state.sideMenuItems.filter(
        (item) => item !== MenuItemEnum.NOTES && item !== MenuItemEnum.CONTACTS
      );
    } else {
      return state.sideMenuItems;
    }
  }
);
export const areAccountDetailsLoaded = createSelector(
  selectAccountState,
  (state) => state.accountDetailsLoaded
);
export const selectAccountAllocation = createSelector(
  selectAccountState,
  (state) => state.allocation
);

export const selectTransactionApprovalRole = createSelector(
  selectAccountState,
  (state) => {
    if (state.account.governmentAccount) {
      switch (state.account.accountType) {
        case AccountType.UK_TOTAL_QUANTITY_ACCOUNT:
        case AccountType.UK_AUCTION_ACCOUNT:
        case AccountType.UK_GENERAL_HOLDING_ACCOUNT:
        case AccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT:
        case AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT:
          return 'user';
        default:
          return 'registry administrator';
      }
    } else {
      return 'authorised representative';
    }
  }
);

export const selectShowReadOnly = createSelector(
  selectAccountState,
  (state) => {
    return (
      AccountType.OPERATOR_HOLDING_ACCOUNT === state.account.accountType ||
      AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ===
        state.account.accountType ||
      AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT ===
        state.account.accountType ||
      AccountType.TRADING_ACCOUNT === state.account.accountType ||
      AccountType.PERSON_HOLDING_ACCOUNT === state.account.accountType
    );
  }
);

export const selectIsOHAOrAOHA = createSelector(selectAccountState, (state) => {
  return (
    AccountType.OPERATOR_HOLDING_ACCOUNT === state.account.accountType ||
    AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT === state.account.accountType
  );
});

export const selectIsOHAOrAOHAorMOHA = createSelector(
  selectAccountState,
  (state) => {
    return (
      AccountType.OPERATOR_HOLDING_ACCOUNT === state.account.accountType ||
      AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ===
        state.account.accountType ||
      AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT ===
        state.account.accountType
    );
  }
);
export const selectIsUKAllocationorSurrenderAccount = createSelector(
  selectAccount,
  (account) => {
    {
      return (
        account.accountType === AccountType.UK_ALLOCATION_ACCOUNT ||
        account.accountType === AccountType.UK_SURRENDER_ACCOUNT
      );
    }
  }
);

export const selectIsKyotoAccountType = createSelector(
  selectAccountState,
  (state) => {
    return state.account.kyotoAccountType;
  }
);

export const selectAccountHolderFiles = createSelector(
  selectAccountState,
  (state) => state.accountHolderFiles
);

export const selectUpdateAccountDetails = createSelector(
  selectAccountState,
  (state) => state.updateAccountDetails
);

export const selectAccountDetailsForEdit = createSelector(
  selectAccountState,
  (state) =>
    state.updateAccountDetails != null
      ? state.updateAccountDetails
      : state.account.accountDetails
);

export const selectAccountDetailsSameBillingAddress = createSelector(
  selectAccountState,
  (state) => state.accountDetailsSameBillingAddress
);

export const selectAccountFullIdentifier = createSelector(
  selectAccountState,
  (state) => state.selectedTrustedAccountFullIdentifier
);

export const selectAccountTrustedAccountDescription = createSelector(
  selectAccountState,
  selectAccountFullIdentifier,
  (state, identifier) => {
    const trustedAccount = state.account.trustedAccountList?.results?.find(
      (item) => item.accountFullIdentifier === identifier
    );
    return trustedAccount?.description;
  }
);

export const selectTrustedAccountDescriptionOrFullIdentifier = createSelector(
  selectAccountState,
  (state) => {
    return (
      state.selectedTrustedAccountDescription ||
      state.selectedTrustedAccountFullIdentifier
    );
  }
);

export const selectAccountDetails = createSelector(
  selectAccountState,
  areAccountDetailsLoaded,
  (state, areAccountDetailsLoaded) =>
    !empty(state.account) ? state.account.accountDetails : null
);

export const selectExcludeBillingRemarksForm = createSelector(
  selectAccountState,
  (state) => state.excludeBillingRemarks
);

export const selectLastYear = createSelector(selectAccountState, (state) =>
  state?.account?.operator?.lastYear
    ? Number(state.account.operator.lastYear)
    : null
);

export const selectTransferringAccountInfoFromAccount = createSelector(
  selectAccountState,
  (state) => ({
    identifier: state.account?.identifier,
    accountHolderName: state.account?.accountDetails?.accountHolderName,
    fullIdentifier: state.account?.accountDetails?.accountNumber,
    accountName: state.account?.accountDetails?.name,
    operator: state.account?.operator,
  })
);

export const selectIsAOHAorMOHAorOHAO = createSelector(
  selectAccountState,
  (state) => {
    return (
      state.account?.accountType ==
        AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT ||
      state.account?.accountType ==
        AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT ||
      state.account?.accountType == AccountType.OPERATOR_HOLDING_ACCOUNT
    );
  }
);
