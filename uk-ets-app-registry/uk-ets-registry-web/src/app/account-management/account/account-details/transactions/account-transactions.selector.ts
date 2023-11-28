import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  accountFeatureKey,
  AccountState
} from '@account-management/account/account-details/account.reducer';

const selectAccountState = createFeatureSelector<AccountState>(
  accountFeatureKey
);

export const selectAccountTransactions = createSelector(
  selectAccountState,
  state => state.transactions
);
export const selectTransactionPagination = createSelector(
  selectAccountState,
  state => state.transactionPagination
);
