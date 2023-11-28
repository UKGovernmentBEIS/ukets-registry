import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  transactionListFeatureKey,
  TransactionListState,
} from './transaction-list.reducer';

const selectTransactionListState = createFeatureSelector<TransactionListState>(
  transactionListFeatureKey
);

export const selectSearchState = createSelector(
  selectTransactionListState,
  (state) => state
);

export const areCriteriaExternal = createSelector(
  selectTransactionListState,
  (state) => state.externalCriteria
);

export const selectPagination = createSelector(
  selectTransactionListState,
  (state) => state.pagination
);

export const selectPageParameters = createSelector(
  selectTransactionListState,
  (state) => state.pageParameters
);

export const selectSortParameters = createSelector(
  selectTransactionListState,
  (state) => state.sortParameters
);

export const selectResults = createSelector(
  selectTransactionListState,
  (state) => state.results
);

export const selectHideCriteria = createSelector(
  selectTransactionListState,
  (state) => state.hideCriteria
);

export const selectShowAdvancedSearch = createSelector(
  selectTransactionListState,
  (state) => state.showAdvancedSearch
);

export const selectResultsLoaded = createSelector(
  selectTransactionListState,
  (state) => state.resultsLoaded
);

export const selectCriteria = createSelector(
  selectTransactionListState,
  (state) => state.criteria
);
