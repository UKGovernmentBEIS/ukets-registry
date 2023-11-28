import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  accountFeatureKey,
  AccountState,
} from '@account-management/account/account-details/account.reducer';

const selectAccountState =
  createFeatureSelector<AccountState>(accountFeatureKey);

export const selectTrustedAccountPagination = createSelector(
  selectAccountState,
  (state) => state.account.trustedAccountList?.pagination
);

export const selectTrustedAccountPageParameters = createSelector(
  selectAccountState,
  (state) => state.account.trustedAccountList?.pageParameters
);

export const selectTrustedAccountShowHideCriteria = createSelector(
  selectAccountState,
  (state) => state.account.trustedAccountList?.hideCriteria
);

export const selectTrustedAccountCriteria = createSelector(
  selectAccountState,
  (state) => state.account.trustedAccountList?.criteria
);

export const selectTrustedAccountSortParameters = createSelector(
  selectAccountState,
  (state) => state.account.trustedAccountList?.sortParameters
);
