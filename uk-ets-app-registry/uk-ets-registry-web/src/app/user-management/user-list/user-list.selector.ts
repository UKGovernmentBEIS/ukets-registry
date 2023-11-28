import { createFeatureSelector, createSelector } from '@ngrx/store';
import { userListFeatureKey, UserListState } from './user-list.reducer';

const selectUserListState =
  createFeatureSelector<UserListState>(userListFeatureKey);

export const selectSearchState = createSelector(
  selectUserListState,
  (state) => state
);

export const selectPagination = createSelector(
  selectUserListState,
  (state) => state.pagination
);

export const selectPageParameters = createSelector(
  selectUserListState,
  (state) => state.pageParameters
);

export const selectResults = createSelector(
  selectUserListState,
  (state) => state.results
);

export const selectHideCriteria = createSelector(
  selectUserListState,
  (state) => state.hideCriteria
);

export const selectResultsLoaded = createSelector(
  selectUserListState,
  (state) => state.resultsLoaded
);

export const selectCriteria = createSelector(
  selectUserListState,
  (state) => state.criteria
);

export const selectSortParameters = createSelector(
  selectUserListState,
  (state) => state.sortParameters
);
