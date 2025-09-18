import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  AllocationJobStatusState,
  allocationJobStatusFeatureKey,
} from './allocation-job-status.reducer';

const selectAllocationJobStatusState =
  createFeatureSelector<AllocationJobStatusState>(
    allocationJobStatusFeatureKey
  );

export const selectSearchState = createSelector(
  selectAllocationJobStatusState,
  (state) => state
);

export const areCriteriaExternal = createSelector(
  selectAllocationJobStatusState,
  (state) => state.externalCriteria
);

export const selectPagination = createSelector(
  selectAllocationJobStatusState,
  (state) => state.pagination
);

export const selectPageParameters = createSelector(
  selectAllocationJobStatusState,
  (state) => state.pageParameters
);

export const selectSortParameters = createSelector(
  selectAllocationJobStatusState,
  (state) => state.sortParameters
);

export const selectResults = createSelector(
  selectAllocationJobStatusState,
  (state) => state.results
);

export const selectHideCriteria = createSelector(
  selectAllocationJobStatusState,
  (state) => state.hideCriteria
);

export const selectShowAdvancedSearch = createSelector(
  selectAllocationJobStatusState,
  (state) => state.showAdvancedSearch
);

export const selectResultsLoaded = createSelector(
  selectAllocationJobStatusState,
  (state) => state.resultsLoaded
);

export const selectCriteria = createSelector(
  selectAllocationJobStatusState,
  (state) => state.criteria
);
