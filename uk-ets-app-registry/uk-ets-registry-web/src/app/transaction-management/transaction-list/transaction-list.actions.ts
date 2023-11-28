import { createAction, props } from '@ngrx/store';
import { Pagination } from '@shared/search/paginator';
import { SortParameters } from 'src/app/shared/search/sort/SortParameters';
import { SearchActionPayload } from './transaction-list.model';
import {
  Transaction,
  TransactionSearchCriteria,
} from '@shared/model/transaction';

export const loadTransactions = createAction(
  '[Transaction list Effect] Load the Transactions and pagination',
  props<SearchActionPayload>()
);

export const searchTransactions = createAction(
  '[Transaction List: Search form: Search button] Search for results',
  props<SearchActionPayload>()
);

export const navigateToPageOfResults = createAction(
  '[Transaction List Paginator] Select the page of results',
  props<SearchActionPayload>()
);

export const replaySearch = createAction(
  '[Transaction List resolver] Replay the search by using the stored criteria, page number, page size (LOAD mode)',
  props<SearchActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  '[Transaction List Paginator] Select the next page of results',
  props<SearchActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  '[Transaction List Paginator] Select the previous page of results',
  props<SearchActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  '[Transaction List Paginator] Select the last page of results',
  props<SearchActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  '[Transaction List Paginator] Select the first page of results',
  props<SearchActionPayload>()
);

export const changePageSize = createAction(
  '[Transaction List Paginator] Change the page size of results',
  props<SearchActionPayload>()
);

export const sortResults = createAction(
  '[Transaction List results sort column] Sort results by column',
  props<SearchActionPayload>()
);

export const transactionsLoaded = createAction(
  '[Transaction list Effect] Transaction results and pagination info loaded',
  props<{
    results: Transaction[];
    pagination: Pagination;
    sortParameters: SortParameters;
    criteria: TransactionSearchCriteria;
  }>()
);

export const hideCriteria = createAction(
  '[Transaction List show/hide button] Set the hideCriteria boolean to false'
);

export const showCriteria = createAction(
  '[Transaction List show/hide button] Set the hideCriteria boolean to true'
);

export const showAdvancedSearch = createAction(
  '[Task List advanced search button] Set the advanced search visible or hidden',
  props<{ isVisible: boolean }>()
);

export const clearState = createAction(
  '[Transaction list resolver] Clear the state of Transaction List (default mode)'
);

export const resetResultsLoaded = createAction(
  '[Transaction list resolver] Set the resultsLoaded flag to false (LOAD mode)'
);

export const resetSuccess = createAction(
  '[Transaction list resolver] Send command to clear the success message'
);

export const clearSuccess = createAction(
  '[Transaction list Effect] Clear the success message'
);
