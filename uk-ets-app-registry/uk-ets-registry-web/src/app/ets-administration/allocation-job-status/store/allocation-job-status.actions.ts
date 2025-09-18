/* eslint-disable ngrx/prefer-inline-action-props */
import { createAction, props } from '@ngrx/store';
import {
  AllocationJob,
  SearchAllocationJobActionPayload,
} from '../models/allocation-job.model';
import { AllocationJobSearchCriteria } from '../models/allocation-job-search-criteria.model';
import {
  PageParameters,
  Pagination,
} from '@registry-web/shared/search/paginator';
import { PagedResults } from '@registry-web/shared/search/util/search-service.util';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';

export const toggleCriteria = createAction(
  '[Allocation Job Status] Toggle criteria'
);

export const loadAllocationJobs = createAction(
  '[Allocation Job Status] Load the allocation jobs and pagination',
  props<SearchAllocationJobActionPayload>()
);

export const searchAllocationJobs = createAction(
  '[Allocation Job Status] Search for results',
  props<SearchAllocationJobActionPayload>()
);

export const navigateToPageOfResults = createAction(
  '[Allocation Job Status] Select the page of results',
  props<SearchAllocationJobActionPayload>()
);

export const replaySearch = createAction(
  '[Allocation Job Status] Replay the search by using the stored criteria, page number, page size (LOAD mode)',
  props<SearchAllocationJobActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  '[Allocation Job Status] Select the next page of results',
  props<SearchAllocationJobActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  '[Allocation Job Status] Select the previous page of results',
  props<SearchAllocationJobActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  '[Allocation Job Status] Select the last page of results',
  props<SearchAllocationJobActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  '[Allocation Job Status] Select the first page of results',
  props<SearchAllocationJobActionPayload>()
);

export const changePageSize = createAction(
  '[Allocation Job Status] Change the page size of results',
  props<SearchAllocationJobActionPayload>()
);

export const sortResults = createAction(
  '[Allocation Job Status] Sort results by column',
  props<SearchAllocationJobActionPayload>()
);

export const allocationJobsLoaded = createAction(
  '[Allocation Job Status] AllocationJob results and pagination info loaded',
  props<{
    results: AllocationJob[];
    pagination: Pagination;
    sortParameters: SortParameters;
    criteria: AllocationJobSearchCriteria;
  }>()
);

export const navigateToTask = createAction(
  '[Allocation Job Status] Navigate to task',
  props<{ requestIdentifier: string }>()
);

export const cancelPendingAllocationById = createAction(
  '[Allocation Job Status] Cancel pending allocation job by ID',
  props<{ jobId: number }>()
);

export const cancelPendingAllocationByIdSuccess = createAction(
  '[Allocation Job Status] Cancel pending allocation job by ID Success'
);

export const downloadAllocationReportById = createAction(
  '[Allocation Job Status] Download allocation job report by ID',
  props<{ jobId: number }>()
);

export const clearState = createAction('[Allocation Job Status] Clear state');

export const resetResultsLoaded = createAction(
  '[Allocation Job Status] Set the resultsLoaded flag to false (LOAD mode)'
);
