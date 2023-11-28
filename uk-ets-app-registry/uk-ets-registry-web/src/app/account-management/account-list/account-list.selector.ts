import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  accountListFeatureKey,
  AccountListState,
} from './account-list.reducer';

const selectAccountListState = createFeatureSelector<AccountListState>(
  accountListFeatureKey
);

export const selectSearchState = createSelector(
  selectAccountListState,
  (state) => state
);

export const areCriteriaExternal = createSelector(
  selectAccountListState,
  (state) => state.externalCriteria
);

export const selectPagination = createSelector(
  selectAccountListState,
  (state) => state.pagination
);

export const selectPageParameters = createSelector(
  selectAccountListState,
  (state) => state.pageParameters
);

export const selectSortParameters = createSelector(
  selectAccountListState,
  (state) => state.sortParameters
);

export const selectResults = createSelector(
  selectAccountListState,
  (state) => state.results
);

export const selectHideCriteria = createSelector(
  selectAccountListState,
  (state) => state.hideCriteria
);

export const selectShowAdvancedSearch = createSelector(
  selectAccountListState,
  (state) => state.showAdvancedSearch
);

export const selectResultsLoaded = createSelector(
  selectAccountListState,
  (state) => state.resultsLoaded
);

export const selectCriteria = createSelector(
  selectAccountListState,
  (state) => state.criteria
);
