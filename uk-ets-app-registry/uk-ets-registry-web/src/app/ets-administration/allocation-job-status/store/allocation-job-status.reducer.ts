import { Action, createReducer, on } from '@ngrx/store';
import * as AllocationJobStatusActions from './allocation-job-status.actions';
import { mutableOn } from '@registry-web/shared/mutable-on';
import { AllocationJob } from '../models/allocation-job.model';
import { AllocationJobSearchCriteria } from '../models/allocation-job-search-criteria.model';
import { PagedResults } from '@registry-web/shared/search/util/search-service.util';
import {
  PageParameters,
  Pagination,
} from '@registry-web/shared/search/paginator';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';

export const allocationJobStatusFeatureKey = 'allocationJobStatus';

const initialState = {
  externalCriteria: undefined,
  pageParameters: {
    page: 0,
    pageSize: 10,
  },
  pagination: undefined,
  sortParameters: {
    sortField: 'id',
    sortDirection: 'DESC',
  },
  results: undefined,
  criteria: {
    id: undefined,
    requestIdentifier: undefined,
    executionDateTo: undefined,
    executionDateFrom: undefined,
    allocationStatus: undefined,
  },
  hideCriteria: false,
  showAdvancedSearch: false,
  hideTable: true,
  resultsLoaded: false,
};

export interface AllocationJobStatusState {
  externalCriteria: boolean;
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: AllocationJob[];
  criteria: AllocationJobSearchCriteria;
  hideCriteria: boolean;
  showAdvancedSearch: boolean;
  hideTable: boolean;
  resultsLoaded: boolean;
}

const allocationJobStatusReducer = createReducer(
  initialState,
  mutableOn(
    AllocationJobStatusActions.allocationJobsLoaded,
    (state, { results, pagination, sortParameters, criteria }) => {
      state.criteria = criteria;
      state.pagination = pagination;
      state.results = results;
      state.hideTable = false;
      state.resultsLoaded = true;
      state.pageParameters = {
        page: pagination.currentPage - 1,
        pageSize: pagination.pageSize, // The page size that user selected
      };
      state.sortParameters = sortParameters;
    }
  ),
  mutableOn(AllocationJobStatusActions.toggleCriteria, (state) => {
    state.hideCriteria = !state.hideCriteria;
  }),
  on(
    AllocationJobStatusActions.clearState,
    (state): AllocationJobStatusState => initialState
  )
);

export function reducer(
  state: AllocationJobStatusState | undefined,
  action: Action
) {
  return allocationJobStatusReducer(state, action);
}
