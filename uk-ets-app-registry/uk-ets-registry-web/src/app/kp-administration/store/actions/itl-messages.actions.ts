import { createAction, props } from '@ngrx/store';
import { Pagination } from '@shared/search/paginator';
import { SortParameters } from '@shared/search/sort/SortParameters';
import {
  MessageSearchCriteria,
  MessageSearchResult,
  SearchActionPayload
} from '@kp-administration/itl-messages/model';

// TODO: Actions that needed for searching messages are exactly the same with
// account,task & transaction list actions. Consider minimizing duplications.

export const loadMessages = createAction(
  '[ITL Message List Effect] Load the ITL Messages and pagination',
  props<SearchActionPayload>()
);

export const searchMessages = createAction(
  '[ITL Message List: Search form: Search button] Search for results',
  props<SearchActionPayload>()
);

export const navigateToPageOfResults = createAction(
  '[ITL Message List Paginator] Select the page of results',
  props<SearchActionPayload>()
);

export const replaySearch = createAction(
  '[ITL Message List resolver] Replay the search by using the stored criteria, page number, page size (LOAD mode)',
  props<SearchActionPayload>()
);

export const navigateToNextPageOfResults = createAction(
  '[ITL Message List Paginator] Select the next page of results',
  props<SearchActionPayload>()
);

export const navigateToPreviousPageOfResults = createAction(
  '[ITL Message List Paginator] Select the previous page of results',
  props<SearchActionPayload>()
);

export const navigateToLastPageOfResults = createAction(
  '[ITL Message List Paginator] Select the last page of results',
  props<SearchActionPayload>()
);

export const navigateToFirstPageOfResults = createAction(
  '[ITL Message List Paginator] Select the first page of results',
  props<SearchActionPayload>()
);

export const changePageSize = createAction(
  '[ITL Message List Paginator] Change the page size of results',
  props<SearchActionPayload>()
);

export const sortResults = createAction(
  '[ITL Message List results sort column] Sort results by column',
  props<SearchActionPayload>()
);

export const messagesLoaded = createAction(
  '[ITL Message List Effect] ITL Messages results and pagination info loaded',
  props<{
    results: MessageSearchResult[];
    pagination: Pagination;
    sortParameters: SortParameters;
    criteria: MessageSearchCriteria;
  }>()
);

export const hideCriteria = createAction(
  '[ITL Message List show/hide button] Set the hideCriteria boolean to false'
);

export const showCriteria = createAction(
  '[ITL Message List show/hide button] Set the hideCriteria boolean to true'
);

export const clearState = createAction(
  '[ITL Message List resolver] Clear the state of ITL Message List (default mode)'
);

export const resetResultsLoaded = createAction(
  '[ITL Message List resolver] Set the resultsLoaded flag to false (LOAD mode)'
);

export const resetSuccess = createAction(
  '[ITL Message List resolver] Send command to clear the success message'
);

export const clearSuccess = createAction(
  '[ITL Message List Effect] Clear the success message'
);
